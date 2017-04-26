package com.vipkid.portal.glory.controller;

import com.vipkid.cache.CacheConfigConst;
import com.vipkid.cache.utils.RedisClient;
import com.vipkid.portal.glory.model.TeacherGlory;
import com.vipkid.portal.glory.service.TeacherGloryRestService;
import com.vipkid.rest.security.AppContext;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.proxy.RedisProxy;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by LP-813 on 2017/4/24.
 */
@RestController
//@RestInterface(lifeCycle = TeacherEnum.LifeCycle.REGULAR)
public class TeacherGloryRestController {

    private TeacherGloryRestService teacherGloryRestService;

    @RequestMapping(value = "getTeacherGlory", method = RequestMethod.GET)
    public Map<String, Object> getByTeacherId() {

        long userId = AppContext.getUser().getId();
        String userGloryKey = CacheConfigConst.TEACHER_GLORY_KEY + "_" + userId;
        RedisProxy redisProxy = RedisClient.getInstance();
        String currentGlory = redisProxy.get(userGloryKey);
        String[] newGlory = teacherGloryRestService.refeshGlory(currentGlory,new Long(userId).intValue());
        List<TeacherGlory> ret = teacherGloryRestService.getGloryView(newGlory);
        String[] markedGlory = teacherGloryRestService.markShownStatus(newGlory);
        redisProxy.set(userGloryKey,StringUtils.join(markedGlory));

        return ApiResponseUtils.buildSuccessDataResp(ret);
    }
}
