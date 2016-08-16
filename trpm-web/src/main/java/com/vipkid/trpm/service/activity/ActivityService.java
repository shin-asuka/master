package com.vipkid.trpm.service.activity;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.mchange.v2.ser.SerializableUtils;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.dao.StudentDao;
import com.vipkid.trpm.dao.TeacherActivityDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Student;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.proxy.redis.RedisClient;

@Service
public class ActivityService {
    
    private static Logger logger = LoggerFactory.getLogger(ActivityService.class);

    @Autowired
    private TeacherActivityDao teacherActivityDao;
    
    @Autowired
    private TeacherDao teacherDao;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private StudentDao studentDao;
    
    /**
     * 查询老师在一年内上了多少节课
     * @Author:ALong (ZengWeiLong)
     * @param id
     * @param yearmd
     * @return int
     * @date 2016年3月18日
     */
    private int countClassByTeacher(long id,String yearmd){
        return this.teacherActivityDao.countClassByTeacherId(id, yearmd);
    }
    
    /**
     * 查询老师在一年内教了多少学生
     * @Author:ALong (ZengWeiLong)
     * @param id
     * @param yearmd
     * @return    
     * int
     * @date 2016年3月18日
     */
    private int countStudentByTeacherId(long id,String yearmd){
        List<Map<String, Object>> list = this.teacherActivityDao.countStudentByTeacherId(id, yearmd);
        if(list != null){
            return list.size();
        }
        return 0;
    }
    
    private Map<String,String> findMoreClassStudent(long id,String yearmd){
        Map<String,String> resultMap = Maps.newHashMap();
        List<Map<String, Object>> list = this.teacherActivityDao.countStudentByMax(id,yearmd);
        if(list != null && list.size() > 0){
            Map<String, Object> map = list.get(0);
            long studentId = (Long)map.get("student_id");
            Student stud = this.studentDao.findById(studentId);
            if(stud != null){
                resultMap.put("studentName",stud.getEnglishName());
                if("1".equals(String.valueOf(map.get("counts")))){
                    resultMap.put("moreclass","1 class");
                }else{
                    resultMap.put("moreclass",map.get("counts")+" classes");
                }
                
                resultMap.put("avatar","He");
                if(!StringUtils.isEmpty(stud.getAvatar())){
                    if(stud.getAvatar().startsWith("girl")){
                        resultMap.put("avatar","She");
                    }
                }
            }
        }
        return resultMap;
    }
            
    public Teacher findTeacherById(long id){
        if( id != 0){
            return this.teacherDao.findById(id);
        }
        return null;
    }
    
    public User findUserById(long id){
        if( id != 0){
            return this.userDao.findById(id);
        }
        return null;
    }
    /**
     * ch 
     * @Author:ALong (ZengWeiLong)
     * @param model
     * @param teacher
     * @return    
     * Model
     * @throws java.io.IOException
     * @throws ClassNotFoundException 
     * @date 2016年3月18日
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> readInfo(Teacher teacher,String yearmd) throws ClassNotFoundException, IOException{
        Map<String,Object> resultMap = Maps.newHashMap();
        resultMap.put("info","0");//默认读取失败
        try{
            logger.info("从Redis读取老师信息 -- start teacherId:" + teacher.getId());
            byte[] bytes = RedisClient.me().get((ApplicationConstant.REDIS_ACTIVITY_KEY+teacher.getId()).getBytes());
            if(bytes != null){
                resultMap = (Map<String, Object>) SerializableUtils.fromByteArray(bytes);
                logger.info("从Redis读取老师信息 -- end -- "+teacher.getId());
                //读取成功
                resultMap.put("info","1");
                return resultMap;
            }
        }catch(Exception e){
            //读取异常
            logger.error("从Redis读取老师信息异常："+e.getMessage(),e);
        }
        //继续查询数据库
        User user = this.findUserById(teacher.getId());
        Teacher teacherModul = new Teacher();
        teacherModul.setRealName(user.getName());
        teacherModul.setAvatar(teacher.getAvatar());
        resultMap.put("teacher", teacherModul);
        
        //如果entryDate 为 null 显示error
        if(teacher.getEntryDate() == null){
            logger.error("Teacher id = " + teacher.getId() + ",entryDate is null");
            return resultMap;
        }
        long hour = this.countHour(teacher);
        if(hour <= 1){
            resultMap.put("message","1 hour");
        }else if(hour <= 24){
            resultMap.put("message",hour + " hours");
        }else if(hour <= 48){
            resultMap.put("message","2 days");
        }else{
            hour = hour/(30*24);
            if(hour <= 1){
                resultMap.put("message","1 month"); 
            }else{
                resultMap.put("message", (hour+1) + " months");
            }
        }
        
        int countStudent = this.countStudentByTeacherId(teacher.getId(), yearmd);
        //如果countStudent 为 0 显示error
        if(countStudent == 0){
            return resultMap;
        }
        if(countStudent == 1){
            resultMap.put("student",countStudent+" Chinese student");
        }else{
            resultMap.put("student",countStudent+" Chinese students");
        }
        
        int countClass = this.countClassByTeacher(teacher.getId(),yearmd);
        //如果countClass 为 0 显示error
        if(countClass == 0){
            return resultMap;
        }
        if(countClass == 1){
            resultMap.put("class", countClass + " class");
        }else{
            resultMap.put("class", countClass+ " classes");
        }
        
        resultMap.put("minutes",(countClass * 30) + " minutes");
        resultMap.putAll(this.findMoreClassStudent(teacher.getId(), yearmd));
        
        //查询成功 放入redis
        try{
            logger.info("Redis放入老师信息:teacherId:"+teacher.getId());
            RedisClient.me().set((ApplicationConstant.REDIS_ACTIVITY_KEY+teacher.getId()).getBytes(), SerializableUtils.toByteArray(resultMap));
        }catch(Exception e){
            logger.error("Redis放入老师信息异常："+e.getMessage(),e);
        }
        resultMap.put("info","1");
        return resultMap;
    } 
    
    /**
     * 计算入职时间 
     * @Author:ALong (ZengWeiLong)
     * @param user
     * @return    
     * long
     * @date 2016年4月6日
     */
    private long countHour(Teacher teacher){
        long entryDate = teacher.getEntryDate().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016,4,12,11,30,0);
        long count = (calendar.getTimeInMillis() - entryDate);
        count = count/(3600*1000);
        return count;
    }
    
}
