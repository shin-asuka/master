package com.vipkid.portal.classroom.controller;

import com.vipkid.enums.TeacherEnum;
import com.vipkid.portal.classroom.model.TrialCommentsVo;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by LP-813 on 2017/2/16.
 */
@RestController
@RestInterface(lifeCycle= TeacherEnum.LifeCycle.REGULAR)
@RequestMapping("/portal/comments/")
public class TrialFeedbackController {

    @RequestMapping("/trial/save")
    public Map<String,Object> feedbackTrialSubmit(HttpServletRequest request, HttpServletResponse response,
                                                  @RequestBody TrialCommentsVo teacherComment, Model model) {

        return ApiResponseUtils.buildSuccessDataResp("OK");
    }

    @RequestMapping("/trial/view")
    public Map<String,Object> feedbackTrialView(HttpServletRequest request, HttpServletResponse response,
                                                @RequestParam Long onlineClassId ,@RequestParam Integer studentId) {
        return ApiResponseUtils.buildSuccessDataResp("OK");
    }

}
