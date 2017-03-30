package com.vipkid.rest.portal.controller;

import com.vipkid.enums.TeacherEnum;
import com.vipkid.http.vo.StandardJsonObject;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.rest.utils.BuryPointUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by LP-813 on 2017/3/30.
 */
@RestController
@RestInterface(lifeCycle = TeacherEnum.LifeCycle.REGULAR)
@RequestMapping("/burypoint/")
public class BuryPointRestController {

    @RequestMapping(value = "shareParentFeedback", method = RequestMethod.GET)
    public Object shareParentFeedback(Integer teacherId,Long onlineClassId,Long feedbackId,String channel){
        BuryPointUtils.shareParentFeedBack(teacherId, onlineClassId, feedbackId, "PC",channel);
        return ApiResponseUtils.buildSuccessDataResp("OK");
    }


}
