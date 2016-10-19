package com.vipkid.trpm.service.management;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.dao.TeacherApplicationDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherLocationDao;
import com.vipkid.trpm.dao.TeachingExperienceDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherApplication;
import com.vipkid.trpm.entity.TeacherLocation;
import com.vipkid.trpm.util.DateUtils;

@Service
public class BasicInfoAduitService {

    @Autowired
    private TeacherApplicationDao teacerApplicationDao;
    
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
        result.put("changeStatus",true);
        TeacherApplication teacherApplication = this.teacerApplicationDao.findApplictionById(teacherApplicationId);
        result.put("list", this.teachingExperienceDao.findTeachingList(teacherApplication.getTeacherId()));
        result.put("failReason",teacherApplication.getFailedReason());
        result.put("status",teacherApplication.getStatus());
        result.put("remark",teacherApplication.getComments());
        long auditTime = teacherApplication.getAuditDateTime().getTime();
        //超过12小时不能修改
        if(DateUtils.count11hrlf(auditTime)){
            result.put("changeStatus",false);
        }
        //已经修改过不能修改
        if(StringUtils.isNotBlank(teacherApplication.getComments())){
            result.put("changeStatus",false);
        }
        Teacher teacher = this.teacherDao.findById(teacherApplication.getTeacherId());
        Map<String,Object> teacherMap = Maps.newHashMap();
        teacherMap.put("fullName", teacher.getRealName());
        teacherMap.put("timezone", teacher.getTimezone());
        teacherMap.put("recruitmentChannel", teacher.getTimezone());
        teacherMap.put("country", teacher.getCountry());
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
}
