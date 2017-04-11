package com.vipkid.recruitment.dao;

import com.vipkid.recruitment.entity.TeacherReminder;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liuguowen on 2017/4/11.
 */
@Repository
public class TeacherReminderDao extends MapperDaoTemplate<TeacherReminder> {

    @Autowired
    public TeacherReminderDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherReminder.class);
    }

    public void saveTeacherReminders(List<TeacherReminder> teacherReminderList){
        super.saveBatch(teacherReminderList);
    }

}
