package com.vipkid.rest.portal.controller;

import com.google.api.client.util.Lists;
import java.util.List;
import java.util.Map;

import com.vipkid.enums.TeacherEnum;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.portal.model.TeacherGlory;
import com.vipkid.rest.security.AppContext;
import com.vipkid.rest.utils.ApiResponseUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by LP-813 on 2017/4/24.
 */
@RestController
//@RestInterface(lifeCycle = TeacherEnum.LifeCycle.REGULAR)
public class TeacherGloryRestController {

    @RequestMapping(value = "getTeacherGlory", method = RequestMethod.GET)
    public Map<String,Object> getByTeacherId(){
        TeacherGlory teacherGlory = new TeacherGlory();
        teacherGlory.setGloryId(1);
        teacherGlory.setName("Become Regular");
        teacherGlory.setUserId(2040456);
        teacherGlory.setFinishTime("2017-04-24 16:17:00");
        teacherGlory.setPriority(1);
        teacherGlory.setAvatar("boy3");
        teacherGlory.setTitle("万人迷");
        teacherGlory.setDescription("在VIPKID被超过10000名学员关注");
        teacherGlory.setShareTitle("哇！有超过10000名学⽣生喜欢我！");
        teacherGlory.setShareDescription("想和我⼀一样吗？点击加⼊VIPKID和我们⼀一起改变世界吧");
        List<TeacherGlory> ret = Lists.newArrayList();
        ret.add(teacherGlory);
        return ApiResponseUtils.buildSuccessDataResp(ret);
    }
}
