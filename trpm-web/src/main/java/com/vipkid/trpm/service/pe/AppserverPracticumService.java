package com.vipkid.trpm.service.pe;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.community.http.client.HttpClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.Teacher;

@Service
public class AppserverPracticumService {

    private static final Logger logger = LoggerFactory.getLogger(AppserverPracticumService.class);

    private static ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3);

    private static final String FINISH_PRACTICUM_PROCESS_URL =
            PropertyConfigurer.stringValue("finish.practicum.process.url");

    @Autowired
    private TeacherDao teacherDao;

    /*
     * 调用AppServer接口完成Practicum流程的处理
     */
    public void finishPracticumProcess(long teacherApplicationId, Teacher recruitmentTeacher) {
        // 获取推荐老师的邮箱
        String refeeEmail = "";
        if (StringUtils.isNotEmpty(recruitmentTeacher.getReferee())) {
            try {
                Teacher refereeTeacher = teacherDao
                        .findById(Long.valueOf(recruitmentTeacher.getReferee().split(",")[0]));
                if (null != refereeTeacher) {
                    refeeEmail = refereeTeacher.getEmail();
                }
            } catch (Exception e) {
                logger.warn("Referee teacher email is :" + refeeEmail);
            }
        }

        // 调用接口
        Map<String, String> requestParams = Maps.newHashMap();
        requestParams.put("teacherApplicationId", String.valueOf(teacherApplicationId));
        requestParams.put("refeeEmail", refeeEmail);

        // 请求头设置
        Map<String, String> requestHeader = new HashMap<String, String>();
        String t = "TEACHER " + recruitmentTeacher.getId();
        requestHeader.put("Authorization", t + " " + DigestUtils.md5Hex(t));

        executorService.execute(() -> {
            try {
                HttpClientProxy.post(FINISH_PRACTICUM_PROCESS_URL, requestParams, requestHeader);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });
        logger.info("Invoke appserver to finish practicum [{}] process done.",
                teacherApplicationId);
    }

}
