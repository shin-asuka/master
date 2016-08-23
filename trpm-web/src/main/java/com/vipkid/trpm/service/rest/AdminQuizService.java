package com.vipkid.trpm.service.rest;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipkid.enums.TeacherQuizEnum;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.dao.TeacherQuizDao;
import com.vipkid.trpm.entity.TeacherQuiz;

@Service
public class AdminQuizService {

    private static Logger logger = LoggerFactory.getLogger(AdminQuizService.class);
    
    @Autowired
    private TeacherQuizDao teacherQuizDao;
    
    /**
     * 查询用户最后一次有结果的考试记录 
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @return    
     * List<TeacherQuiz>
     * @date 2016年8月18日
     */
    public List<TeacherQuiz> getLastQuiz(long teacherId){
       logger.info("select quiz list for teacherId is " + teacherId);
       return this.teacherQuizDao.getLastQuiz(teacherId);
    }
    
    /**
     * 查询用户是否存在待考记录
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @return    
     * List<TeacherQuiz>
     * @date 2016年8月18日
     */
    public boolean findNeedQuiz(long teacherId){
        logger.info("select need quiz list for teacherId is " + teacherId);
        List<TeacherQuiz> list = this.teacherQuizDao.findNeedQuiz(teacherId);
        return CollectionUtils.isNotEmpty(list);
    }
    
    /**
     * // 保存考试结果,大于60分更新当前数据PASS通过，不插入新的考试
     * // 小于60分则更新当前数据，并插入新的考试记录
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @param quizScore
     * @return    
     * boolean
     * @date 2016年8月18日
     */
    public boolean saveQuizResult(long teacherId,int quizScore){
        //查询老师待考试记录
        List<TeacherQuiz> list = this.getLastQuiz(teacherId);
        if(CollectionUtils.isNotEmpty(list)){
            //更新待考记录
            TeacherQuiz teacherQuiz = list.get(0);
            teacherQuiz.setQuizScore(quizScore);
            teacherQuiz.setUpdateTime(new Date());
            teacherQuiz.setUpdateId(teacherId);
            teacherQuiz.setStatus(quizScore < RestfulConfig.QUIZ_PASS_SCORE?TeacherQuizEnum.Status.FAIL.val():TeacherQuizEnum.Status.PASS.val());
            //更新当前考试记录
            this.teacherQuizDao.update(teacherQuiz);
            // 插入新的待考记录
            if(quizScore < RestfulConfig.QUIZ_PASS_SCORE){
              this.teacherQuizDao.insertQuiz(teacherId);
            }
        }
        return true;
    }
    
}
