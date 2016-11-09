package com.vipkid.ua.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vipkid.rest.security.AppContext;
import com.vipkid.trpm.controller.portal.AbstractPortalController;
import com.vipkid.trpm.entity.Student;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.portal.TeacherService;
import org.apache.commons.lang.math.NumberUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.vipkid.file.utils.DateUtils;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.service.portal.OnlineClassService;
import com.vipkid.ua.model.SimpleOnlineClass;

/**
 * Created by zfl on 2016/11/9.
 */
@Controller
@RequestMapping("/onlineClass")
public class UaOnlineClassController extends AbstractPortalController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UaOnlineClassController.class);

    @Resource
    private OnlineClassService onlineClassService;
    @Resource
    private TeacherService teacherService;

    @RequestMapping("/fetchById.json")
    @ResponseBody
    public String fetchOnlineClassById(@RequestParam("onlineClassId") long onlineClassId,
            HttpServletResponse response) {
        LOGGER.info("获取OnlineClass信息，onlineClassId = {}", onlineClassId);
        Map<String, Object> result = Maps.newHashMap();
        OnlineClass onlineClass = onlineClassService.getOnlineClassById(onlineClassId);
        Teacher loginTeacher = AppContext.getTeacher();
        User user = AppContext.getUser();
        if (null == loginTeacher || null == user || null == onlineClass) {
            result.put("status", HttpStatus.OK.value());
            result.put("data", null);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return JsonTools.getJson(result);
        }
        Teacher onlineClassTeacher = teacherService.get(onlineClass.getTeacherId());
        Student student = onlineClassService.fetchStudentByOnlineClassId(onlineClassId);
        if (null == onlineClassTeacher || null == student
                || NumberUtils.compare((double) onlineClassTeacher.getId(), (double) loginTeacher.getId()) != 0) {
            result.put("status", HttpStatus.OK.value());
            result.put("data", null);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return JsonTools.getJson(result);
        }
        SimpleOnlineClass simpleOnlineClass = new SimpleOnlineClass();
        simpleOnlineClass.setId(onlineClass.getId());
        simpleOnlineClass.setScheduleDatetime(
                DateUtils.formatDateTime(onlineClass.getScheduledDateTime()));

        simpleOnlineClass.setStudentName(student.getEnglishName() + "-" + student.getName());
        simpleOnlineClass.setTeacherName(user.getName());
        result.put("status", HttpStatus.OK.value());
        result.put("data", simpleOnlineClass);
        return JsonTools.getJson(result);
    }
}
