package com.vipkid.trpm.service.passport;

import static com.vipkid.trpm.constant.ApplicationConstant.NEW_TEACHER_NAME;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
import com.vipkid.email.handle.EmailConfig.EmailTypeEnum;
import com.vipkid.email.templete.TempleteUtils;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.util.DateUtils;

@Deprecated
@Service
public class EmailService {

    private Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private NoticeService noticeService;

    /**
     * 发送指定类型邮件
     *
     * @Author:ALong (ZengWeiLong)
     * @param user
     * @param teacher
     * @param type
     * @return Map<String,String>
     * @date 2016年3月31日
     */
    public Map<String, String> senEmail(User user, Teacher teacher, String type) {
        if (EmailTypeEnum.ACTIVATION.toString().equals(type)) {
            return this.sendActivation(user, teacher);
        } else if (EmailTypeEnum.SUBMITBASICINFO.toString().equals(type)) {
            return this.sendApplyThx(user, teacher);
        } else if (EmailTypeEnum.CLASSNOTE.toString().equals(type)) {
            return this.sendClassNote(teacher);
        } else {
            Map<String, String> resultMap = Maps.newHashMap();
            resultMap.put("info", ApplicationConstant.AjaxCode.ERROR_CODE);
            return resultMap;
        }

    }

    private Map<String, String> sendClassNote(Teacher teacher) {
        Map<String, String> resultMap = Maps.newHashMap();
        Map<String, String> timeMap = DateUtils.yesterdayParamMap();
        log.info("检查老师明天是否有BOOK的课，参数:" + timeMap);
        try {
            Map<String, List<Map<String, Object>>> list = noticeService.findBookedClass(
                    timeMap.get("startTime"), timeMap.get("endTime"), teacher.getId());
            if (list != null && list.size() > 0) {
                log.info("需要发送邮件通知:" + teacher.getId());
                if (noticeService.emailHandle(teacher, list.get(0))) {
                    log.info("邮件发送完毕:" + teacher.getId());
                } else {
                    log.info("邮件发送失败:" + teacher.getId());
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return resultMap;
    }

    /**
     * 发送激活邮件
     *
     * @Author:ALong (ZengWeiLong)
     * @param user
     * @param teacher
     * @return Map<String,String>
     * @date 2016年3月31日
     */
    private Map<String, String> sendActivation(User user, Teacher teacher) {
        Map<String, String> resultMap = Maps.newHashMap();
        TempleteUtils templete = new TempleteUtils();
        Map<String, String> map = Maps.newHashMap();
        if (StringUtils.isEmpty(teacher.getRealName())) {
            map.put("teacherName", NEW_TEACHER_NAME);
        } else {
            map.put("teacherName", teacher.getRealName());
        }
        map.put("link", PropertyConfigurer.stringValue("teacher.www") + "activation.shtml?uuid="
                + teacher.getRecruitmentId());
        Map<String, String> tmpMap = templete.readTemplete("VIPKIDAccountActivationLink.html", map,
                "VIPKIDAccountActivationLink-Title.html");

        new EmailEngine().addMail(user.getUsername(), tmpMap, EmailFormEnum.TEACHVIP);
        resultMap.put("info", ApplicationConstant.AjaxCode.SUCCESS_CODE);

        return resultMap;
    }

    /**
     * 发送欢迎邮件
     *
     * @Author:ALong (ZengWeiLong)
     * @param user
     * @param teacher
     * @return Map<String,String>
     * @date 2016年3月31日
     */
    private Map<String, String> sendApplyThx(User user, Teacher teacher) {
        Map<String, String> resultMap = Maps.newHashMap();
        TempleteUtils templete = new TempleteUtils();
        Map<String, String> map = Maps.newHashMap();
        if (StringUtils.isEmpty(teacher.getRealName())) {
            map.put("teacherName", NEW_TEACHER_NAME);
        } else {
            map.put("teacherName", teacher.getRealName());
        }
        map.put("loginName", user.getUsername());
        Map<String, String> tmpMap =
                templete.readTemplete("Step2Apply.html", map, "Step2Apply-Title.html");

        new EmailEngine().addMail(user.getUsername(), tmpMap, EmailFormEnum.TEACHVIP);
        resultMap.put("info", ApplicationConstant.AjaxCode.SUCCESS_CODE);

        return resultMap;
    }

}
