package com.vipkid.trpm.service.recruitment;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.google.common.base.Preconditions;
import com.vipkid.rest.dto.TeachingExperienceDto;
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
    
    public long saveTeaching(TeachingExperienceDto bean,User user){
        TeachingExperience teachingExperience = new TeachingExperience();
        teachingExperience.setOrganisationName(bean.getOrganisationName());
        teachingExperience.setTeacherId(user.getId());
        teachingExperience.setJobTitle(HtmlUtils.htmlEscape(bean.getJobTitle()));
        teachingExperience.setTimePeriodStart(new Timestamp(bean.getTimePeriodStart()));
        teachingExperience.setTimePeriodEnd(new Timestamp(bean.getTimePeriodEnd()));
        teachingExperience.setHoursWeek(bean.getHoursPerWeek());
        teachingExperience.setJobDescription(HtmlUtils.htmlEscape(bean.getJobDescription()));
        teachingExperience.setCreateId(user.getId());
        teachingExperience.setCreateTime(new Timestamp(System.currentTimeMillis()));
        teachingExperience.setUpdateId(user.getId());
        teachingExperience.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        teachingExperience.setStatus(TeachingExperienceDao.Status.SAVE.val());
        teachingExperience.setTotalHours(DateUtils.countWeeks(bean.getTimePeriodStart(), bean.getTimePeriodEnd())*bean.getHoursPerWeek());
        if(teachingExperienceDao.save(teachingExperience) > 0 ){
            return teachingExperience.getId();
        }
        return 0L;
    }
    
    public long updateTeaching(TeachingExperienceDto bean,User user){
        Preconditions.checkArgument(bean.getId() > 0);
        logger.info("userId is {}, update TeachingExperience,teachingExperienceId is:{}",user.getId(),bean.getId());
        TeachingExperience teachingExperience = new TeachingExperience();
        teachingExperience.setId(bean.getId());
        teachingExperience.setOrganisationName(HtmlUtils.htmlEscape(bean.getOrganisationName()));
        teachingExperience.setJobTitle(bean.getJobTitle());
        teachingExperience.setTimePeriodStart(new Timestamp(bean.getTimePeriodStart()));
        teachingExperience.setTimePeriodEnd(new Timestamp(bean.getTimePeriodEnd()));
        teachingExperience.setHoursWeek(bean.getHoursPerWeek());
        teachingExperience.setJobDescription(HtmlUtils.htmlEscape(bean.getJobDescription()));
        
        teachingExperience.setUpdateId(user.getId());
        teachingExperience.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        teachingExperience.setStatus(TeachingExperienceDao.Status.SAVE.val());
        teachingExperience.setTotalHours(DateUtils.countWeeks(bean.getTimePeriodStart(), bean.getTimePeriodEnd())*bean.getHoursPerWeek());
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
            if(teachingExperience.getTeacherId() == user.getId()){
                if(teachingExperienceDao.delete(teachingExperience) > 0){
                    return id;
                }
            }else{
                logger.warn("不能删除非自己的教育经验teacherId:{},id:{}",user.getId(),id);    
            }
        }else{
            logger.warn("已经提交的数据不能删除id:{}",id);
        }
        return 0L;
    }
    
    public List<TeachingExperience> getTeachingList(long teacherId){
        return teachingExperienceDao.findTeachingList(teacherId);
    }
}
