package com.vipkid.recruitment.dao;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.recruitment.entity.TeacherApplicationLog;
import com.vipkid.trpm.entity.OnlineClass;

@Repository
public class TeacherApplicationLogDao extends MapperDaoTemplate<TeacherApplicationLog>{

    @Autowired
    public TeacherApplicationLogDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherApplicationLog.class);
    }
 
    public List<TeacherApplicationLog> selectList(TeacherApplicationLog bean){
        return super.selectList(bean);
    }
    
    public int getCancelNum(long teacherId,String status,Result result){
        TeacherApplicationLog bean = new TeacherApplicationLog();
        bean.setTeacherId(teacherId);
        bean.setStatus(status);
        bean.setResult(result.toString());
        List<TeacherApplicationLog> listLog = this.selectList(bean);
        if(CollectionUtils.isNotEmpty(listLog)){
            return listLog.size();
        }
        return 0;
    }
    
    public void saveCancel(long teacherId,long teacherApplicationId, Status status,Result result,OnlineClass onlineClass){
        long time = System.currentTimeMillis();
        TeacherApplicationLog bean = new TeacherApplicationLog();
        bean.setTeacherId(teacherId);
        bean.setStatus(status.toString());
        bean.setResult(result.toString());
        bean.setOnlineClassId(onlineClass.getId());
        bean.setScheduleDateTime(onlineClass.getScheduledDateTime());
        bean.setTeacherApplicationId(teacherApplicationId);
        bean.setCreateId(teacherId);
        bean.setUpdateId(teacherId);
        bean.setCreateTime(new Timestamp(time));
        bean.setUpdateTime(new Timestamp(time));
        this.save(bean);
    }
}
