package com.vipkid.background.controller;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.vipkid.background.dto.input.BackgroundCheckCaInputDto;
import com.vipkid.background.dto.input.BackgroundCheckInputDto;
import com.vipkid.background.dto.output.BaseOutputDto;
import com.vipkid.background.enums.TeacherPortalCodeEnum;
import com.vipkid.background.service.BackgroundCheckService;
import com.vipkid.background.vo.BackgroundCheckVo;
import com.vipkid.common.utils.IdentityValidatorUtils;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.file.model.FileVo;
import com.vipkid.file.service.AwsFileService;
import com.vipkid.recruitment.entity.TeacherContractFile;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.exception.ServiceException;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.portal.TeacherService;
import com.vipkid.trpm.util.AwsFileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * background check for teacher
 */
@RestController
@RestInterface(lifeCycle = {TeacherEnum.LifeCycle.REGULAR})
@RequestMapping("/background/info")
public class BackgroundCheckController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(BackgroundCheckController.class);

    @Autowired
    private BackgroundCheckService checkService;

    @Autowired
    private AwsFileService fileService;

    @Autowired
    private TeacherService teacherService;


    /***
     * save background check information for US
     * @param checkInput
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/saveCheckInfoForUs", method = RequestMethod.POST)
    public Map<String, Object> saveCheckInfoForUs(@RequestBody BackgroundCheckInputDto checkInput, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = Maps.newHashMap();
        Teacher teacher = getTeacher(request);
        Long teacherId = teacher.getId();
        checkInput.setTeacherId(teacherId);
        String operateType = checkInput.getOperateType();
        /*
        Integer currentCountryId = checkInput.getCurrentCountryId();
        Integer currentStateId = checkInput.getCurrentStateId();
        Integer currentCityId = checkInput.getCurrentCity();
        */
        Integer countryId = checkInput.getLatestCountryId();
        Integer stateId = checkInput.getLatestStateId();
        Integer cityId = checkInput.getLatestCity();
        String street = checkInput.getLatestStreet();
        String zipCode = checkInput.getLatestZipCode();

        String birthday = checkInput.getBirthDay();
        String socialSecurityNo = checkInput.getSocialSecurityNumber();
        String fileUrl = checkInput.getFileUrl();
        try {
            /*
            Preconditions.checkArgument(currentCountryId != null, "currentCountryId cannot be null");
            Preconditions.checkArgument(currentStateId != null, "currentStateId cannot be null");
            Preconditions.checkArgument(currentCityId != null, "currentCityId cannot be null");
            */
            Preconditions.checkArgument(countryId != null, "latestCountryId cannot be null");
            Preconditions.checkArgument(stateId != null, "latestStateId cannot be null");
            Preconditions.checkArgument(cityId != null, "latestCityId cannot be null");
            Preconditions.checkArgument(StringUtils.isNotBlank(street), "latestStreet cannot be null");
            Preconditions.checkArgument(StringUtils.isNotBlank(zipCode), "latestZipCode cannot be null");
            Preconditions.checkArgument(StringUtils.isNotBlank(birthday), "latestBirthday cannot be null");
            Preconditions.checkArgument(StringUtils.isNotBlank(socialSecurityNo), "socialSecurityNumber cannot be null");

            if(socialSecurityNo.indexOf("*") == -1){
                if(!IdentityValidatorUtils.validSocialNoForUs(socialSecurityNo)){
                    throw new IllegalArgumentException("socialSecurityNumber is illegal");
                }
            }
            if(!IdentityValidatorUtils.validZipCodeForUs(zipCode)){
                throw new IllegalArgumentException("latestZipCode is illegal");
            }

            if(StringUtils.equals(operateType, "submit")){
                Preconditions.checkArgument(StringUtils.isNotBlank(fileUrl), "fileUrl cannot be null");
            }

            BaseOutputDto output = checkService.saveBackgroundCheckInfo(checkInput, operateType);

            if (!StringUtils.equals(TeacherPortalCodeEnum.RES_SUCCESS.getCode(), output.getResCode())) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ApiResponseUtils.buildErrorResp(output.getResCode(), output.getResMsg());
            }

        } catch (IllegalArgumentException e) {
            logger.warn("save background check info for US occur IllegalArgumentException, teacherId=" + teacherId);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ApiResponseUtils.buildErrorResp(TeacherPortalCodeEnum.SYS_PARAM_ERROR.getCode(), e.getMessage());
        } catch (ServiceException e) {
            logger.error("save background check info for US occur ServiceException, teacherId=" + teacherId, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(TeacherPortalCodeEnum.SYS_FAIL.getCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("save background check info for US occur exception, teacherId=" + teacherId, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(TeacherPortalCodeEnum.SYS_FAIL.getCode(), "Failed to save background check information.");
        }
        logger.info("BackgroundCheckController.saveCheckInfoForUs succeed, teacherId="+teacherId);
        return ApiResponseUtils.buildSuccessDataResp(result);
    }

    /**
     * upload background check file
     */
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file,  @RequestParam("fileType") Integer type, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        Long teacherId = null;
        try {
            Preconditions.checkArgument(file != null, "文件不能为空");
            Preconditions.checkArgument(type != null, "文件类型不能为空");
            Preconditions.checkArgument(validateContractFileType(type), "文件类型错误");
            Long size = file.getSize();
            String fileName = file.getOriginalFilename();

            Preconditions.checkArgument(AwsFileUtils.checkContractFileType(fileName), "文件类型不正确，支持类型为" + AwsFileUtils.CONTRACT_FILE_TYPE);
            Preconditions.checkArgument(AwsFileUtils.checkContractFileSize(size), "文件太大，maxSize = " + AwsFileUtils.CONTRACT_FILE_MAX_SIZE);

            Teacher teacher = getTeacher(request);
            teacherId = teacher.getId();
            if (StringUtils.isNotBlank(fileName)) {
                fileName = AwsFileUtils.reNewFileName(fileName);
            }

            String key="";
            switch (type){
                case 9:
                    key = AwsFileUtils.getUsBackgroundCheckKey(teacher.getId(), teacher.getId() + "-" + fileName);
                    break;
                case 10:
                    key = AwsFileUtils.getCanadaBackgroundCheckCpicFormKey(teacher.getId(), teacher.getId() + "-" + fileName);
                    break;
                case 11:
                    key = AwsFileUtils.getCanadaBackgroundCheckId2Key(teacher.getId(), teacher.getId() + "-" + fileName);
                    break;
            }
            FileVo fileVo = fileService.awsUpload(file, teacher.getId(), fileName, key);
            if (fileVo == null) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return ApiResponseUtils.buildErrorResp(TeacherPortalCodeEnum.SYS_FAIL.getCode(),"Upload failed!  Please try again.");
            }
            checkService.saveContractFile(teacherId, type, fileVo.getUrl(), "save");
            result.put("fileUrl", fileVo.getUrl());
        } catch (IllegalArgumentException e) {
            logger.warn("upload background file for US occur IllegalArgumentException, teacherId="+teacherId, e);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ApiResponseUtils.buildErrorResp(TeacherPortalCodeEnum.SYS_PARAM_ERROR.getCode(), e.getMessage());
        }catch (ServiceException e) {
            logger.error("upload background file for US occur ServiceException, teacherId="+teacherId, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(TeacherPortalCodeEnum.SYS_FAIL.getCode(), "failed to upload file");
        } catch (Exception e) {
            logger.error("upload background file for US occur exception, teacherId="+teacherId, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(TeacherPortalCodeEnum.SYS_FAIL.getCode(), "failed to upload file");
        }
        logger.info("BackgroundCheckController.uploadFile succeed, teacherId="+teacherId);
        return ApiResponseUtils.buildSuccessDataResp(result);
    }

    /**
     * save background check file for CA
     */
    @RequestMapping(value = "/saveCheckInfoForCa", method = RequestMethod.POST)
    public Map<String, Object> saveCheckInfoForCa(@RequestBody BackgroundCheckCaInputDto input, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        String cpicUrl = input.getCpicUrl();
        String id2Url = input.getId2Url();

        Long teacherId = null;
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(cpicUrl), "CPIC file cannot be null");
            Preconditions.checkArgument(StringUtils.isNotBlank(id2Url), "id2 file cannot be null");

            Teacher teacher = getTeacher(request);
            teacherId = teacher.getId();
            logger.info("save background check file for CA, teacherId=" + teacherId);

            List<TeacherContractFile> list = checkService.getInfoForCa(teacherId);

            if(list != null && list.size() > 0){
                for(TeacherContractFile file : list){
                    String fileResult = file.getResult();
                    if(!fileResult.equals("PASS") && file.getFileType() == TeacherApplicationEnum.ContractFileType.CANADA_BACKGROUND_CHECK_CPIC_FORM.val().intValue()){
                        checkService.saveContractFile(teacherId, TeacherApplicationEnum.ContractFileType.CANADA_BACKGROUND_CHECK_CPIC_FORM.val(), cpicUrl, "submit");
                    }
                    if(!fileResult.equals("PASS") && file.getFileType() == TeacherApplicationEnum.ContractFileType.CANADA_BACKGROUND_CHECK_ID2.val().intValue()){
                        checkService.saveContractFile(teacherId, TeacherApplicationEnum.ContractFileType.CANADA_BACKGROUND_CHECK_ID2.val(), id2Url, "submit");
                    }
                }
            }else{
                checkService.saveContractFile(teacherId, TeacherApplicationEnum.ContractFileType.CANADA_BACKGROUND_CHECK_CPIC_FORM.val(), cpicUrl, "submit");
                checkService.saveContractFile(teacherId, TeacherApplicationEnum.ContractFileType.CANADA_BACKGROUND_CHECK_ID2.val(), id2Url, "submit");
            }
            result.put("cpicUrl", cpicUrl);
            result.put("id2Url", id2Url);
            logger.info("save background check file for CA success, teacherId=" + teacherId );
        } catch (IllegalArgumentException e) {
            logger.error("save background check file for CA occur IllegalArgumentException, teacherId=" + teacherId, e);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ApiResponseUtils.buildErrorResp(TeacherPortalCodeEnum.SYS_PARAM_ERROR.getCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("save background check file for CA  occur exception, teacherId=" + teacherId, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(TeacherPortalCodeEnum.SYS_FAIL.getCode(), TeacherPortalCodeEnum.SYS_FAIL.getMsg());
        }
        logger.info("BackgroundCheckController.saveCheckInfoForCa succeed, teacherId="+teacherId);
        return ApiResponseUtils.buildSuccessDataResp(result);
    }

    /***
     * get USA background check info
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getCheckInfoForUs", method = RequestMethod.GET)
    public Map<String, Object> getCheckInfoForUs(HttpServletRequest request, HttpServletResponse response) {
        Teacher teacher = getTeacher(request);
        User user = getUser(request);
        Long teacherId = teacher.getId();
        BackgroundCheckVo info = null;
        try{
            info = checkService.getInfoForUs(teacherId);
            info.setGender(user.getGender());
        }catch (Exception e) {
            logger.error("get background check info occur exception, teacherId=" + teacherId, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(TeacherPortalCodeEnum.SYS_FAIL.getCode(), TeacherPortalCodeEnum.SYS_FAIL.getMsg());
        }
        logger.info("BackgroundCheckController.getCheckInfoForUs succeed, teacherId="+teacherId);
        return ApiResponseUtils.buildSuccessDataResp(info);
    }

    /***
     * get CA background check info
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getCheckInfoForCa", method = RequestMethod.GET)
    public Map<String, Object> getCheckInfoForCa(HttpServletRequest request, HttpServletResponse response) {
        Teacher teacher = getTeacher(request);
        Long teacherId = teacher.getId();
        Map<String, Object> map = new HashMap<String, Object>();
        try{
            Teacher teacherDo = teacherService.get(teacherId);
            map.put("id1Url", teacherDo.getPassport());
            List<TeacherContractFile> list = checkService.getInfoForCa(teacherId);
            if(CollectionUtils.isEmpty(list)){
                return ApiResponseUtils.buildSuccessDataResp(map);
            }
            for(TeacherContractFile file : list){
                String url = file.getUrl();
                Integer type = file.getFileType();
                if(type.equals(TeacherApplicationEnum.ContractFileType.CANADA_BACKGROUND_CHECK_CPIC_FORM.val())){
                    map.put("cpicUrl", url);
                    map.put("cpicResult", file.getResult());
                    map.put("cpicFailReason", file.getFailReason());
                }
                if(type.equals(TeacherApplicationEnum.ContractFileType.CANADA_BACKGROUND_CHECK_ID2.val())){
                    map.put("id2Url", url);
                    map.put("id2Result", file.getResult());
                    map.put("id2FailReason", file.getFailReason());
                }
            }
        }catch (Exception e) {
            logger.error("get background check info occur exception, teacherId=" + teacherId, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(TeacherPortalCodeEnum.SYS_FAIL.getCode(), TeacherPortalCodeEnum.SYS_FAIL.getMsg());
        }
        logger.info("BackgroundCheckController.getCheckInfoForCa succeed, teacherId="+teacherId);
        return ApiResponseUtils.buildSuccessDataResp(map);
    }

    private boolean validateContractFileType(Integer type){
        if(!type.equals(TeacherApplicationEnum.ContractFileType.US_BACKGROUND_CHECK.val())
                && !type.equals(TeacherApplicationEnum.ContractFileType.CANADA_BACKGROUND_CHECK_CPIC_FORM.val())
                && !type.equals(TeacherApplicationEnum.ContractFileType.CANADA_BACKGROUND_CHECK_ID2.val())){
            return false;
        }
        return true;
    }
}
