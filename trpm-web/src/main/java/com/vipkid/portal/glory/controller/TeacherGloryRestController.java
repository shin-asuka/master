package com.vipkid.portal.glory.controller;

import com.vipkid.cache.CacheConfigConst;
import com.vipkid.cache.utils.RedisClient;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.http.utils.JacksonUtils;
import com.vipkid.portal.glory.model.TeacherGlory;
import com.vipkid.portal.glory.service.TeacherGloryRestService;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.security.AppContext;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.rest.utils.UserUtils;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.proxy.RedisProxy;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by LP-813 on 2017/4/24.
 */
@RestController
@RestInterface(lifeCycle = TeacherEnum.LifeCycle.ALL)
public class TeacherGloryRestController extends RestfulController {

    private static final Logger logger = LoggerFactory.getLogger(TeacherGloryRestController.class);

    @Autowired
    private TeacherGloryRestService teacherGloryRestService;

    @RequestMapping(value = "getTeacherGlory", method = RequestMethod.GET)
    public Map<String, Object> getByTeacherId(HttpServletRequest request,String site) {
        try {
            User getUser = getUser(request);
            long userId = getUser.getId();
            String userGloryKey = CacheConfigConst.TEACHER_GLORY_KEY + "_" + userId;
            RedisProxy redisProxy = RedisClient.getInstance();
            String currentGlory = redisProxy.get(userGloryKey);
            logger.info("currentGlory:{}", currentGlory);
            String[] newGlory = teacherGloryRestService.refeshGlory(currentGlory, new Long(userId).intValue());
            logger.info("newGlory:{}", JacksonUtils.toJSONString(newGlory));
            List<TeacherGlory> ret = teacherGloryRestService.getGloryView(newGlory, userId, site);
            logger.info("viewGlory:{}", JacksonUtils.toJSONString(ret));
            String[] markedGlory = teacherGloryRestService.markShownStatus(newGlory);
            logger.info("markedGlory:{}", JacksonUtils.toJSONString(markedGlory));
            redisProxy.set(userGloryKey, StringUtils.join(markedGlory, ","));
            teacherGloryRestService.saveLog(ret);
            return ApiResponseUtils.buildSuccessDataResp(ret);
        }catch(Exception e){
            return ApiResponseUtils.buildErrorResp(1,"获取成就失败");
        }
    }
}
