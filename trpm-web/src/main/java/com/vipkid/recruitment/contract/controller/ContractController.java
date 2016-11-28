package com.vipkid.recruitment.contract.controller;
import java.io.IOException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.common.base.Preconditions;
import com.vipkid.file.model.FileVo;
import com.vipkid.file.service.AwsFileService;
import com.vipkid.recruitment.entity.ContractFile;
import com.vipkid.recruitment.entity.TeacherOtherDegrees;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.trpm.util.AwsFileUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.recruitment.contract.service.ContractService;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;
import com.vipkid.trpm.entity.TeacherTaxpayerFormDetail;
import com.vipkid.trpm.service.portal.TeacherTaxpayerFormService;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by zhangzhaojun on 2016/11/14.
 */
@RestController
@RestInterface(lifeCycle={LifeCycle.CONTRACT})

@RequestMapping("/recruitment/contract")
public class ContractController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(ContractController.class);
    @Autowired
    private ContractService contractService;
    @Autowired
    private TeacherTaxpayerFormService teacherTaxpayerFormService;
    @Autowired
    private AwsFileService awsFileService;

    /**
     * 提交用户上传文件的信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public  Map<String,Object> submitsTeacher(@RequestBody Map<String,Object> pramMap, HttpServletRequest request, HttpServletResponse response){
        Object id = pramMap.get("id");
        if(id.equals("")||id==null){
            return ResponseUtils.responseFail("You don't have to upload the file", this);
        }
        String ids = String.valueOf(id);
        String[] fileId = ids.split(",");
        List<Integer> idList = new ArrayList<Integer>();
        for(int i=0;i<fileId.length;i++){
            idList.add(Integer.parseInt(fileId[i]));
        }
        Teacher teacher = getTeacher(request);
        logger.info("保存用户：{}TeacherApplication",teacher.getId());
        try{
            Map<String,Object> result = contractService.updateTeacherApplication(teacher,idList);
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

    /**
     * 跳转到PUBLICITY_INFO改变用户的LifeCycle
     * @param request
     * @param response
     */
    @RequestMapping(value = "/toPublic", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> toPublic(HttpServletRequest request, HttpServletResponse response){
        try{
            Teacher teacher = getTeacher(request);
            logger.info("user:{},getReschedule",teacher.getId());
            Map<String,Object> result = this.contractService.toPublic(teacher);
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