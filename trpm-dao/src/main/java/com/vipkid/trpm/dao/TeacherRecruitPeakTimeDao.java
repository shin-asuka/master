package com.vipkid.trpm.dao;

import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherRecruitPeakTime;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by pankui on 2017-04-14.
 */

@Repository
public class TeacherRecruitPeakTimeDao extends MapperDaoTemplate<TeacherRecruitPeakTime> {

    private Logger logger = LoggerFactory.getLogger(TeacherRecruitPeakTimeDao.class);

    @Autowired
    public TeacherRecruitPeakTimeDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherRecruitPeakTime.class);
    }


    /**
     * 批量保存老师约面试信息
     * @return  int 返回插入的条数
     * */
    public int saveTeacherRecruitPeakTimeBatch(List<TeacherRecruitPeakTime> list){

        return super.getSqlSession().insert("saveTeacherRecruitPeakTimeBatch", list);

    }

    /**
     * @param  teacherRecruitPeakTime 约时间数据
     * */
    public boolean isExistRecruitPeakTime(TeacherRecruitPeakTime teacherRecruitPeakTime) {

        int count = super.getSqlSession().selectOne("isExistRecruitPeakTime",teacherRecruitPeakTime);

        logger.info("查询老师约课时间是否已经存在{}",count);
        if (count>0){
            return  true;
        }

        return  false;
    }
}
