package com.vipkid.recruitment.contractinfo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.util.Maps;
import com.google.common.base.Preconditions;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.file.model.AppLifePicture;
import com.vipkid.file.model.FileUploadStatus;
import com.vipkid.file.model.FileVo;
import com.vipkid.file.service.AwsFileService;
import com.vipkid.http.service.FileHttpService;
import com.vipkid.recruitment.common.service.RecruitmentService;
import com.vipkid.recruitment.contractinfo.service.ContractService;
import com.vipkid.recruitment.entity.ContractFile;
import com.vipkid.recruitment.entity.TeacherOtherDegrees;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.recruitment.contractinfo.service.ContractInfoService;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;
import com.vipkid.trpm.entity.TeacherTaxpayerFormDetail;
import com.vipkid.trpm.service.portal.TeacherTaxpayerFormService;
import com.vipkid.trpm.util.AwsFileUtils;

/**
 * ContractInfo -> Regular
 *
 * LifeCycle: ContractInfo
 * 上传头像, 生活照, 介绍视频, 和自我介绍
 *
 * @author Austin.Cao  Date: 18/11/2016
 */
@RestController
@RestInterface(lifeCycle={TeacherEnum.LifeCycle.CONTRACT_INFO})
@RequestMapping("/recruitment/contractinfo")
public class ContractInfoController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(ContractInfoController.class);

    @Autowired
    private AwsFileService awsFileService;

    @Autowired
    private FileHttpService fileHttpService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractInfoService contractInfoService;

    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private TeacherTaxpayerFormService teacherTaxpayerFormService;


    /**
     * 进入 PersonalInfo 状态之前, 先查出之前是否有上传资料
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryPersonalInfo", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> queryPersonalInfo(HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> result = Maps.newHashMap();
        try {
            Teacher teacher = getTeacher(request);
            if (null == teacher) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return ResponseUtils.responseFail("Teacher doesn't exist", this);
            }
            Long teacherId = teacher.getId();
            //从 TIS 获取数据, 检查老师的头像/照片/视频是都都上传了?

            Map<String, Object> teacherFiles = fileHttpService.queryTeacherFiles(teacherId);
            String avatarUrl = (String) teacherFiles.get("avatarUrl");
            List<AppLifePicture> lifePictures = (List<AppLifePicture>) teacherFiles.get("lifePictures");
            String shortVideoUrl = (String) teacherFiles.get("shortVideo");
            Integer shortVideoStatus = (Integer) teacherFiles.get("shortVideoStatus");

            FileUploadStatus fileUploadStatus = new FileUploadStatus();
            if(shortVideoStatus != null && shortVideoUrl != null) {
                fileUploadStatus.setStatus(shortVideoStatus);
                fileUploadStatus.setUrl(shortVideoUrl);
            }
            result.put("avatar", avatarUrl);
            result.put("video", fileUploadStatus);
            result.put("lifePics", lifePictures);

            Map<String,Object> status = recruitmentService.getStatus(teacherId);
            if(status != null && status.size() > 0) {
                String failedReasonJson = (String) status.get("failedReason");
                result.put("failedReason", failedReasonJson);
            }
            result.put("bio", teacher.getIntroduction());

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }

        return ResponseUtils.responseSuccess(result);
    }

    /**
     * 提交用户上传文件的信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public  Map<String,Object> submitsContractInfo(@RequestBody Map<String,Object> paramMap, HttpServletRequest request, HttpServletResponse response){
        String fileIds = (String) paramMap.get("id");
        String bio = (String) paramMap.get("bio");

        if(StringUtils.isBlank(fileIds)){
            return ResponseUtils.responseFail("You don't have to upload the file", this);
        }

        //ID (3 TYPES), DIPLOMA, DEGREE, CERTIFICATE, W9, CONTRACT,
        List<String> idLists = Splitter.on(",").splitToList(fileIds);
        List<Integer> idList = new ArrayList<>();
        for(int i=0;i<idLists.size();i++){
            idList.add(Integer.parseInt(idLists.get(i)));
        }
        try {
            Teacher teacher = getTeacher(request);
            Long teacherId = teacher.getId();

            if (null == teacher) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return ResponseUtils.responseFail("Teacher doesn't exist", this);
            }

            //check contract files
            boolean isContractFileValid = checkContractFile(teacherId,idList);
            if(!isContractFileValid) {
                return ResponseUtils.responseFail("Teacher's contract files do NOT exists, failed to submit", this);

            }
            //check personal info
            boolean isPersonalInfoValid = checkPersonInfo(teacherId);
            if(!isPersonalInfoValid) {
                return ResponseUtils.responseFail("Teacher's personal files do NOT exists, failed to update bio", this);
            }

            //update teacher's bio
            teacher.setIntroduction(bio);
            boolean bioUpdated = contractInfoService.updateTeacher(teacher);
            if(!bioUpdated) {
                return ResponseUtils.responseFail("Failed to submit teacher bio", this);
            }

            boolean result = contractService.updateTeacherApplication(teacher,idList);
            if(result){
                return ResponseUtils.responseSuccess();
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }

        return ResponseUtils.responseFail("Failed to submitsContractInfo!", this);
    }

    private boolean checkPersonInfo (Long teacherId) {
        //检查老师的头像/照片/视频是都都上传了? 从 TIS 获取数据
        Map<String, Object> teacherFiles = Maps.newHashMap();
        teacherFiles = fileHttpService.queryTeacherFiles(teacherId);
        String avatarUrl = (String) teacherFiles.get("avatarUrl");
        String lifePictures = (String) teacherFiles.get("lifePictures");
        String shortVideoUrl = (String) teacherFiles.get("shortVideo");
        String shortVideoStatus = (String) teacherFiles.get("shortVideoStatus");

        if (StringUtils.isEmpty(avatarUrl) || StringUtils.isEmpty(lifePictures)
                || StringUtils.isEmpty(shortVideoUrl) || StringUtils.isEmpty(shortVideoStatus)) {
            logger.warn("Teacher's files do NOT exists, failed to update bio");
            return false;
        }

        return true;
    }

    private boolean checkContractFile(Long teacherId,List<Integer> fileIds) {
        boolean isFileValid = false;
        List<TeacherOtherDegrees> files= contractService.findTeacherOtherDegrees(teacherId);
        List<Integer> idList = Lists.transform(files, new Function<TeacherOtherDegrees, Integer>() {
            @Nullable
            @Override
            public Integer apply(TeacherOtherDegrees input) {
                return input.getId();
            }
        });

        if(CollectionUtils.isNotEmpty(files)) {
            boolean hasIdCard = false;
            boolean hasDiploma = false;
            boolean hasContract = false;
            for (TeacherOtherDegrees file : files) {
                //TODO for zhaojun, add Enum
                if (file.getFileType() == 3) {
                    hasIdCard = true;
                }
                if (file.getFileType() == 4) {
                    hasDiploma = true;
                }
                if (file.getFileType() == 5) {
                    hasContract = true;
                }
                idList.add(file.getId());
            }

            if (hasIdCard && hasDiploma && hasContract && idList.containsAll(fileIds)) {
                isFileValid = true;
            }
        }

        return isFileValid;
    }


    @ResponseBody
    @RequestMapping("/uploadAvatar")
    public Object uploadAvatar(@RequestParam("file") MultipartFile file,
                               HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> result = Maps.newHashMap();
        if (file != null) {
            String fileName = file.getOriginalFilename();
            String bucketName = PropertyConfigurer.stringValue("aws.bucketName");
            String key = AwsFileUtils.getAvatarKey(fileName);
            Long fileSize = file.getSize();

            Preconditions.checkArgument(AwsFileUtils.checkAvatarFileType(fileName), "文件类型不正确，支持类型为" + AwsFileUtils.AVATAR_FILE_TYPE);
            Preconditions.checkArgument(AwsFileUtils.checkAvatarFileSize(fileSize), "文件太大，maxSize = " + AwsFileUtils.AVATAR_MAX_SIZE);

            try {
                Teacher teacher = getTeacher(request);
                if (null == teacher) {
                    response.setStatus(HttpStatus.NOT_FOUND.value());
                    return ResponseUtils.responseFail("Teacher doesn't exist", this);
                }
                Long teacherId = teacher.getId();

                FileVo fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), fileSize);

                if (fileVo != null) {
                    String url = "http://" + bucketName + "/" + key;
                    FileUploadStatus fileUploadStatus = fileHttpService.uploadAvatar(teacherId, key);
                    result.put("url", fileUploadStatus.getUrl());
                    result.put("status", fileUploadStatus.getStatus());
                    return ResponseUtils.responseSuccess(result);
                } else {
                    return ResponseUtils.responseFail("Failed to upload avatar to AWS.", this);
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail(e.getMessage(), this);
            } catch (Exception e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ResponseUtils.responseFail(e.getMessage(), this);
            }
        }

        return ResponseUtils.responseFail("Failed to upload avatar!", this);
    }

    @ResponseBody
    @RequestMapping("/uploadLifePic")
    public Object uploadLifePic(@RequestParam("file") MultipartFile file,
                                HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> result = Maps.newHashMap();
        if (file != null) {
            String fileName = file.getOriginalFilename();
            String bucketName = PropertyConfigurer.stringValue("aws.bucketName");
            String key = AwsFileUtils.getLifePictureKey(fileName);
            Long fileSize = file.getSize();

            Preconditions.checkArgument(AwsFileUtils.checkLifePicFileType(fileName), "文件类型不正确，支持类型为" + AwsFileUtils.LIFE_PICTURE_FILE_TYPE);
            Preconditions.checkArgument(AwsFileUtils.checkLifePicFileSize(fileSize), "文件太大，maxSize = " + AwsFileUtils.LIFE_PICTURE_MAX_SIZE);

            try {
                Teacher teacher = getTeacher(request);
                if (null == teacher) {
                    response.setStatus(HttpStatus.NOT_FOUND.value());
                    return ResponseUtils.responseFail("Teacher doesn't exist", this);
                }
                Long teacherId = teacher.getId();

                FileVo fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), fileSize);

                if (fileVo != null) {
                    String url = "http://" + bucketName + "/" + key;
                    FileUploadStatus fileUploadStatus = fileHttpService.uploadLifePicture(teacherId, key);
                    result.put("url", fileUploadStatus.getUrl());
                    result.put("status", fileUploadStatus.getStatus());
                    return ResponseUtils.responseSuccess(result);
                } else {
                    return ResponseUtils.responseFail("Failed to upload life picture to AWS.", this);
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail(e.getMessage(), this);
            } catch (Exception e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ResponseUtils.responseFail(e.getMessage(), this);
            }
        }

        return ResponseUtils.responseFail("Failed to upload life picture!", this);
    }

    @ResponseBody
    @RequestMapping("/uploadVideo")
    public Object uploadVideo(@RequestParam("file") MultipartFile file,
                              HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> result = Maps.newHashMap();
        if (file != null) {
            String fileName = file.getOriginalFilename();
            String bucketName = PropertyConfigurer.stringValue("aws.bucketName");
            String key = AwsFileUtils.getShortVideoKey(fileName);
            Long fileSize = file.getSize();

            Preconditions.checkArgument(AwsFileUtils.checkShortVideoFileType(fileName), "文件类型不正确，支持类型为" + AwsFileUtils.SHORT_VIDEO_FILE_TYPE);
            Preconditions.checkArgument(AwsFileUtils.checkShortVideoFileSize(fileSize), "文件太大，maxSize = " + AwsFileUtils.SHORT_VIDEO_MAX_SIZE);

            try {
                Teacher teacher = getTeacher(request);
                if (null == teacher) {
                    response.setStatus(HttpStatus.NOT_FOUND.value());
                    return ResponseUtils.responseFail("Teacher doesn't exist", this);
                }
                Long teacherId = teacher.getId();

                FileVo fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), fileSize);

                if (fileVo != null) {
                    String url = "http://" + bucketName + "/" + key;
                    FileUploadStatus fileUploadStatus = fileHttpService.uploadShortVideo(teacherId, key);
                    result.put("url", fileUploadStatus.getUrl());
                    result.put("status", fileUploadStatus.getStatus());
                    return ResponseUtils.responseSuccess(result);
                } else {
                    return ResponseUtils.responseFail("Failed to upload short video to AWS.", this);
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail(e.getMessage(), this);
            } catch (Exception e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ResponseUtils.responseFail(e.getMessage(), this);
            }
        }

        return ResponseUtils.responseFail("Failed to short video picture!", this);
    }

    @ResponseBody
    @RequestMapping("/deleteAvatar")
    public Object deleteAvatar(HttpServletRequest request, HttpServletResponse response) {

        Teacher teacher = getTeacher(request);
        if (null == teacher) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return ResponseUtils.responseFail("Teacher doesn't exist", this);
        }
        Long teacherId = teacher.getId();

        try {
            boolean ret = fileHttpService.deleteAvatar(teacherId);
            if (ret) {
                return ResponseUtils.responseSuccess("Successful to delete avatar.", null);
            } else {
                return ResponseUtils.responseFail("Failed to delete avatar.", this);
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }

    @ResponseBody
    @RequestMapping("/deleteLifePic")
    public Object deleteAvatar(Long lifePicId, HttpServletRequest request, HttpServletResponse response) {

        Teacher teacher = getTeacher(request);
        if (null == teacher) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return ResponseUtils.responseFail("Teacher doesn't exist", this);
        }
        Long teacherId = teacher.getId();

        try {
            boolean ret = fileHttpService.deleteLifePicture(teacherId, lifePicId);
            if (ret) {
                return ResponseUtils.responseSuccess("Successful to delete life picture.", null);
            } else {
                return ResponseUtils.responseFail("Failed to delete life picture.", this);
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }

    @ResponseBody
    @RequestMapping("/deleteVideo")
    public Object deleteVideo(HttpServletRequest request, HttpServletResponse response) {

        Teacher teacher = getTeacher(request);
        if (null == teacher) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return ResponseUtils.responseFail("Teacher doesn't exist", this);
        }
        Long teacherId = teacher.getId();

        try {
            boolean ret = fileHttpService.deleteShortVideo(teacherId);
            if (ret) {
                return ResponseUtils.responseSuccess("Successful to delete short video.", null);
            } else {
                return ResponseUtils.responseFail("Failed to delete short video.", this);
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }

    @RequestMapping(value = "/toRegular", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> toRegular(HttpServletRequest request, HttpServletResponse response){
        try{
            Teacher teacher = getTeacher(request);
            logger.info("Teacher:{} toPublic",teacher.getId());
            Map<String,Object> result = this.contractInfoService.toRegular(teacher);
            if(ResponseUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }

    /**
     * =============================================== Contract 想关接口 =====================================================
     */

    /**
     *
     * 删除文件
     * @param pramMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/deleteFile")
    public Map<String,Object> deleteFile(@RequestBody Map<String,Object> pramMap,HttpServletRequest request, HttpServletResponse response){
        Object id = pramMap.get("id");
        int fileId =Integer.parseInt(String.valueOf(id));
        Teacher teacher = getTeacher(request);
        try{
            logger.info("删除文件id........:{}",fileId);
            Map<String,Object> result = contractService.reomteFile(fileId,teacher);
            if(ResponseUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }

    /**
     * 文件上传功能
     * @param file
     * @param teacherId
     * @return
     */
    public FileVo AwsUpload(MultipartFile file, Long teacherId) {
        logger.info("teacher id = {} ", teacherId);
        FileVo fileVo = null;
        if (file != null) {
            String name = file.getOriginalFilename();
            String bucketName = PropertyConfigurer.stringValue("aws.bucketName");

            teacherId = teacherId == null ? 0 : teacherId;
            String key = AwsFileUtils.getTaxpayerkey(teacherId + "-" + name);
            Long size = file.getSize();

            Preconditions.checkArgument(AwsFileUtils.checkTaxPayerFileType(name), "文件类型不正确，支持类型为" + AwsFileUtils.TAPXPAYER_FILE_TYPE);
            Preconditions.checkArgument(AwsFileUtils.checkTaxPayerFileSize(size), "文件太大，maxSize = " + AwsFileUtils.TAPXPAYER_FILE_MAX_SIZE);

            try {
                logger.info("文件:{}上传",name);
                fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), file.getSize());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fileVo != null) {
                String url = "http://" + bucketName + "/" + key;
                fileVo.setUrl(url);
            }
        }
        return fileVo;
    }


    /**
     * 上传老师的身份证明
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/uploadIdentification")
    public Map<String,Object> uploadIdentification(@RequestParam("file") MultipartFile file,String filetype,HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = new HashMap<String,Object>();
        if(filetype.equals("")||filetype==null){
            return ResponseUtils.responseFail("There is no type of file upload", this);
        }
        Teacher teacher = new Teacher().setId(getTeacher(request).getId());
        logger.info("用户：{}，upload Identification file = {}",teacher.getId(),file);

        FileVo fileVo  = AwsUpload(file,teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail("upload file is fail",this);
        }

        try{
            TeacherOtherDegrees teacherOtherDegrees = new TeacherOtherDegrees();
            teacherOtherDegrees.setTeacherId(teacher.getId());
            teacherOtherDegrees.setUrl(fileVo.getUrl());
            //文件类型1-other_degrees  2-certificationFiles   3-Identification  4-Diploma 5-Contract  6-Passport   7-Driver's license
            if(filetype.equals("passport")){
                teacherOtherDegrees.setFileType(6);
            }
            if(filetype.equals("driver")){
                teacherOtherDegrees.setFileType(7);
            }
            if(filetype.equals("identity")){
                teacherOtherDegrees.setFileType(3);
            }

            contractService.save(teacherOtherDegrees);
            result.put("file",fileVo.getUrl());
            result.put("status",true);
            result.put("id",teacherOtherDegrees.getId());
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }


    /**
     * 上传老师的最高学历
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/uploadDiploma")
    public Map<String,Object> uploadDiploma(@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response){

        Map<String,Object> result = new HashMap<String,Object>();

        Teacher teacher = new Teacher().setId(getTeacher(request).getId());
        logger.info("用户 :{},upload uploadDiploma file = {}",teacher.getId(),file);
        FileVo fileVo  = AwsUpload(file,teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail("upload file is fail",this);
        }

        try{
            TeacherOtherDegrees teacherOtherDegrees = new TeacherOtherDegrees();
            teacherOtherDegrees.setTeacherId(teacher.getId());
            teacherOtherDegrees.setUrl(fileVo.getUrl());
            teacherOtherDegrees.setFileType(4);
            contractService.save(teacherOtherDegrees);
            result.put("file",fileVo.getUrl());
            result.put("status",true);
            result.put("id",teacherOtherDegrees.getId());
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }


    /**
     * 上传老师的合同
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/uploadContract")
    public Map<String,Object> uploadContract(@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response){

        Map<String,Object> result = new HashMap<String,Object>();

        Teacher teacher = new Teacher().setId(getTeacher(request).getId());
        logger.info("用户 :{},upload uploadContract file = {}",teacher.getId(),file);
        FileVo fileVo  = AwsUpload(file,teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail("upload file is fail",this);
        }

        try{
            TeacherOtherDegrees teacherOtherDegrees = new TeacherOtherDegrees();
            teacherOtherDegrees.setTeacherId(teacher.getId());
            teacherOtherDegrees.setUrl(fileVo.getUrl());
            teacherOtherDegrees.setFileType(5);
            contractService.save(teacherOtherDegrees);

            result.put("file",fileVo.getUrl());
            result.put("status",true);
            result.put("id",teacherOtherDegrees.getId());
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }


    /**
     * 上传W9-TAX文件
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/uploadW9Tax")
    public Map<String,Object> uploadW9Tax(@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response){

        Map<String,Object> result = new HashMap<String,Object>();

        Teacher teacher = new Teacher().setId(getTeacher(request).getId());
        logger.info("用户 :{},upload uploadW9Tax file = {}",teacher.getId(),file);
        FileVo fileVo  = AwsUpload(file,teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail("upload file is fail",this);
        }

        try{
            //更新w9-tax
            TeacherTaxpayerForm teacherTaxpayerForm = new TeacherTaxpayerForm();
            logger.info("保存用户：{}上传的合W9-TAX文件url",teacher.getId());
            teacherTaxpayerForm.setTeacherId(teacher.getId());
            teacherTaxpayerForm.setUrl(fileVo.getUrl());
            teacherTaxpayerForm.setFormType(TeacherEnum.FormType.W9.val());
            setTeacherTaxpayerFormInfo(teacherTaxpayerForm, request);
            teacherTaxpayerFormService.saveTeacherTaxpayerForm(teacherTaxpayerForm );

            TeacherOtherDegrees teacherOtherDegrees = new TeacherOtherDegrees();
            logger.info("保存用户：{}上传的合W9-TAX文件url到teacher_other_degrees",teacher.getId());
            teacherOtherDegrees.setTeacherId(teacher.getId());
            teacherOtherDegrees.setUrl(fileVo.getUrl());
            teacherOtherDegrees.setFileType(8);
            contractService.save(teacherOtherDegrees);

            result.put("file",fileVo.getUrl());
            result.put("id",teacherOtherDegrees.getId());
            result.put("status",true);
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }





    /**
     * 上传Certification文件
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/uploadCertification ")
    public Map<String,Object> uploadCertification (@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response){

        Map<String,Object> result = new HashMap<String,Object>();

        Teacher teacher = new Teacher().setId(getTeacher(request).getId());
        logger.info("用户 :{},upload uploadCertification file = {}",teacher.getId(),file);
        FileVo fileVo  = AwsUpload(file,teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail("upload file is fail",this);
        }
        try{
            TeacherOtherDegrees teacherOtherDegrees = new TeacherOtherDegrees();
            teacherOtherDegrees.setTeacherId(teacher.getId());
            teacherOtherDegrees.setUrl(fileVo.getUrl());
            teacherOtherDegrees.setFileType(2);
            contractService.save(teacherOtherDegrees);
            result.put("file",fileVo.getUrl());
            result.put("status",true);
            result.put("id",teacherOtherDegrees.getId());
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }

    /**
     * 上传Degrees文件
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/uploadDegrees ")
    public Map<String,Object> uploadDegrees (@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response){

        Map<String,Object> result = new HashMap<String,Object>();

        Teacher teacher = new Teacher().setId(getTeacher(request).getId());
        logger.info("用户 :{},upload uploadDegrees file = {}",teacher.getId(),file);
        FileVo fileVo  = AwsUpload(file,teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail("upload file is fail",this);
        }
        try{
            TeacherOtherDegrees teacherOtherDegrees = new TeacherOtherDegrees();
            teacherOtherDegrees.setTeacherId(teacher.getId());
            teacherOtherDegrees.setUrl(fileVo.getUrl());
            teacherOtherDegrees.setFileType(1);
            contractService.save(teacherOtherDegrees);
            result.put("file",fileVo.getUrl());
            result.put("status",true);
            result.put("id",teacherOtherDegrees.getId());
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }



    /**
     * 查询用户所提交的文件URL
     * @param request
     * @param response
     * @return
     */

    @RequestMapping(value = "/contract", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object>  contract(HttpServletRequest request,HttpServletResponse response){
        Teacher teacher = getTeacher(request);
        Map<String,Object> result = new HashMap<String,Object>();
        logger.info("保存用户：{}查询上传过的文件",teacher.getId());
        try {
            Map<String, ContractFile> map = contractService.findContract(teacher);
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (java.util.Map.Entry) it.next();
                String re = (String) entry.getKey();
                ContractFile contractFile = (ContractFile) entry.getValue();
                logger.info("保存用户：{}查询上传过的文件{}", teacher.getId(), contractFile);
                result.put("file",contractFile);
                result.put("result", re);
                result.put("status", true);
            }
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }

    public void setTeacherTaxpayerFormInfo(TeacherTaxpayerForm teacherTaxpayerForm, HttpServletRequest request){
        Teacher teacher = getTeacher(request);
        Long teacherId = teacher.getId();
        String teacherName = teacher.getRealName();

        teacherTaxpayerForm.setTeacherId(teacherId);
        teacherTaxpayerForm.setTeacherName(teacherName);
        teacherTaxpayerForm.setUploader(teacherId);
        teacherTaxpayerForm.setUploadTime(new Date());

        teacherTaxpayerForm.setIsNew(TeacherEnum.ISNew.NEW.val());
        teacherTaxpayerForm.setUploaded(TeacherEnum.UploadStatus.UPLOADED.val());
        teacherTaxpayerForm.setCreateBy(teacherId);
        teacherTaxpayerForm.setUpdateBy(teacherId);

        TeacherTaxpayerFormDetail detail = new TeacherTaxpayerFormDetail();
        teacherTaxpayerForm.setTeacherTaxpayerFormDetail(detail);

        detail.setUploaderName(teacherName);

    }

}
