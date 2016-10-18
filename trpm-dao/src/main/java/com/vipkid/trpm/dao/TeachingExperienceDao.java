package com.vipkid.trpm.dao;

import java.util.List;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.base.Preconditions;
import com.vipkid.trpm.entity.TeachingExperience;

@Repository
public class TeachingExperienceDao extends MapperDaoTemplate<TeachingExperience>{

    @Autowired
    public TeachingExperienceDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeachingExperience.class);
    }
    
    public enum Status{
        
        SAVE(1),
        
        SUBMIT(2);
        
        private Integer val;    
        
        private Status(Integer val) {
            this.val = val;
        }
        
        public Integer val() {
            return val;
        }
    }
    
    /**
     * 获取老师的工作经历列表 
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @return    
     * List<TeachingExperience>
     * @date 2016年10月14日
     */
    public List<TeachingExperience> findTeachingList(long teacherId){
        TeachingExperience teachingExperience = new TeachingExperience();
        teachingExperience.setTeacherId(teacherId);
        return super.selectList(teachingExperience);
    }
        
    public int save(TeachingExperience teachingExperience){
        return super.save(teachingExperience);
    }
    
    public int update(TeachingExperience teachingExperience){
        Preconditions.checkArgument(teachingExperience.getId() > 0);
        return super.update(teachingExperience);
    }
    
    public int delete(TeachingExperience teachingExperience){
        return super.delete(teachingExperience);
    }
    
    public TeachingExperience findById(long id){
        TeachingExperience teachingExperience = new TeachingExperience();
        teachingExperience.setId(id);
        return super.selectOne(teachingExperience);
    }
    
    /**
     * 获取招聘渠道列表 
     *  
     * @Author:ALong (ZengWeiLong)
     * @param paramMap
     * @return    
     * List<Map<String,Object>>
     * @date 2016年10月14日
     */
    public List<Map<String,Object>> findRecruitingChannel(Map<String,Object> paramMap){
        return super.listEntity("findRecruitingChannel", paramMap);
    }
}
