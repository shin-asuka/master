package com.vipkid.trpm.service.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.templete.TempleteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.enums.UserEnum;
import com.vipkid.rest.app.BasicInfoBean;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeachingExperienceDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;

@Service
public class BasicInfoService {

    @Autowired
    private TeachingExperienceDao teachingExperienceDao;
    
    @Autowired
    private TeacherDao teacherDao;

    private void sendEmail4UndoFail(Teacher teacher) {
        Map<String, String> paramsMap = new HashMap();
        paramsMap.put("teacherName", teacher.getRealName());

        Map<String, String> emailMap = new TempleteUtils().readTemplete("BasicInfoUndoFail.html", paramsMap, "BasicInfoUndoFailTitle.html");
        new EmailEngine().addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
    }

    public List<Map<String,Object>> getRecruitmentChannelList(){
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("type", "TEACHER_RECRUITMENT");
        paramMap.put("status", UserEnum.Status.NORMAL);
        paramMap.put("dtype", "Partner");
        return this.teachingExperienceDao.findRecruitingChannel(paramMap);
    }
    
    /**
     * 1.修改User表
     * 2.插入地址表
     * 3.招聘渠道逻辑
     * 4.更新教育经验表
     * 5.Auto Fail 逻辑 及邮件
     * 6.国籍替换
     * 7.修改Teacher表
     * @Author:ALong (ZengWeiLong)
     * @param bean
     * @param user
     * @param teacher
     * @return    
     * Map<String,Object>
     * @date 2016年10月17日
     */
    public Map<String,Object> submitInfo(BasicInfoBean bean,User user,Teacher teacher){       
        
        return Maps.newHashMap();
    }
    
}
