package com.vipkid.trpm.service.rest;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.vipkid.trpm.dao.TeachingExperienceDao;
import com.vipkid.trpm.entity.TeachingExperience;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.util.DateUtils;

/**
 * 教师教育经验 
 * @author Along(ZengWeiLong)
 * @ClassName: TeachingExperienceService 
 * @date 2016年10月17日 下午5:14:59 
 *
 */
@Service
public class TeachingExperienceService {
    
    
    private static Logger logger = LoggerFactory.getLogger(TeachingExperienceService.class);

    @Autowired
    private TeachingExperienceDao teachingExperienceDao;
    
    public long saveTeaching(TeachingExperience teachingExperience,User user){
        teachingExperience.setCreateId(user.getId());
        teachingExperience.setCreateTime(new Timestamp(System.currentTimeMillis()));
        teachingExperience.setUpdateId(user.getId());
        teachingExperience.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        teachingExperience.setStatus(TeachingExperienceDao.Status.SAVE.val());
        teachingExperience.setTotalHours(DateUtils.countWeeks(teachingExperience.getTimePeriodStart().getTime(), teachingExperience.getTimePeriodEnd().getTime())*teachingExperience.getHoursWeek());
        if(teachingExperienceDao.save(teachingExperience) > 0 ){
            return teachingExperience.getId();
        }
        return 0L;
    }
    
    public long updateTeaching(TeachingExperience teachingExperience,User user){
        Preconditions.checkArgument(teachingExperience.getId() > 0);
        logger.info("userId is {}, update TeachingExperience,teachingExperienceId is:{}",user.getId(),teachingExperience.getId());
        teachingExperience.setCreateId(user.getId());
        teachingExperience.setCreateTime(new Timestamp(System.currentTimeMillis()));
        teachingExperience.setUpdateId(user.getId());
        teachingExperience.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        teachingExperience.setStatus(TeachingExperienceDao.Status.SAVE.val());
        teachingExperience.setTotalHours(DateUtils.countWeeks(teachingExperience.getTimePeriodStart().getTime(), teachingExperience.getTimePeriodEnd().getTime())*teachingExperience.getHoursWeek());
        if(teachingExperienceDao.update(teachingExperience) > 0){
            return teachingExperience.getId();
        }
        return 0L;        
    }

    public long delTeaching(long id,User user){
        Preconditions.checkArgument(id > 0);
        logger.info("userId is {}, delete TeachingExperience,teachingExperienceId is:{}",user.getId(),id);
        TeachingExperience teachingExperience = teachingExperienceDao.findById(id);
        //仅仅保存状态可以删除
        if(teachingExperience.getStatus() == TeachingExperienceDao.Status.SAVE.val()){
            if(teachingExperienceDao.delete(teachingExperience) > 0){
                return id;
            }
        }
        return 0L;
    }
    
    public List<TeachingExperience> getTeachingList(long teacherId){
        return teachingExperienceDao.findTeachingList(teacherId);
    }
    
}
