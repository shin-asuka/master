package com.vipkid.recruitment.contract.controller;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.recruitment.contract.service.ContractService;
import com.vipkid.recruitment.interceptor.RestInterface;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.entity.ContractFile;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;
import com.vipkid.trpm.entity.TeacherTaxpayerFormDetail;
import com.vipkid.trpm.service.portal.TeacherTaxpayerFormService;

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

        logger.info("保存用户：{}上传的合同等文件URL",teacher.getId());
        teacher = this.contractService.updateTeacher(contractFile,teacher.getId());
        //更新w9-tax
        TeacherTaxpayerForm teacherTaxpayerForm = new TeacherTaxpayerForm();
        logger.info("保存用户：{}上传的合W9-TAX文件url",teacher.getId());
        teacherTaxpayerForm.setUrl(contractFile.getTax());
        teacherTaxpayerForm.setFormType(TeacherEnum.FormType.W9.val());
        setTeacherTaxpayerFormInfo(teacherTaxpayerForm, request);
        teacherTaxpayerFormService.saveTeacherTaxpayerForm(teacherTaxpayerForm );
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("info", true);
        return map;
    }


    /**
     * 检查用户的Contract是否审核通过
     * @param request
     * @param response
     */
    @RequestMapping(value = "/checkedContract", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public  Map<String,Object> checkedContract(HttpServletRequest request,HttpServletResponse response){
        Teacher teacher = getTeacher(request);
        logger.info("检查用户：{}的lifeCycle状态",teacher.getId());
        Map<String,Object> recMap = new HashMap<String,Object>();
        if(TeacherEnum.LifeCycle.SENT_DOCS.toString().equals(teacher.getLifeCycle())){
            recMap.put("info", true);
        }else{
            recMap.put("info", false);
        }
        logger.info("toRegular Status " + teacher.getLifeCycle());
        return recMap;
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
}