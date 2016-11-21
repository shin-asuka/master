package com.vipkid.recruitment.contract.controller;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.vipkid.file.model.FileVo;
import com.vipkid.file.service.AwsFileService;

import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.trpm.util.AwsFileUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.recruitment.contract.service.ContractService;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.recruitment.entity.ContractFile;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;
import com.vipkid.trpm.entity.TeacherTaxpayerFormDetail;
import com.vipkid.trpm.service.portal.TeacherTaxpayerFormService;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by zhangzhaojun on 2016/11/14.
 */
@RestController
@RestInterface(lifeCycle={LifeCycle.CONTRACT,LifeCycle.REGULAR})

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
     * 提交并保存合同信息
     * @param request
     * @param response
     * @param contractFile
     * @return
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public  Map<String,Object> submitsTeacher(HttpServletRequest request,HttpServletResponse response,ContractFile contractFile){
        Teacher teacher = getTeacher(request);

        /*logger.info("保存用户：{}上传的合同等文件URL",teacher.getId());
        teacher = this.contractService.updateTeacher(contractFile,teacher.getId());
        //更新w9-tax
        TeacherTaxpayerForm teacherTaxpayerForm = new TeacherTaxpayerForm();
        logger.info("保存用户：{}上传的合W9-TAX文件url",teacher.getId());
        teacherTaxpayerForm.setUrl(contractFile.getTax());
        teacherTaxpayerForm.setFormType(TeacherEnum.FormType.W9.val());
        setTeacherTaxpayerFormInfo(teacherTaxpayerForm, request);
        teacherTaxpayerFormService.saveTeacherTaxpayerForm(teacherTaxpayerForm );*/
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("info", true);
        return map;
    }

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

    @RequestMapping("/uploadIdentification")
    public Map<String,Object> uploadIdentification(@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response){
        logger.info("upload Identification file = {}",file);
        Map<String,Object> result = new HashMap<String,Object>();

        Teacher teacher = new Teacher().setId(getTeacher(request).getId());

        FileVo fileVo  = AwsUpload(file,teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
           return ResponseUtils.responseFail("upload file is fail",this);
        }

        try{
            teacher.setPassport(fileVo.getUrl());
            int n = this.contractService.updateTeacher(teacher);
            if(n<=0){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail("upload file is fail",this);
            }
           String info = toJSONString(fileVo);
            result.put("file",info);
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


    @RequestMapping("/uploadDiploma")
    public Map<String,Object> uploadDiploma(@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response){
        logger.info("upload uploadDiploma file = {}",file);
        Map<String,Object> result = new HashMap<String,Object>();

        Teacher teacher = new Teacher().setId(getTeacher(request).getId());

        FileVo fileVo  = AwsUpload(file,teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail("upload file is fail",this);
        }

        try{
            teacher.setHighestLevelOfEdu(fileVo.getUrl());
            int n = this.contractService.updateTeacher(teacher);
            if(n<=0){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail("upload file is fail",this);
            }
            String info = toJSONString(fileVo);
            result.put("file",info);
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
    @RequestMapping("/uploadCertification ")
    public Map<String,Object> uploadCertification (@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpServletResponse response){
        logger.info("upload uploadCertification file = {}",file);
        Map<String,Object> result = new HashMap<String,Object>();

        Teacher teacher = new Teacher().setId(getTeacher(request).getId());

        FileVo fileVo  = AwsUpload(file,teacher.getId());
        if(fileVo==null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail("upload file is fail",this);
        }
        try{
           /* contractService.
            teacher.setHighestLevelOfEdu(fileVo.getUrl());
            int n = this.contractService.updateTeacher(teacher);
            if(n<=0){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ResponseUtils.responseFail("upload file is fail",this);
            }
            String info = toJSONString(fileVo);
            result.put("file",info);
            result.put("status",true);*/
            return ResponseUtils.responseSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseUtils.responseFail(e.getMessage(), this);
        }
    }









    @RequestMapping(value = "/contract", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object>  contract(HttpServletRequest request,HttpServletResponse response){
        Teacher teacher = getTeacher(request);
        logger.info("保存用户：{}查询上传过的文件",teacher.getId());
        return null;
    }

    /**
     * 跳转到SEND_DOCS改变用户的LifeCycle
     * @param request
     * @param response
     */
    @RequestMapping(value = "/toSendDocs", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public  Map<String,Object> toSendDocs(HttpServletRequest request,HttpServletResponse response){
        Teacher teacher = getTeacher(request);
        teacher = this.contractService.toSendDocs(teacher);
        logger.info("用户{}跳转到SEND_DOCS",teacher.getId());
        Map<String,Object> recMap = new HashMap<String,Object>();
        if(TeacherEnum.LifeCycle.SENT_DOCS.toString().equals(teacher.getLifeCycle())){
            recMap.put("info", true);
        }else{
            recMap.put("info", false);
        }
        logger.info("toSendDocs Status :" + teacher.getLifeCycle());
        return recMap;
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


    public static String toJSONString(Object object) {
        String str = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            str = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return str;
    }
}