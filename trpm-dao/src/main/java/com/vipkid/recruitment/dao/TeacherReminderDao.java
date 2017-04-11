package com.vipkid.recruitment.dao;

import com.google.common.base.Preconditions;
import com.vipkid.recruitment.entity.TeacherReminder;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
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

    public void saveTeacherReminders(List<TeacherReminder> teacherReminderList) {
        super.saveBatch(teacherReminderList);
    }

    public List<TeacherReminder> findBySendScheduledTime(Date sendScheduledTime) {
        TeacherReminder teacherReminder = new TeacherReminder();
        teacherReminder.setSendScheduledTime(sendScheduledTime);
        return super.selectList(teacherReminder);
    }

    public int deleteTeacherReminder(Long id) {
        Preconditions.checkNotNull(id);
        TeacherReminder teacherReminder = new TeacherReminder();
        teacherReminder.setId(id);
        return super.delete(teacherReminder);
    }

}
