package com.vipkid.trpm.dao;

import java.util.Date;
import java.util.List;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.enums.TeacherQuizEnum;
import com.vipkid.trpm.entity.TeacherQuiz;

@Repository
public class TeacherQuizDao extends MapperDaoTemplate<TeacherQuiz>{
    
    private Logger logger = LoggerFactory.getLogger(TeacherQuizDao.class);

    @Autowired
    public TeacherQuizDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherQuiz.class);
    }
    /**
     * 查询teacherId 考试的列表 
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @return    
     * List<TeacherQuiz>
     * @date 2016年8月18日
     */;
    public List<TeacherQuiz> getLastQuiz(long teacherId){
        TeacherQuiz teacherQuiz = new TeacherQuiz();
        teacherQuiz.setTeacherId(teacherId);
        teacherQuiz.setAndwhere(" AND status > " + TeacherQuizEnum.Status.NOQUIZ.val());
        teacherQuiz.setStatus(-1);
        teacherQuiz.setOrderString(" id DESC ");
        return super.selectList(teacherQuiz);
    }
    
    /**
     * 查询是否有需要考试的记录
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @return    
     * List<TeacherQuiz>
     * @date 2016年8月18日
     */
    public List<TeacherQuiz> findNeedQuiz(long teacherId){
        TeacherQuiz teacherQuiz = new TeacherQuiz();
        teacherQuiz.setTeacherId(teacherId);
        teacherQuiz.setStatus(TeacherQuizEnum.Status.NOQUIZ.val());
        teacherQuiz.setOrderString(" id DESC ");
        return super.selectList(teacherQuiz);
    }
    
    
    public int update(TeacherQuiz teacherQuiz){
        return super.update(teacherQuiz);
    }
    
    /**
     * 插入一条新的考试记录 
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @return    
     * int
     * @date 2016年8月18日
     */
    public int insertQuiz(long teacherId,long passId){
        logger.info("新增一条考试记录:{}",teacherId);
        TeacherQuiz teacherQuiz = new TeacherQuiz();
        teacherQuiz.setTeacherId(teacherId);
        teacherQuiz.setCreationTime(new Date());
        teacherQuiz.setUpdateId(teacherId);
        teacherQuiz.setUpdateTime(new Date());
        teacherQuiz.setQuizScore(0);
        return super.save(teacherQuiz);
    }
    
}
