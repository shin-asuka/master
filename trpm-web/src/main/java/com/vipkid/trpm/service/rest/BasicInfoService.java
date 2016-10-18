package com.vipkid.trpm.service.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.templete.TempleteUtils;
import com.vipkid.enums.UserEnum;
import com.vipkid.rest.app.BasicInfoBean;
import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeachingExperienceDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeachingExperience;
import com.vipkid.trpm.entity.User;

@Service
public class BasicInfoService {
    
    @Autowired
    private TeachingExperienceDao teachingExperienceDao;
    
    @Autowired
    private TeacherDao teacherDao;
    
    @Autowired    
    private UserDao userDao;
    
    @Autowired
    private TeacherAddressDao teacherAddressDao;
    

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
     * 1.插入地址表
     * 2.招聘渠道逻辑(实体里面)
     * 3.国籍替换
     * 4.修改Teacher表
     * 5.修改User表
     * @Author:ALong (ZengWeiLong)
     * @param bean
     * @param user
     * @param teacher
     * @return    
     * Map<String,Object>
     * @date 2016年10月17日
     */
    public Map<String,Object> submitInfo(BasicInfoBean bean,User user){       
        Teacher teacher = this.teacherDao.findById(user.getId());
        //更新Address
        TeacherAddress teacherAddress = new TeacherAddress();
        teacherAddress.setCountryId(bean.getCountryId());
        teacherAddress.setStateId(bean.getStateId());
        teacherAddress.setCity(bean.getCityId());
        teacherAddress.setStreetAddress(bean.getStreetAddress());
        teacherAddress.setZipCode(bean.getZipCode());
        this.teacherAddressDao.updateOrSave(teacherAddress);
        //更新Teacher
        teacher.setCurrentAddressId(teacherAddress.getId());
        teacher.setTimezone(bean.getTimezone());
        teacher.setCountry(bean.getNationality());
        teacher.setPhoneNationCode(bean.getPhoneNationCode());
        teacher.setPhoneNationId(bean.getPhoneNationId());
        teacher.setMobile(bean.getMobile());
        teacher.setPhoneType(bean.getPhoneType());
        teacher.setHighestLevelOfEdu(bean.getHighestLevelOfEdu());
        //AutoFail Pass TeacherApplication
        teacher = this.autoFail(teacher);
        this.teacherDao.update(teacher);
        //更新User
        user.setGender(bean.getGender());
        this.userDao.update(user);
        return Maps.newHashMap();
    }
    
    
    /**
     * 1.更新教育经验表
     * 2.Auto Fail 逻辑
     * 3.邮件 
     * 4.更新TeacherApplication
     * @Author:ALong (ZengWeiLong)
     * @param teacher
     * @return    
     * Teacher
     * @date 2016年10月18日
     */
    private Teacher autoFail(Teacher teacher){
        List<TeachingExperience> list = teachingExperienceDao.findTeachingList(teacher.getId());
        
        return teacher;
    }
}
