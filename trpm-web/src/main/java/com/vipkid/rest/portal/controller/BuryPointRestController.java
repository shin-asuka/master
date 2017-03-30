package com.vipkid.rest.portal.controller;

import com.vipkid.enums.TeacherEnum;
import com.vipkid.http.vo.StandardJsonObject;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.rest.utils.BuryPointUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by LP-813 on 2017/3/30.
 */
@Controller
@RestInterface(lifeCycle = TeacherEnum.LifeCycle.REGULAR)
@RequestMapping("/burypoint/")
public class BuryPointRestController {

    @RequestMapping(value = "shareParentFeedback", method = RequestMethod.GET)
    public Object shareParentFeedback(Integer teacherId,Long onlineClassId,Long feedbackId){
        BuryPointUtils.shareParentFeedBack(teacherId,onlineClassId,feedbackId,"PC");
        return ApiResponseUtils.buildSuccessDataResp(true);
    }


}
