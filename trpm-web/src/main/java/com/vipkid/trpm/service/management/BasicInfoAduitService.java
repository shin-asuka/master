package com.vipkid.trpm.service.management;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.vipkid.email.EmailUtils;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.dao.TeachingExperienceDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.trpm.constant.ApplicationConstant.TeacherLifeCycle;
import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherLocationDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherLocation;
import com.vipkid.trpm.util.DateUtils;

@Service
public class BasicInfoAduitService {
    
    private static Logger logger = LoggerFactory.getLogger(BasicInfoAduitService.class);

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    
    @Autowired
    private TeacherDao teacherDao;
    
    @Autowired
    private TeacherAddressDao teacherAddressDao;
    
    @Autowired
    private TeachingExperienceDao teachingExperienceDao;
    
    @Autowired
    private TeacherLocationDao teacherLocationDao;
    
    /**
     * Review 
     * @Author:ALong (ZengWeiLong)
     * @param teacherApplicationId
     * @return    
     * Map<String,Object>
     * @date 2016年10月19日
     */
    public Map<String,Object> basicReview(long teacherApplicationId){
        Map<String,Object> result = Maps.newHashMap();
        
        TeacherApplication teacherApplication = this.teacherApplicationDao.findApplictionById(teacherApplicationId);
        if(teacherApplication == null){
            result.put("status",false);
            return result;
        }
        if(!StringUtils.equals(teacherApplication.getStatus(), TeacherLifeCycle.BASIC_INFO)){
            result.put("status",false);
            return result;
        }
        result.put("list", this.teachingExperienceDao.findTeachingList(teacherApplication.getTeacherId()));
        result.put("changeStatus",false);
        result.put("failReason",teacherApplication.getFailedReason());
        result.put("status",teacherApplication.getStatus());
        result.put("remark",teacherApplication.getComments());
        //只有Fail状态的才显示 change fail to pass 按钮
        if(StringUtils.equals(teacherApplication.getResult(), TeacherApplicationDao.Result.FAIL.toString())){
            result.put("changeStatus",true);
            //超过11.5小时不能修改
            Date auditDate = teacherApplication.getAuditDateTime(); 
            if(auditDate == null){
                result.put("changeStatus",false);
            }else{
                long auditTime = auditDate.getTime();
                if(DateUtils.count11hrlf(auditTime)){
                    result.put("changeStatus",false);
                }
            }
            //已经修改过不能修改
            if(StringUtils.isNotBlank(teacherApplication.getComments())){
                result.put("changeStatus",false);
            }
        }
        Teacher teacher = this.teacherDao.findById(teacherApplication.getTeacherId());
        Map<String,Object> teacherMap = Maps.newHashMap();
        teacherMap.put("fullName", teacher.getRealName());
        teacherMap.put("timezone", teacher.getTimezone());
        teacherMap.put("recruitmentChannel", teacher.getTimezone());
        teacherMap.put("nationality", teacher.getCountry());
        teacherMap.put("highestLevelOfEdu", teacher.getHighestLevelOfEdu());
        TeacherAddress teacherAddress = teacherAddressDao.getTeacherAddress(teacher.getCurrentAddressId());
        teacherMap.put("currentLocation", this.getAddressString(teacherAddress));
        result.put("teacher",teacherMap);
        return result;
    }
    
    /**
     * 根据  TeacherAddress 对象获取地址字符串
     * @Author:ALong (ZengWeiLong)
     * @param teacherAddress
     * @return    
     * String
     * @date 2016年10月19日
     */
    private String getAddressString(TeacherAddress teacherAddress){
        StringBuffer address = new StringBuffer("");
        if(teacherAddress != null){
            if(teacherAddress.getCountryId() != 0){
                TeacherLocation country = this.teacherLocationDao.findById(teacherAddress.getCountryId());
                if(country != null)
                    address.append(" " +country.getName());  
            }
            if(teacherAddress.getCity() != 0){
                TeacherLocation city = this.teacherLocationDao.findById(teacherAddress.getCity());
                if(city != null)
                    address.append(" " +city.getName()); 
            }
            if(teacherAddress.getStateId() != 0){
                TeacherLocation state = this.teacherLocationDao.findById(teacherAddress.getStateId());
                if(state != null)
                    address.append(" " +state.getName()); 
            }
            address.append(" " +teacherAddress.getStreetAddress());
        }
        return address.toString();
    }
    
    public Map<String,Object> changeStatus(long teacherApplicationId,long userId,String remark){
        Map<String,Object> result =Maps.newHashMap();
        result.put("id", teacherApplicationId);
        TeacherApplication teacherApplication = this.teacherApplicationDao.findApplictionById(teacherApplicationId);
        if(teacherApplication == null){
            logger.warn("没有找到TeacherApplication,appId:{}",teacherApplicationId);
            result.put("status", false);
            return result;
        }
        Teacher teacher = this.teacherDao.findById(userId);
        if(teacher == null){
            logger.warn("没有找到Teacher,userId:{}",userId);
            result.put("status", false);
            return result;
        }
        //PASS的不能操作
        if(StringUtils.equals(teacherApplication.getResult(), TeacherApplicationDao.Result.PASS.toString())){
            logger.warn("PASS的不能操作,TeacherApplication:{}",teacherApplicationId);
            result.put("status",false);
            return result;
        }
        //超过11.5小时不能修改
        long auditTime = teacherApplication.getAuditDateTime().getTime();
        if(DateUtils.count11hrlf(auditTime)){
            logger.warn("超过11.5小时不能修改,TeacherApplication:{}",teacherApplicationId);
            result.put("status",false);
        }
        //已经修改过不能修改
        if(StringUtils.isNotBlank(teacherApplication.getComments())){
            logger.warn("已经修改过不能修改,TeacherApplication:{}",teacherApplicationId);
            result.put("status",false);
        }
        teacherApplication.setResult(TeacherApplicationDao.Result.PASS.toString());
        teacherApplication.setAuditDateTime(new Timestamp(System.currentTimeMillis()));
        teacherApplication.setAuditorId(teacher.getId());
        teacherApplication.setComments(remark);
        //更新application
        this.teacherApplicationDao.update(teacherApplication); 
        teacher.setLifeCycle(TeacherLifeCycle.INTERVIEW);
        //更新Teacher
        this.teacherDao.update(teacher);
        //插入insert log
        this.teacherDao.insertLifeCycleLog(teacher.getId(), TeacherLifeCycle.BASIC_INFO, TeacherLifeCycle.INTERVIEW, userId);
        //发送邮件
        EmailUtils.sendEmail4BasicInfoPass(teacher);
        result.put("status", true);
        return result;
    }
}
