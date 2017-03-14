package com.vipkid.background.controller;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.vipkid.background.enums.TeacherPortalCodeEnum;
import com.vipkid.background.service.BackgroundCheckService;
import com.vipkid.background.vo.input.BackgroundCheckInput;
import com.vipkid.background.vo.output.BaseOutput;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.util.AwsFileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/***
 * background check for teacher
 */
@RestController
@RequestMapping("/backgroundCheck")
public class BackgroundCheckController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(BackgroundCheckController.class);

    @Autowired
    private BackgroundCheckService checkService;

    /***
     * save background check information
     * @param checkInput
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/submitCheckInfoForUs", method = RequestMethod.POST)
    public Map<String, Object> submitCheckInfoForUs(@RequestBody BackgroundCheckInput checkInput, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = Maps.newHashMap();
        Long teacherId = checkInput.getTeacherId();
        String maidenName = checkInput.getMaidenName();
        Integer countryId = checkInput.getCountryId();
        Integer stateId = checkInput.getStateId();
        Integer cityId = checkInput.getCity();
        String street = checkInput.getStreet();
        String zipCode = checkInput.getZipCode();

        String birthday = checkInput.getBirthDay();
        String driverLicenseNumber = checkInput.getDriverLicenseNumber();
        String driverLicenseType = checkInput.getDriverLicenseType();
        String driverLicenseAgency = checkInput.getDriverLicenseAgency();
        String fileUrl = checkInput.getFileUrl();
        try{
            Preconditions.checkArgument(teacherId != null, "teacher ID cannot be null");
            //Preconditions.checkArgument(StringUtils.isNotBlank(maidenName), "maidenName cannot be null");
            Preconditions.checkArgument(countryId != null, "countryId cannot be null");
            Preconditions.checkArgument(stateId != null, "stateId cannot be null");
            Preconditions.checkArgument(cityId != null, "cityId cannot be null");
            Preconditions.checkArgument(StringUtils.isNotBlank(street), "street cannot be null");
            Preconditions.checkArgument(StringUtils.isNotBlank(zipCode), "zipCode cannot be null");
            Preconditions.checkArgument(StringUtils.isNotBlank(birthday), "birthday cannot be null");

            Preconditions.checkArgument(StringUtils.isNotBlank(driverLicenseNumber), "driverLicenseNumber cannot be null");
            Preconditions.checkArgument(StringUtils.isNotBlank(driverLicenseType), "driverLicenseType cannot be null");
            Preconditions.checkArgument(StringUtils.isNotBlank(driverLicenseAgency), "driverLicenseAgency cannot be null");
            Preconditions.checkArgument(StringUtils.isNotBlank(fileUrl), "fileUrl cannot be null");

            BaseOutput output = checkService.saveBackgroundCheckInfo(checkInput);
            if(!StringUtils.equals(TeacherPortalCodeEnum.RES_SUCCESS.getCode(), TeacherPortalCodeEnum.RES_SUCCESS.getMsg())){
                return ApiResponseUtils.buildErrorResp(-2, "Failed to submit background check information.");
            }
        }catch(Exception e){
            logger.warn("submit background check info occur exception, ");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ApiResponseUtils.buildErrorResp(-2, "Failed to submit background check information.");
        }
        return ApiResponseUtils.buildSuccessDataResp(result);
    }


}
