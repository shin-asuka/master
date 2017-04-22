package com.vipkid.recruitment.dao;

import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.recruitment.entity.TeacherApplicationLog;
import com.vipkid.trpm.entity.OnlineClass;
import org.apache.commons.collections.CollectionUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

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

    public int getOnlineClassCancelNum(long teacherId, long onlineClassId, Result result){
        TeacherApplicationLog applicationLog = new TeacherApplicationLog();
        applicationLog.setTeacherId(teacherId);
        applicationLog.setOnlineClassId(onlineClassId);
        applicationLog.setResult(result.toString());
        return super.selectCount(applicationLog);
    }

}
