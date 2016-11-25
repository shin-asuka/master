package com.vipkid.recruitment.dao;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.vipkid.recruitment.entity.TeacherOtherDegrees;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by zhangzhaojun on 2016/11/15.
 */

@Repository
public class TeacherOtherDegreesDao extends MapperDaoTemplate<TeacherOtherDegrees> {
    @Autowired
    public TeacherOtherDegreesDao(SqlSessionTemplate sqlSessionTemplate)
    {super(sqlSessionTemplate, TeacherOtherDegrees.class);}
    @Override
    public int save(TeacherOtherDegrees teacherOtherDegrees) {

        teacherOtherDegrees.setCreateId(teacherOtherDegrees.getTeacherId());
        teacherOtherDegrees.setUpdateId(teacherOtherDegrees.getTeacherId());
        teacherOtherDegrees.setCreateTime(new Timestamp(System.currentTimeMillis()));

        return super.save(teacherOtherDegrees);
    }
    public List<TeacherOtherDegrees> findByTeacherIdAndTeacherApplicationId(long teacherId,long teacherApplicationId){
        TeacherOtherDegrees teacherOtherDegrees =new TeacherOtherDegrees();
        teacherOtherDegrees.setTeacherId(teacherId);
        teacherOtherDegrees.setTeacherApplicationId(teacherApplicationId);
        return super.selectList( teacherOtherDegrees,"findByTeacherIdAndTeacherApplicationId");
    }
    public int  delete(TeacherOtherDegrees teacherOtherDegrees){
        return super.delete(teacherOtherDegrees);
    }

    public int  update(TeacherOtherDegrees teacherOtherDegrees){
        teacherOtherDegrees.setUpdateId(teacherOtherDegrees.getTeacherId());
        teacherOtherDegrees.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return super.update(teacherOtherDegrees);
    }

   public void  updateBatch(List<TeacherOtherDegrees> teacherOtherDegrees){
       super.updateBatch(teacherOtherDegrees);
   }

   public TeacherOtherDegrees findById(int id){
       TeacherOtherDegrees teacherOtherDegrees =new TeacherOtherDegrees();
       teacherOtherDegrees.setId(id);
       return super.selectOne(teacherOtherDegrees,"findById");
   }
}


