package com.vipkid.trpm.controller.portal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.vipkid.file.utils.Encodes;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.personal.APIQueryContractByIdResult;
import com.vipkid.trpm.entity.personal.APIQueryContractListByTeacherIdResult;
import com.vipkid.trpm.entity.personal.QueryContractByTeacherIdOutputDto;
import com.vipkid.trpm.service.portal.TeacherService;
import com.vipkid.trpm.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.vipkid.trpm.service.portal.PersonalInfoService;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现描述:personal页面的数据接口,页面接口在PersonalInfoController
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2017/4/10 下午2:41
 */
@RestController
@RequestMapping("/personal")
public class PersonalInfoDataController {


    private Logger logger = LoggerFactory.getLogger(PersonalInfoDataController.class);

    @Resource
    private PersonalInfoService personalInfoService;

    @Resource
    private TeacherService teacherService;

    /***
     *
     * 获取教师合同信息、分两种返回:1是未签的unsignList,2是已签或生效的signedList
     *
     * @author steven
     * @param teacherId 老师Id
     * @return
     */
    @RequestMapping(value = "/contractInfo", method = RequestMethod.GET)
    public Map<String, Object> contractinfo(@RequestParam("teacherId") Long teacherId) {

        //分两种返回:
        //1.未签的
        List<QueryContractByTeacherIdOutputDto> signedList = Lists.newArrayList();
        //2.已签或生效的,并且是在合同有效期内的
        List<QueryContractByTeacherIdOutputDto> unsignList = Lists.newArrayList();

        Date today = new Date();
        Teacher teacher = teacherService.get(teacherId);
        if (StringUtils.isNotBlank(teacher.getContract())
                && teacher.getContractStartDate() != null
                && teacher.getContractEndDate() != null
                && DateUtils.compareDate(today, teacher.getContractStartDate()) >= 0
                && DateUtils.compareDate(today, teacher.getContractEndDate()) <= 0) {
            //如果是在合同期内(注意one的时区和today的时区必须都要是北京时区)
            signedList.add(new QueryContractByTeacherIdOutputDto(teacher.getContractStartDate(),
                    teacher.getContractEndDate(),
                    teacher.getContract()));
        }

        List<APIQueryContractListByTeacherIdResult> contractInstanceResultList = personalInfoService
                .queryALLContractByTeacherId(teacherId);


        if (CollectionUtils.isNotEmpty(contractInstanceResultList)) {
            for (APIQueryContractListByTeacherIdResult one : contractInstanceResultList) {
                if (CollectionUtils.isEmpty(unsignList) && StringUtils
                        .equals(one.getInstanceStatus(), ApplicationConstant.ContractConstants.INSTANT_STATUS_NOSIGN)) {
                    //未签的合同(最多只返回给前端一个)
                    unsignList.add(new QueryContractByTeacherIdOutputDto(one));
                } else if (CollectionUtils.isEmpty(signedList) &&
                        (StringUtils.equals(one.getInstanceStatus(), ApplicationConstant.ContractConstants.INSTANT_STATUS_SIGNED)
                        || StringUtils.equals(one.getInstanceStatus(), ApplicationConstant.ContractConstants.INSTANT_STATUS_ENABLE))) {
                    //已签或生效的合同(最多只返回给前端一个)
                    if (DateUtils.compareDate(today, one.getStartTime()) >= 0
                            && DateUtils.compareDate(today, one.getEndTime()) <= 0) {
                        //如果是在合同期内(注意one的时区和today的时区必须都要是北京时区)
                        signedList.add(new QueryContractByTeacherIdOutputDto(one));
                    }
                }
            }
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put("signedList", signedList);
        result.put("unsignList", unsignList);
        return ApiResponseUtils.buildSuccessDataResp(result);
    }

    /**
     * 老师签名
     * @param  signerName 老师全名
     * @param  contractId 合同Id
     */
    @RequestMapping("/contract/doSign")
    public  Map<String, Object> teacherSignature(
            @RequestParam("contractId") Long contractId,
            @RequestParam("teacherName") String signerName){

        APIQueryContractByIdResult contract = personalInfoService.queryContractById(contractId);
        if (contract == null || !StringUtils.equals(signerName, contract.getSignerName())) {
            return ApiResponseUtils.buildErrorResp(-1,"sign error,please confirm the signer's name");
        }

        boolean doSignResult = personalInfoService.doSign(contractId);
        if(doSignResult){
            return ApiResponseUtils.buildSuccessDataResp("success");
        }else{
            return ApiResponseUtils.buildErrorResp(-1,"fail");
        }
    }

    /**
     *
     * 导出合同 html 页面
     * @param contractId 合同Id
     *
     */
    @RequestMapping("/contract/download")
    public void downLoadContract(@RequestParam("contractId") Long contractId, HttpServletResponse response) {

        APIQueryContractByIdResult contract = personalInfoService.queryContractById(contractId);

        if (contract == null) {
            logger.error("can not find contract id:{}", contractId);
            return;
        }
        String fileName = contract.getInstanceNumber() + ".html";

        InputStream contentInputStream = null;
        OutputStream outputStream = null;
        try {
            contentInputStream = new ByteArrayInputStream(contract.getInstanceContent().getBytes());
            outputStream = response.getOutputStream();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));

            byte[] buffer = new byte[1024];
            int i = -1;
            while ((i = contentInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, i);
            }
            outputStream.flush();
            outputStream.close();
            contentInputStream.close();
        } catch (Exception e) {
            logger.error("downLoadContract error!", e);
        } finally {
            try {
                if (null != contentInputStream) {
                    contentInputStream.close();

                }
                if (null != outputStream) {
                    outputStream.close();

                }
            } catch (IOException e) {
                logger.error("downLoadContract io error!", e);
            }
        }

    }



    /**
     *
     * 获取合同内容
     *
     * @param contractId 合同Id
     */
    @RequestMapping("/contractContent")
    public Map<String, Object> contractContentById(@RequestParam("contractId") Long contractId){

        APIQueryContractByIdResult contract = personalInfoService.queryContractById(contractId);
        if (contract != null) {
            return ApiResponseUtils.buildSuccessDataResp(contract.getInstanceContent());
        }else{
            return ApiResponseUtils.buildErrorResp(-1,"result is null");
        }
    }
}
