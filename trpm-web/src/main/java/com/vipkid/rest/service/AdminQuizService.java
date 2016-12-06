package com.vipkid.rest.service;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vipkid.email.EmailUtils;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherPageLoginEnum.LoginType;
import com.vipkid.enums.TeacherQuizEnum;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.dao.AppRestfulDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherPageLoginDao;
import com.vipkid.trpm.dao.TeacherQuizDao;
import com.vipkid.trpm.dao.TeacherQuizDetailsDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherPageLogin;
import com.vipkid.trpm.entity.TeacherQuiz;
import com.vipkid.trpm.entity.TeacherQuizDetails;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.security.SHA256PasswordEncoder;

@Service
public class AdminQuizService {

    private static Logger logger = LoggerFactory.getLogger(AdminQuizService.class);
    
    private long token  = 10000;
    
    @Autowired
    private TeacherQuizDao teacherQuizDao;
    
    @Autowired
    private TeacherQuizDetailsDao teacherQuizDetailsDao;

    @Autowired    
    private TeacherPageLoginDao teacherPageLoginDao;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private TeacherDao teacherDao;
    
    @Autowired
    private AppRestfulDao appRestfulDao;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    /**
     * is first quiz 
     * @Author:ALong (ZengWeiLong)
     * @param teacerId
     * @return    
     * TeacherPageLogin
     * @date 2016年8月25日
     */
    public boolean openQuiz(long teacerId){
        TeacherPageLogin teacherPageLogin = this.teacherPageLoginDao.findByUserIdAndLoginType(teacerId, LoginType.ADMINQUIZ.val());
        return teacherPageLogin == null ? true : false;
    }
    
    
    /**
     * 点击保存admin quiz 
     * @Author:ALong (ZengWeiLong)
     * @param teacerId
     * @return    
     * boolean
     * @date 2016年8月25日
     */
    public boolean saveOpenQuiz(long teacerId){
        TeacherPageLogin teacherPageLogin = new TeacherPageLogin();
        teacherPageLogin.setUserId(teacerId);
        teacherPageLogin.setLoginType(LoginType.ADMINQUIZ.val());
        return this.teacherPageLoginDao.saveTeacherPageLogin(teacherPageLogin) == 1 ? true : false;
    }
   
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
     *  检查老师是否有考试记录
     *  没有则插入一条新的待考记录
     * @param teacherId    
     * void
     */
    public void updateCheckQuiz(long teacherId){
        List<TeacherQuiz> list = this.teacherQuizDao.findAllQuiz(teacherId);
        logger.info("check quiz reslult count:" + list);
        if(CollectionUtils.isEmpty(list)){
            teacherQuizDao.insertQuiz(teacherId,teacherId);
        }
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
        List<TeacherQuiz> teacherQuiz = teacherQuizDao.findNeedQuiz(teacherId);
        return CollectionUtils.isNotEmpty(teacherQuiz);
    }
    
    /**
     * // 提交考试结果,大于80分更新当前数据PASS通过，不插入新的考试
     * // 小于60分则更新当前数据，并插入新的考试记录
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @param quizToken
     * @return    
     * boolean
     * @date 2016年8月18日
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
                teacherQuiz.setStatus(quizScore < RestfulConfig.Quiz.QUIZ_PASS_SCORE?TeacherQuizEnum.Status.FAIL.val():TeacherQuizEnum.Status.PASS.val());
                //更新当前考试记录
                this.teacherQuizDao.update(teacherQuiz);
                // 插入新的待考记录
                if(quizScore < RestfulConfig.Quiz.QUIZ_PASS_SCORE){
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
     * 修改密码 
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @param password
     * @return    
     * boolean
     * @date 2016年8月27日
     */
    public boolean updatePassword(long teacherId,String password){
        logger.info("强制更新密码");
        User user = this.userDao.findById(teacherId);
        if (user == null){
            logger.warn("用户为Null,userId:{}",teacherId);
            return false;
        }
        String strPwd = new String(Base64.getDecoder().decode(password));
        if (StringUtils.isBlank(strPwd)){
            logger.warn("teacherId:{},密码为空{}",teacherId,strPwd);
            return false;
        }
        SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
        user.setPassword(encoder.encode(strPwd));
        if (StringUtils.isBlank(user.getToken())) {
            user.setToken(UUID.randomUUID().toString());
        }
        // 更新手机端appToken
        Map<String, Object> tokenMap = this.appRestfulDao.findAppTokenByTeacherId(teacherId);
        if (tokenMap != null && !tokenMap.isEmpty()) {
            logger.warn("更新token,teacherId:{},手机端需要重新登陆",teacherId);
            this.appRestfulDao.updateTeacherToken(Long.valueOf(tokenMap.get("id") + ""), user.getToken());
        }
        
        int i = this.userDao.update(user);
        if (i > 0) {
            this.updateRecruitmentId(teacherId);
        }
        return true;
    }
    
    /**
     * 单独更新 Teacher 的recruitmentId
     * 
     * @Author:ALong (ZengWeiLong) void
     * @date 2016年3月2日
     */
    public String updateRecruitmentId(long teacherId) {
        Teacher teacher = this.teacherDao.findById(teacherId);
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
        uuid = encoder.encode(teacher.getSerialNumber() + uuid + teacher.getEmail());
        uuid = System.currentTimeMillis() + "-" + uuid;
        teacher.setRecruitmentId(uuid);
        this.teacherDao.update(teacher);
        return uuid;
    }
    
    /**
     * 保存考试详细 
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @param teacherQuiz
     * @param grade
     * @return    
     * int
     * @date 2016年8月23日
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
            details.setCorrectAnswer(RestfulConfig.Quiz.CORRECTANSWERMAP.get(details.getSn()));
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
     * 待考记录更新 开始考试 
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @return    
     * boolean
     * @date 2016年8月29日
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
}
