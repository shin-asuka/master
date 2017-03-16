package com.vipkid.recruitment.basicinfo.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.google.api.client.util.Maps;
import com.google.common.base.Preconditions;
import com.vipkid.recruitment.dao.TeachingExperienceDao;
import com.vipkid.recruitment.entity.TeachingExperience;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.dto.TeachingExperienceDto;
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
        teachingExperience.setOrganisationName(HtmlUtils.htmlEscape(bean.getOrganisationName()));
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

        TeachingExperience teachingExperienceOld = teachingExperienceDao.findById(bean.getId());
        if(teachingExperienceOld == null || teachingExperienceOld.getTeacherId() != user.getId()){
            return 0L;
        }

        Preconditions.checkArgument(bean.getId() > 0);
        logger.info("userId is {}, update TeachingExperience,teachingExperienceId is:{}",user.getId(),bean.getId());
        TeachingExperience teachingExperience = new TeachingExperience();
        teachingExperience.setId(bean.getId());
        teachingExperience.setOrganisationName(HtmlUtils.htmlEscape(bean.getOrganisationName()));
        teachingExperience.setJobTitle(HtmlUtils.htmlEscape(bean.getJobTitle()));
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

    public Map<String,Object> delTeaching(long id,User user){
        logger.info("userId is {}, delete TeachingExperience,teachingExperienceId is:{}",user.getId(),id);
        if(id == 0){
            return ReturnMapUtils.returnFail("delete fail , reason id is error:"+id);
        }
        TeachingExperience teachingExperience = teachingExperienceDao.findById(id);
        if(teachingExperience == null){
            return ReturnMapUtils.returnFail("delete fail , reason id not exits:"+id);
        }
        //仅仅保存状态可以删除
        if(teachingExperience.getStatus() == TeachingExperienceDao.Status.SAVE.val()){
            if(teachingExperience.getTeacherId() == user.getId()){
                if(teachingExperienceDao.delete(teachingExperience) > 0){
                    Map<String,Object> result = Maps.newHashMap();
                    result.put("id", id);
                    return ReturnMapUtils.returnSuccess(result);
                }
            }else{
                return ReturnMapUtils.returnFail("delete fail , reason: Permissions error,id:"+id+",teacherId:"+teachingExperience.getTeacherId());
            }
        }else{
            return ReturnMapUtils.returnFail("delete fail , reason the ecperience's status already submitted:"+id);
        }
        return ReturnMapUtils.returnFail("delete fail , reason id is error:"+id);
    }
    
    public List<TeachingExperience> getTeachingList(long teacherId){
        List<TeachingExperience> list = teachingExperienceDao.findTeachingList(teacherId);
        for (TeachingExperience te : list) {
            te.setOrganisationName(HtmlUtils.htmlUnescape(te.getOrganisationName()));
            te.setJobTitle(HtmlUtils.htmlUnescape(te.getJobTitle()));
            te.setJobDescription(HtmlUtils.htmlUnescape(te.getJobDescription()));
        }
        return list;
    }
}
