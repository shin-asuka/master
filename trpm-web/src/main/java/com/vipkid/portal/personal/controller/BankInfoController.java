package com.vipkid.portal.personal.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherLocation;
import com.vipkid.trpm.entity.personal.TeacherBankVO;
import com.vipkid.trpm.service.portal.PersonalInfoService;

/**
 * 前后端分离: bankInfo 相应的接口
 *
 * @author Austin.Cao  Date: 23/12/2016
 */
@RestController
@RestInterface(lifeCycle=LifeCycle.REGULAR)
@RequestMapping("/portal/personal")
public class BankInfoController extends RestfulController{

    private static final Logger logger = LoggerFactory.getLogger(BankInfoController.class);

    @Resource
    private PersonalInfoService personalInfoService;

    @RequestMapping(value = "/bankInfo", method = RequestMethod.GET)
    public Object getBankInfo(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = Maps.newHashMap();

        long teacherId = 0L;
        try {
            Teacher teacher = getTeacher(request);
            if(null == teacher) {
                logger.error("获取教师银行信息, 教师信息为空.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return ApiResponseUtils.buildErrorResp(-1, "Teacher's info is null!");
            }
            teacherId = teacher.getId();
            logger.info("获取教师银行信息 teacherId = {}", teacherId);

            String accountName = teacher.getBankAccountName();

            accountName = personalInfoService.hideNameInfo(accountName);

            String accountNumber = teacher.getBankCardNumber();
            int len;
            if (StringUtils.isNotBlank(accountNumber)) {
                len = accountNumber.length();
                accountNumber = PersonalInfoService.hideInfo(accountNumber, 0, len - 4);
            }

            String swiftCode = teacher.getBankSwiftCode();
            if (StringUtils.isNotBlank(swiftCode)) {
                len = swiftCode.length();
                swiftCode = PersonalInfoService.hideInfo(swiftCode, 0, len - 2);
            }

            String bankABARoutingNumber = teacher.getBankABARoutingNumber();
            if (StringUtils.isNotBlank(bankABARoutingNumber)) {
                len = bankABARoutingNumber.length();
                bankABARoutingNumber = PersonalInfoService.hideInfo(bankABARoutingNumber, 0, len - 4);
            }
            String bankACHNumber = teacher.getBankACHNumber();
            if (StringUtils.isNotBlank(bankACHNumber)) {
                len = bankACHNumber.length();
                bankACHNumber = PersonalInfoService.hideInfo(bankACHNumber, 0, len - 4);
            }

            String idNumber = teacher.getIdentityNumber();
            if (StringUtils.isNotBlank(idNumber)) {
                len = idNumber.length();
                idNumber = PersonalInfoService.hideInfo(idNumber, 1, len);
            }

            result.put("accountName", accountName);
            result.put("accountNumber", accountNumber);
            result.put("swiftCode", swiftCode);
            result.put("bankABARoutingNumber", bankABARoutingNumber);
            result.put("bankACHNumber", bankACHNumber);
            result.put("idNumber", idNumber);

            TeacherAddress beneficiaryAddress = personalInfoService
                    .getTeacherAddress(teacher.getBeneficiaryAddressId());
            if (null != beneficiaryAddress) {
                TeacherLocation beneficiaryCountry = personalInfoService
                        .getLocationById(beneficiaryAddress.getCountryId());
                TeacherLocation beneficiaryState = personalInfoService.getLocationById(beneficiaryAddress.getStateId());
                TeacherLocation beneficiaryCity = personalInfoService.getLocationById(beneficiaryAddress.getCity());
                result.put("beneficiaryAddress", beneficiaryAddress);
                result.put("beneficiaryCountry", beneficiaryCountry);
                result.put("beneficiaryState", beneficiaryState);
                result.put("beneficiaryCity", beneficiaryCity);
            }
            TeacherAddress beneficiaryBankAddress = personalInfoService
                    .getTeacherAddress(teacher.getBeneficiaryBankAddressId());
            if (beneficiaryBankAddress != null) {
                TeacherLocation beneficiaryBankCountry = personalInfoService
                        .getLocationById(beneficiaryBankAddress.getCountryId());
                TeacherLocation beneficiaryBankState = personalInfoService
                        .getLocationById(beneficiaryBankAddress.getStateId());
                TeacherLocation beneficiaryBankCity = personalInfoService
                        .getLocationById(beneficiaryBankAddress.getCity());
                result.put("beneficiaryBankAddress", beneficiaryBankAddress);
                result.put("beneficiaryBankCountry", beneficiaryBankCountry);
                result.put("beneficiaryBankState", beneficiaryBankState);
                result.put("beneficiaryBankCity", beneficiaryBankCity);
            }
            TeacherLocation issuanceCountry = personalInfoService.getLocationById(teacher.getIssuanceCountry());
            result.put("issuanceCountry", issuanceCountry);
        } catch (Exception e) {
            logger.error("Failed to query bank info for " + teacherId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ApiResponseUtils.buildErrorResp(-2, "Failed to query bank info.");
        }

        return ApiResponseUtils.buildSuccessDataResp(result);

    }

    /**
     * 处理老师银行信息更新
     *
     * @param request
     * @param response
     * @param bankInfo
     * @param bankInfo
     * @return
     */
    @RequestMapping(value = "/updateBankInfo", method = RequestMethod.POST)
    public Object setBankInfoAction(HttpServletRequest request, HttpServletResponse response, TeacherBankVO bankInfo) {
        Map<String, Object> result = Maps.newHashMap();
        long teacherId = 0L;
        try {
            Teacher teacher = getTeacher(request);

            if (null == teacher) {
                logger.error("Failed to update teacher's bank info, teacher is null.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return ApiResponseUtils.buildErrorResp(-1, "Teacher's info is null!");
            }
            teacherId = teacher.getId();

            logger.info("setBankInfoAction for {}", teacherId);
            personalInfoService.doSetBankInfo(teacher, bankInfo);
            logger.info("Successfully update {}'s bank info!", teacherId);
        }catch (Exception e) {
            logger.error("Failed to update bank info for " + teacherId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ApiResponseUtils.buildErrorResp(-2, "Failed to update teacher's bank info.");
        }
        return ApiResponseUtils.buildSuccessDataResp(result);
    }

}
