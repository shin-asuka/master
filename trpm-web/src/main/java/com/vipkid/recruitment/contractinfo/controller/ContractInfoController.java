package com.vipkid.recruitment.contractinfo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Splitter;
import com.vipkid.enums.TeacherApplicationEnum;

import com.vipkid.recruitment.entity.TeacherApplication;
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
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.recruitment.common.service.RecruitmentService;
import com.vipkid.recruitment.contractinfo.service.ContractInfoService;
import com.vipkid.recruitment.entity.TeacherContractFile;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
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
    private ContractInfoService contractInfoService;

    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private TeacherTaxpayerFormService teacherTaxpayerFormService;


    /**
     * 进入 PersonalInfo 状态之前, 先查出之前是否有上传资料
     */
    @RequestMapping(value = "/queryContractInfo", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> queryPersonalInfo(HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> result = Maps.newHashMap();

        try {
            Teacher teacher = getTeacher(request);
            if (null == teacher) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return ReturnMapUtils.returnFail("Teacher doesn't exist");
            }
            Long teacherId = teacher.getId();

            //1. 获取老师上传的 person info,  是否有 audit failReason
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
            Map<String, Object> personalInfo = Maps.newHashMap();

            personalInfo.put("avatar", avatarUrl);
            personalInfo.put("video", fileUploadStatus);
            personalInfo.put("lifePics", lifePictures);

            Map<String,Object> status = recruitmentService.getStatus(teacher);
            if(status != null && status.size() > 0) {
                String failedReasonJson = (String) status.get("failedReason");
                personalInfo.put("failedReason", failedReasonJson);
            }
            personalInfo.put("bio", teacher.getIntroduction());

            //2. 获取老师上传的 contract info,  , 是否有 audit failReason
            Map<String, Object> contractInfo = Maps.newHashMap();
            Map<String, Object> contractFileMap = contractInfoService.findContract(teacher);
            logger.info("查询用户：{},查询上传过的文件", teacher.getId());
            contractInfo.put("file",contractFileMap.get("contractFile"));
            contractInfo.put("result", contractFileMap.get("result"));
            result.put("personalInfo", personalInfo);
            result.put("contractInfo", contractInfo);

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            logger.error("queryContractInfo with IllegalArgumentException", e);
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            logger.error("queryContractInfo with Exception", e);
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
        logger.info("Successful query contract info {}", JsonUtils.toJSONString(result));
        return ReturnMapUtils.returnSuccess(result);
    }



    /**
     * 提交用户上传文件的信息
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public  Map<String,Object> submitContractInfo(@RequestBody Map<String,Object> paramMap, HttpServletRequest request, HttpServletResponse response){
        String fileIds = (String) paramMap.get("id");
        String bio = (String) paramMap.get("bio");

        logger.info("uplod file id String {},techer 的自我简介{}",fileIds,bio);
        if(StringUtils.isBlank(fileIds)||StringUtils.isBlank(bio)){
            return ReturnMapUtils.returnFail("You don't have to upload the file");
        }

        //ID (3 TYPES), DIPLOMA, DEGREE, CERTIFICATE, W9, CONTRACT,
        List<String> idLists = Splitter.on(",").splitToList(fileIds);
        List<Integer> idList = new ArrayList<>();

        try {
            for(int i=0;i<idLists.size();i++){
                idList.add(Integer.parseInt(idLists.get(i)));
            }
            Teacher teacher = getTeacher(request);

            Long teacherId = teacher.getId();
            boolean submit = checkManySubmit(teacherId);
            if(submit){
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return ReturnMapUtils.returnFail("The teacher has been submitted. ");
            }

            if (null == teacher) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return ReturnMapUtils.returnFail("Teacher doesn't exist");
            }

            //check contract files
            boolean isContractFileValid = checkContractFile(teacherId,idList);
            if(!isContractFileValid) {
                return ReturnMapUtils.returnFail("Teacher's contract files do NOT exists, failed to submit");

            }
            logger.info("Check Teacher 的 file id{}",idList);
            //check personal info
            boolean isPersonalInfoValid = checkPersonInfo(teacherId);
            if(!isPersonalInfoValid) {
                return ReturnMapUtils.returnFail("Teacher's personal files do NOT exists, failed to update bio");
            }

            logger.info("update Teacher 的自我简介{}",bio);
            teacher.setIntroduction(bio);
            boolean bioUpdated = contractInfoService.updateTeacher(teacher);
            if(!bioUpdated) {
                return ReturnMapUtils.returnFail("Failed to submit teacher bio");
            }

            boolean result = contractInfoService.updateTeacherApplication(teacher,idList);
            if(!result){
                logger.error("Failed to submit contract info!");
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ReturnMapUtils.returnFail("Failed to submitsContractInfo!");
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            logger.error("submitContractInfo with IllegalArgumentException", e);
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            logger.error("submitContractInfo with Exception", e);
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
        logger.info("Successful submit contract info!");
        return ReturnMapUtils.returnSuccess();
    }

    //检查用户是否重复提交
    private boolean checkManySubmit(Long teacherId){
        List<TeacherApplication> teacherApplications =  contractInfoService.findTeacherApplication(teacherId);
        if(CollectionUtils.isNotEmpty(teacherApplications)){
            TeacherApplication teacherApplication = teacherApplications.get(0);
            if(StringUtils.isBlank(teacherApplication.getResult())){
                return true;
            }
        }

        return false;
    }


    private boolean checkPersonInfo (Long teacherId) {
        logger.info("检查老师的头像/照片/视频是都都上传了? 从 TIS 获取数据");
        //检查老师的头像/照片/视频是都都上传了? 从 TIS 获取数据
        Map<String, Object> teacherFiles = fileHttpService.queryTeacherFiles(teacherId);
        String avatarUrl = (String) teacherFiles.get("avatarUrl");
        List<AppLifePicture> lifePictures = (List<AppLifePicture>)teacherFiles.get("lifePictures");
        String shortVideoUrl = (String) teacherFiles.get("shortVideo");
        Integer shortVideoStatus = (Integer) teacherFiles.get("shortVideoStatus");

        if (StringUtils.isEmpty(avatarUrl) || CollectionUtils.isEmpty(lifePictures)
                || StringUtils.isEmpty(shortVideoUrl) || shortVideoStatus==null) {
            logger.warn("Teacher's files do NOT exists, failed to update bio");
            return false;
        }

        return true;
    }

    private boolean checkContractFile(Long teacherId,List<Integer> fileIds) {
        logger.info("Teacher:{} 检查文件id是否合格",teacherId);
        boolean isFileValid = false;
        List<TeacherContractFile> files= contractInfoService.findTeacherContractFile(teacherId);
        List<Integer> idList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(files)) {
            boolean hasIdCard = false;
            boolean hasDiploma = false;
            boolean hasContract = false;
            for (TeacherContractFile file : files) {
                if (file.getFileType() == TeacherApplicationEnum.ContractFileType.IDENTIFICATION.val()
                        ||file.getFileType() == TeacherApplicationEnum.ContractFileType.PASSPORT.val()
                        ||file.getFileType() == TeacherApplicationEnum.ContractFileType.DRIVER.val()) {
                    hasIdCard = true;
                }
                if (file.getFileType() == TeacherApplicationEnum.ContractFileType.DIPLOMA.val()) {
                    hasDiploma = true;
                }
                if (file.getFileType() == TeacherApplicationEnum.ContractFileType.CONTRACT.val()) {
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
                    return ReturnMapUtils.returnFail("Teacher doesn't exist");
                }
                Long teacherId = teacher.getId();

                FileVo fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), fileSize);

                if (fileVo != null) {
                    FileUploadStatus fileUploadStatus = fileHttpService.uploadAvatar(teacherId, key);
                    result.put("url", fileUploadStatus.getUrl());
                    //result.put("status", fileUploadStatus.getStatus());
                    logger.info("Successful to upload avatar!");
                    return ReturnMapUtils.returnSuccess(result);
                } else {
                    logger.error("Failed to upload avatar!");
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    return ReturnMapUtils.returnFail("Failed to upload avatar to AWS.");
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.error("submitContractInfo with Exception", e);
                return ReturnMapUtils.returnFail(e.getMessage(),e);
            } catch (Exception e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                logger.error("submitContractInfo with Exception", e);
                return ReturnMapUtils.returnFail(e.getMessage(),e);
            }
        }
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ReturnMapUtils.returnFail("Failed to upload avatar!");
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
                    return ReturnMapUtils.returnFail("Teacher doesn't exist");
                }
                Long teacherId = teacher.getId();

                FileVo fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), fileSize);

                if (fileVo != null) {
                    FileUploadStatus fileUploadStatus = fileHttpService.uploadLifePicture(teacherId, key);
                    result.put("url", fileUploadStatus.getUrl());
                    //result.put("status", fileUploadStatus.getStatus());
                    logger.info("Successful to upload uploadLifePic: {}", JsonUtils.toJSONString(result));
                    return ReturnMapUtils.returnSuccess(result);
                } else {
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    return ReturnMapUtils.returnFail("Failed to upload life picture to AWS.");
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(e.getMessage(),e);
            } catch (Exception e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ReturnMapUtils.returnFail(e.getMessage(),e);
            }
        }
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ReturnMapUtils.returnFail("Failed to upload life picture!");
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
                    return ReturnMapUtils.returnFail("Teacher doesn't exist");
                }
                Long teacherId = teacher.getId();

                FileVo fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), fileSize);

                if (fileVo != null) {
                    FileUploadStatus fileUploadStatus = fileHttpService.uploadShortVideo(teacherId, key);
                    result.put("url", fileUploadStatus.getUrl());
                    //result.put("status", fileUploadStatus.getStatus());
                    return ReturnMapUtils.returnSuccess(result);
                } else {
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    return ReturnMapUtils.returnFail("Failed to upload short video to AWS.");
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.error("uploadVideo exception", e);
                return ReturnMapUtils.returnFail(e.getMessage(),e);
            } catch (Exception e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                logger.error("uploadVideo exception", e);
                return ReturnMapUtils.returnFail(e.getMessage(),e);
            }
        }

        return ReturnMapUtils.returnFail("Failed to short video picture!");
    }

    @ResponseBody
    @RequestMapping("/deleteAvatar")
    public Object deleteAvatar(HttpServletRequest request, HttpServletResponse response) {

        Teacher teacher = getTeacher(request);
        if (null == teacher) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return ReturnMapUtils.returnFail("Teacher doesn't exist");
        }
        Long teacherId = teacher.getId();

        try {
            boolean ret = fileHttpService.deleteAvatar(teacherId);
            if (ret) {
                logger.info("Successful to delete avatar!");
                return ReturnMapUtils.returnSuccess("Successful to delete avatar.", null);
            } else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ReturnMapUtils.returnFail("Failed to delete avatar.");
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            logger.error("deleteAvatar exception", e);
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }

    @ResponseBody
    @RequestMapping("/deleteLifePic")
    public Object deleteAvatar(@RequestBody Map<String,Object> paramMap, HttpServletRequest request, HttpServletResponse response) {
        String lifePicId = (String)paramMap.get("lifePicId");
        Long fileId = Long.parseLong(lifePicId);
        Teacher teacher = getTeacher(request);
        if (null == teacher) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return ReturnMapUtils.returnFail("Teacher doesn't exist!");
        }
        Long teacherId = teacher.getId();

        try {
            boolean ret = fileHttpService.deleteLifePicture(teacherId, fileId);
            if (ret) {
                logger.info("Successful to delete life picture!");
                return ReturnMapUtils.returnSuccess("Successful to delete life picture.", null);
            } else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ReturnMapUtils.returnFail("Failed to delete life picture.");
            }
        } catch (Exception e) {
            logger.error("deleteLifePic exception", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(), e);
        }
    }

    @ResponseBody
    @RequestMapping("/deleteVideo")
    public Object deleteVideo(HttpServletRequest request, HttpServletResponse response) {
        Teacher teacher = getTeacher(request);
        if (null == teacher) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return ReturnMapUtils.returnFail("Teacher doesn't exist");
        }
        Long teacherId = teacher.getId();

        try {
            boolean ret = fileHttpService.deleteShortVideo(teacherId);
            if (ret) {
                logger.info("Successful to delete short video!");
                return ReturnMapUtils.returnSuccess("Successful to delete short video.", null);
            } else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ReturnMapUtils.returnFail("Failed to delete short video.");
            }
        } catch (Exception e) {
            logger.error("deleteVideo exception", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }

    @RequestMapping(value = "/toRegular", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> toRegular(HttpServletRequest request, HttpServletResponse response){
        try{
            Teacher teacher = getTeacher(request);
            logger.info("Teacher:{} toPublic", teacher.getId());
            boolean result = this.contractInfoService.toRegular(teacher);
            if(result){
                logger.info("Successfully get TO REGULAR!");
                return ReturnMapUtils.returnSuccess();
            } else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ReturnMapUtils.returnFail("Failed to get to REGULAR!");
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            logger.error("toRegular exception", e);
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            logger.error("toRegular exception", e);
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }

    /*
     * =============================================== Contract 想关接口 =====================================================
     */

    /**

     * 删除文件
     */
    @RequestMapping("/deleteFile")
    public Map<String,Object> deleteFile(@RequestBody Map<String,Object> pramMap,HttpServletRequest request, HttpServletResponse response){
        Object id = pramMap.get("id");
        try{
            int fileId =(Integer)id;
            Teacher teacher = getTeacher(request);
            logger.info("删除文件id........:{}",fileId);
            Map<String,Object> result = contractInfoService.remoteFile(fileId,teacher.getId());
            if(ReturnMapUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            logger.error("deleteFile exception", e);
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            logger.error("deleteFile exception", e);
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }

    /**
     * 文件上传功能
     */
    private FileVo awsUpload(MultipartFile file, Long teacherId) {
        logger.info("teacher id = {} ", teacherId);
        FileVo fileVo = null;
        if (file != null) {
            String name = file.getOriginalFilename();
            String bucketName = PropertyConfigurer.stringValue("aws.bucketName");


            String key = AwsFileUtils.getTaxpayerkey(teacherId + "-" + name);
            Long size = file.getSize();

            Preconditions.checkArgument(AwsFileUtils.checkTaxPayerFileType(name), "文件类型不正确，支持类型为" + AwsFileUtils.TAPXPAYER_FILE_TYPE);
            Preconditions.checkArgument(AwsFileUtils.checkTaxPayerFileSize(size), "文件太大，maxSize = " + AwsFileUtils.TAPXPAYER_FILE_MAX_SIZE);

            try {
                logger.info("文件:{}上传",name);
                fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), file.getSize());
            } catch (IOException e) {
                logger.error("awsUpload exception", e);
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
     */
    @RequestMapping("/uploadIdentification")
    public Map<String,Object> uploadIdentification(@RequestParam("file") MultipartFile file,String filetype,HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = new HashMap<>();

        if(StringUtils.isBlank(filetype)){
            return ReturnMapUtils.returnFail("There is no type of file upload");
        }

        try{
        Teacher teacher = getTeacher(request);
        logger.info("用户：{}，upload Identification file = {}", teacher.getId(), file);

        FileVo fileVo  = awsUpload(file, teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail("upload file is fail");
        }


            TeacherContractFile teacherContractFile = new TeacherContractFile();
            teacherContractFile.setTeacherId(teacher.getId());
            teacherContractFile.setUrl(fileVo.getUrl());
            teacherContractFile.setTeacherApplicationId(0);
            //文件类型1-other_degrees  2-certificationFiles   3-Identification  4-Diploma 5-Contract  6-Passport   7-Driver's license
            if(filetype.equals("passport")){
                teacherContractFile.setFileType(TeacherApplicationEnum.ContractFileType.PASSPORT.val());
            }
            if(filetype.equals("driver")){
                teacherContractFile.setFileType(TeacherApplicationEnum.ContractFileType.DRIVER.val());
            }
            if(filetype.equals("identity")){
                teacherContractFile.setFileType(TeacherApplicationEnum.ContractFileType.IDENTIFICATION.val());
            }

            contractInfoService.save(teacherContractFile);
            result.put("file",fileVo.getUrl());
            result.put("status",true);
            result.put("id", teacherContractFile.getId());
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }


    /**
     * 上传老师的最高学历
     */
    @RequestMapping("/uploadDiploma")
    public Map<String,Object> uploadDiploma(@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response){

        Map<String,Object> result = new HashMap<>();
        try{
        Teacher teacher = getTeacher(request);
        logger.info("用户 :{},upload uploadDiploma file = {}",teacher.getId(),file);
        FileVo fileVo  = awsUpload(file, teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail("upload file is fail");
        }


            TeacherContractFile teacherContractFile = new TeacherContractFile();
            teacherContractFile.setTeacherId(teacher.getId());
            teacherContractFile.setUrl(fileVo.getUrl());
            teacherContractFile.setTeacherApplicationId(0);
            teacherContractFile.setFileType(TeacherApplicationEnum.ContractFileType.DIPLOMA.val());
            contractInfoService.save(teacherContractFile);
            result.put("file",fileVo.getUrl());
            result.put("status",true);
            result.put("id", teacherContractFile.getId());
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }


    /**
     * 上传老师的合同
     */
    @RequestMapping("/uploadContract")
    public Map<String,Object> uploadContract(@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response){

        Map<String,Object> result = new HashMap<>();
        try{
        Teacher teacher = getTeacher(request);
        logger.info("用户 :{},upload uploadContract file = {}",teacher.getId(),file);
        FileVo fileVo  = awsUpload(file, teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail("upload file is fail");
        }


            TeacherContractFile teacherContractFile = new TeacherContractFile();
            teacherContractFile.setTeacherId(teacher.getId());
            teacherContractFile.setUrl(fileVo.getUrl());
            teacherContractFile.setTeacherApplicationId(0);
            teacherContractFile.setFileType(TeacherApplicationEnum.ContractFileType.CONTRACT.val());
            contractInfoService.save(teacherContractFile);

            result.put("file",fileVo.getUrl());
            result.put("status",true);
            result.put("id", teacherContractFile.getId());
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }


    /**
     * 上传W9-TAX文件
     */
    @RequestMapping("/uploadW9Tax")
    public Map<String,Object> uploadW9Tax(@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response){

        Map<String,Object> result = new HashMap<>();
        try{
        Teacher teacher = getTeacher(request);
        logger.info("用户 :{},upload uploadW9Tax file = {}",teacher.getId(),file);
        FileVo fileVo  = awsUpload(file, teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail("upload file is fail");
        }


            //更新w9-tax
            TeacherTaxpayerForm teacherTaxpayerForm = new TeacherTaxpayerForm();
            logger.info("保存用户：{}上传的合W9-TAX文件url",teacher.getId());
            teacherTaxpayerForm.setTeacherId(teacher.getId());
            teacherTaxpayerForm.setUrl(fileVo.getUrl());
            teacherTaxpayerForm.setFormType(TeacherEnum.FormType.W9.val());
            setTeacherTaxpayerFormInfo(teacherTaxpayerForm, request);
            teacherTaxpayerFormService.saveTeacherTaxpayerForm(teacherTaxpayerForm );

            TeacherContractFile teacherContractFile = new TeacherContractFile();
            logger.info("保存用户：{}上传的合W9-TAX文件url到teacher_other_degrees",teacher.getId());
            teacherContractFile.setTeacherId(teacher.getId());
            teacherContractFile.setUrl(fileVo.getUrl());
            teacherContractFile.setTeacherApplicationId(0);
            teacherContractFile.setFileType(TeacherApplicationEnum.ContractFileType.CONTRACT_W9.val());
            contractInfoService.save(teacherContractFile);

            result.put("file",fileVo.getUrl());
            result.put("id", teacherContractFile.getId());
            result.put("status",true);
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }





    /**
     * 上传Certification文件
     */
    @RequestMapping("/uploadCertification ")
    public Map<String,Object> uploadCertification (@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response){

        Map<String,Object> result = new HashMap<>();
        try{
        Teacher teacher =getTeacher(request);
        logger.info("用户 :{},upload uploadCertification file = {}",teacher.getId(),file);
        FileVo fileVo  = awsUpload(file, teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail("upload file is fail");
        }

            TeacherContractFile teacherContractFile = new TeacherContractFile();
            teacherContractFile.setTeacherId(teacher.getId());
            teacherContractFile.setUrl(fileVo.getUrl());
            teacherContractFile.setTeacherApplicationId(0);
            teacherContractFile.setFileType(TeacherApplicationEnum.ContractFileType.CERTIFICATIONFILES.val());
            contractInfoService.save(teacherContractFile);
            result.put("file",fileVo.getUrl());
            result.put("status",true);
            result.put("id", teacherContractFile.getId());
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }

    /**
     * 上传Degrees文件
     */
    @RequestMapping("/uploadDegrees ")
    public Map<String,Object> uploadDegrees (@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response){

        Map<String,Object> result = new HashMap<>();
        try{
        Teacher teacher = getTeacher(request);
        logger.info("用户 :{},upload uploadDegrees file = {}",teacher.getId(),file);
        FileVo fileVo  = awsUpload(file, teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail("upload file is fail");
        }

            TeacherContractFile teacherContractFile = new TeacherContractFile();
            teacherContractFile.setTeacherId(teacher.getId());
            teacherContractFile.setUrl(fileVo.getUrl());
            teacherContractFile.setTeacherApplicationId(0);
            teacherContractFile.setFileType(TeacherApplicationEnum.ContractFileType.OTHER_DEGREES.val());
            contractInfoService.save(teacherContractFile);
            result.put("file",fileVo.getUrl());
            result.put("status",true);
            result.put("id", teacherContractFile.getId());
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(), e);
        }
    }





    private void setTeacherTaxpayerFormInfo(TeacherTaxpayerForm teacherTaxpayerForm, HttpServletRequest request){
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
