package com.vipkid.recruitment.training.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vipkid.email.EmailUtils;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherQuizEnum;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.TeacherQuiz;
import com.vipkid.trpm.entity.TeacherQuizDetails;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.trpm.entity.Teacher;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TrainingService {

    private static Logger logger = LoggerFactory.getLogger(TrainingService.class);

    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private TeacherApplicationDao teacherApplicationDao;



    private long token  = 10000;

    @Autowired
    private TeacherQuizDao teacherQuizDao;

    @Autowired
    private TeacherQuizDetailsDao teacherQuizDetailsDao;



    /**
     * Next --> 更新步骤<br/>
     * @param teacher
     * @return
     */
    public Map<String,Object> toPracticum(Teacher teacher){

        logger.info("用户：{}，更改LifeCycle",teacher.getId());
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isEmpty(listEntity)){
            return ReturnMapUtils.returnFail("You have no legal power into the next phase !");
        }

        //执行逻辑 只有在TRAINING的PASS状态才能进入
        if(TeacherApplicationEnum.Status.TRAINING.toString().equals(listEntity.get(0).getStatus())
                && TeacherApplicationEnum.Result.PASS.toString().equals(listEntity.get(0).getResult())){
            teacher.setLifeCycle(LifeCycle.PRACTICUM.toString());
            this.teacherDao.insertLifeCycleLog(teacher.getId(),LifeCycle.TRAINING,LifeCycle.PRACTICUM, teacher.getId());
            this.teacherDao.update(teacher);
            return ReturnMapUtils.returnSuccess();
        }
        return ReturnMapUtils.returnFail("You have no legal power into the next phase !");
    }


    /**
     * 更新代考記錄爲空的教師
     * @param teacherId
     */
    public void updateCheckQuiz(long teacherId){
        List<TeacherQuiz> list = this.teacherQuizDao.findAllQuiz(teacherId);
        logger.info("check quiz reslult count:" + list);
        if(CollectionUtils.isEmpty(list)){
            teacherQuizDao.insertQuiz(teacherId,teacherId);
        }
    }


    /**
     * 查詢老師是否需要考試
     * @param teacherId
     * @return
     */
    public boolean findNeedQuiz(long teacherId){
        logger.info("select need quiz list for teacherId is " + teacherId);
        List<TeacherQuiz> teacherQuiz = teacherQuizDao.findNeedQuiz(teacherId);
        return CollectionUtils.isNotEmpty(teacherQuiz);
    }


    /**
     * 開始考試
     * @param teacherId
     * @return
     */
    public long startQuiz(long teacherId){
        logger.info("teacehrId:{},开始考试",teacherId);
        //查询老师待考试记录
        List<TeacherQuiz> list = this.teacherQuizDao.findNeedQuiz(teacherId);
        if(CollectionUtils.isNotEmpty(list)){
            //更新待考记录
            Timestamp date = new Timestamp(System.currentTimeMillis());
            TeacherQuiz teacherQuiz = list.get(0);
            teacherQuiz.setStartQuizTime(date);
            teacherQuiz.setUpdateTime(date);
            teacherQuiz.setUpdateId(teacherId);
            //更新当前考试记录
            this.teacherQuizDao.update(teacherQuiz);
            logger.info("teacehrId:{},开始考试更新成功，quizId:{},toekn:{}",teacherId,teacherQuiz.getId(),(teacherQuiz.getStartQuizTime().getTime()/token));
            return (teacherQuiz.getStartQuizTime().getTime()/token);
        }
        return 0;
    }


    /**
     * 保存考試記錄
     * @param teacherId
     * @param grade
     * @param quizToken
     * @return
     */
    public boolean saveQuizResult(long teacherId,String grade,long quizToken){
        Teacher teacher  = teacherDao.findById(teacherId);
        logger.info("teacehrId:{},提交分数:{}",teacherId, grade);
        //查询老师待考试记录
        List<TeacherQuiz> list = this.teacherQuizDao.findNeedQuiz(teacherId);
        if(CollectionUtils.isNotEmpty(list)){
            //更新待考记录
            TeacherQuiz teacherQuiz = list.get(0);
            if(quizToken == (teacherQuiz.getStartQuizTime().getTime()/token)){
                int quizScore = this.saveQuizDetals(teacherId,teacherQuiz,grade);
                teacherQuiz.setQuizScore(quizScore);
                teacherQuiz.setQuizTime(System.currentTimeMillis() - teacherQuiz.getStartQuizTime().getTime());
                teacherQuiz.setUpdateTime(new Date());
                teacherQuiz.setUpdateId(teacherId);
                teacherQuiz.setStatus(quizScore < RestfulConfig.Quiz.NEW_QUIZ_PASS_SCORE? TeacherQuizEnum.Status.FAIL.val():TeacherQuizEnum.Status.PASS.val());
                //更新当前考试记录
                this.teacherQuizDao.update(teacherQuiz);
                // 插入新的待考记录
                if(quizScore < RestfulConfig.Quiz.NEW_QUIZ_PASS_SCORE){
                    logger.info("teacehrId:{},提交考试结果，quizId:{} 没通过,新增一条考试记录",teacherId,teacherQuiz.getId(),teacherQuiz.getStatus());
                    this.teacherQuizDao.insertQuiz(teacherId,teacherId);
                }else if(TeacherApplicationEnum.Status.TRAINING.toString().equalsIgnoreCase(teacher.getLifeCycle())){
                    List<TeacherApplication> old_teacherlist = teacherApplicationDao.findCurrentApplication(teacherId);
                    if (CollectionUtils.isNotEmpty(old_teacherlist)) {
                        logger.info("用户：{}执行teacherApplicationDao.update操作", teacherId);
                        for (int i = 0; i < old_teacherlist.size(); i++) {
                            TeacherApplication application = old_teacherlist.get(i);
                            application.setCurrent(0);
                            this.teacherApplicationDao.update(application);
                        }
                    }
                    TeacherApplication application = new TeacherApplication();
                    application.setTeacherId(teacherId);//  步骤关联的教师

                    application.setApplyDateTime(new Timestamp(System.currentTimeMillis()));
                    application.setAuditDateTime(new Timestamp(System.currentTimeMillis()));

                    application.setAuditorId(RestfulConfig.SYSTEM_USER_ID);

                    application.setResult(TeacherApplicationEnum.Result.PASS.toString());
                    application.setStatus(TeacherApplicationEnum.Status.TRAINING.toString());
                    application = teacherApplicationDao.initApplicationData(application);
                    this.teacherApplicationDao.save(application);
                    //教师通过考试发通知邮件
                    EmailUtils.sendEmail4TrainingPass(teacher, quizScore);

                }
                logger.info("teacehrId:{},提交考试结果，quizId:{},result:{} ",teacherId,teacherQuiz.getId(),teacherQuiz.getStatus());
                return true;
            }else{
                logger.info("teacehrId:{},提交考试结果，quizId:{},token不匹配,请求token:{},实际token:{}",teacherId,teacherQuiz.getId(),quizToken,(teacherQuiz.getStartQuizTime().getTime()/token));
                return false;
            }
        }
        return false;
    }


    /**
     * 保存考試的詳細，算出 考試分數
     * @param teacherId
     * @param teacherQuiz
     * @param grade
     * @return
     */
    private int saveQuizDetals(long teacherId,TeacherQuiz teacherQuiz,String grade){
        logger.info("teacehrId:{},保存考试结果，quizId:{} ",teacherId,teacherQuiz.getId());
        int quizScore = 0;

        if(StringUtils.isEmpty(grade)){
            logger.warn("老师成绩提交转化结果是Null");
            return quizScore;
        }

        List<TeacherQuizDetails> list = JsonTools.readValue(grade, new TypeReference<List<TeacherQuizDetails>>(){});

        if(CollectionUtils.isEmpty(list)){
            logger.warn("老师成绩提交为Null");
            return quizScore;
        }

        for (TeacherQuizDetails details:list) {
            details.setTeacherId(teacherId);
            details.setQuizId(teacherQuiz.getId());
            details.setCorrectAnswer(RestfulConfig.Quiz.NEW_CORRECT_ANSWER_MAP.get(details.getSn()));
            int score = details.getCorrectAnswer() == details.getTeacherAnswer() ? 5 : 0;
            details.setScore(score);
            quizScore += score;
            logger.info("sn:{},teacherId:{},quizId:{},correctAnswer:{},teacherAnswer:{},score:{}",
                    details.getSn(),details.getTeacherId(),details.getQuizId(),details.getCorrectAnswer(),details.getTeacherAnswer(),details.getScore());

            teacherQuizDetailsDao.save(details);
        }

        return quizScore;
    }


    /**
     * 得到最後的考試信息
     * @param teacherId
     * @return
     */
    public List<TeacherQuiz> getLastQuiz(long teacherId){
        logger.info("select quiz list for teacherId is " + teacherId);
        return this.teacherQuizDao.getLastQuiz(teacherId);
    }

}



