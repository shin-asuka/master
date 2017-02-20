package com.vipkid.portal.classroom.controller;

import com.vipkid.enums.TeacherEnum;
import com.vipkid.http.service.AssessmentHttpService;
import com.vipkid.http.vo.StudentUnitAssessment;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by LP-813 on 2017/2/20.
 */
@RestController
@RestInterface(lifeCycle= TeacherEnum.LifeCycle.REGULAR)
@RequestMapping("/portal/classroom")
public class ExitClassroomController {

    private static Logger logger = LoggerFactory.getLogger(ExitClassroomController.class);

    @Autowired
    private AssessmentHttpService assessmentHttpService;

    @RequestMapping("/report/ua")
    public Map<String,Object> getUaSubmitStatus(HttpServletRequest request, HttpServletResponse response,
                                             @RequestParam Long onlineClassId,@RequestParam Integer studentId,Model model){
        try{
            logger.info("[ExitClassroomController.getUaSubmitStatus]获取UA状态:onlineClassId = {},studentId = {}",onlineClassId,studentId);
            boolean result = false;
            StudentUnitAssessment studentUnitAssessment = assessmentHttpService.findStudentUnitAssessmentByOnlineClassId(onlineClassId);
            if(studentUnitAssessment!=null && studentUnitAssessment.getSubmitStatus()==1){
                result = true;
            }
            logger.info("[ExitClassroomController.getUaSubmitStatus]获取UA状态成功:ret = {}",result);
            return ApiResponseUtils.buildSuccessDataResp(result);
        }catch (Exception e){
            logger.error("获取UA状态失败",e);
            return ApiResponseUtils.buildErrorResp(1002,"[ExitClassroomController.getUaSubmitStatus ERROR]:UA状态获取失败");
        }
    }
}
