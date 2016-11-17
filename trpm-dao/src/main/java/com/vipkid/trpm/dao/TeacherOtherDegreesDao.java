package com.vipkid.trpm.dao;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.vipkid.trpm.entity.TeacherOtherDegrees;
import java.sql.Timestamp;
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

}


