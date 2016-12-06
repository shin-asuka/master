package com.vipkid.trpm.service.passport;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
import com.vipkid.email.templete.TemplateUtils;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.Lesson;
import com.vipkid.trpm.entity.Student;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;

/**
 * 供：定时器调用 
 * @author Along(ZengWeiLong)
 * @ClassName: NoticeService 
 * @date 2016年4月27日 上午11:38:11 
 *
 */
@Service
public class NoticeService {
    
    private Logger log = LoggerFactory.getLogger(NoticeService.class);

    @Autowired
    public StudentDao studentDao;

    @Autowired
    public TeacherDao teacherDao;

    @Autowired
    public LessonDao lessonDao;
    
    @Autowired
    public OnlineClassDao onlineClassDao;

    @Autowired
    public UserDao userDao;

    /**
     * @Author:ALong (ZengWeiLong)
     * @param teacherId / username
     * @param list
     * @return boolean
     * @date 2016年4月22日
     */
    public boolean emailHandle(String teacherId, List<Map<String,Object>> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        Teacher teacher = teacherDao.findById(Long.valueOf(teacherId));
        if (teacher == null) {
            User user = userDao.findByUsername(teacherId);
            teacher = teacherDao.findById(user.getId());
        }
        if (teacher == null) {
            return true;
        }
        return this.emailHandle(teacher, list);
    }

    public boolean emailHandle(Teacher teacher, List<Map<String,Object>> list) {
        try{
            if (list == null || list.isEmpty()) {
                return true;
            }
            SimpleDateFormat yymmdd = new SimpleDateFormat("yyyy MMMM dd, HH:mm", Locale.US);
            SimpleDateFormat mmdd = new SimpleDateFormat("MMMM d", Locale.US);
            StringBuilder sb = new StringBuilder("");
            Map<String, String> map = Maps.newHashMap();
            for (int i = 0; i < list.size(); i++) {
                Map<String,Object> onlineClassMap = list.get(i);
                
                String row = TemplateUtils.NOTE_TEMPLATE.replace("{{time}}", yymmdd.format(onlineClassMap.get("scheduledDateTime")));
                Student stu = studentDao.findById(Long.valueOf(onlineClassMap.get("studentId")+""));
                if(stu != null){
                    row = row.replace("{{ename}}", stu.getEnglishName());
                }else{
                    Teacher stuTeacher = teacherDao.findById(Long.valueOf(onlineClassMap.get("teacherId")+""));
                    if(stuTeacher!= null){
                        row = row.replace("{{ename}}", stuTeacher.getRealName());
                    }else{
                        row = row.replace("{{ename}}", "");
                    }
                }
                Lesson lesson = lessonDao.findById(Long.valueOf(onlineClassMap.get("lessonId")+""));
                row = row.replace("{{lessonNo}}", lesson.getSerialNumber());
                
                sb.append(row);
                map.put("mouth-day", mmdd.format(onlineClassMap.get("scheduledDateTime")));
            }
            map.put("datetime-name-course", sb.toString());
            map.put("count", String.valueOf(list.size()));
            map.put("teacherName", teacher.getRealName());
            TemplateUtils templete = new TemplateUtils();
            Map<String, String> tmpMap = templete.readTemplate("UpcomingClassesReminder.html", map, "UpcomingClassesReminder-Title.html");
            new EmailEngine().addMail(teacher.getEmail(), tmpMap,EmailFormEnum.TEACHVIP);
            return true;
        }catch(Exception e){
            log.info(e.getMessage(),e);
            return false;
        }
    }

    public List<String> findAllRegular() {
        return teacherDao.findAllRegular();
    }
    
    
    public Map<String,List<Map<String,Object>>> findBookedClass(String startTime,String endTime){
       List<Map<String, Object>> baseDate = onlineClassDao.findTomorrowAllBook(startTime, endTime);
       return findDbData(baseDate);
    }
    
    public Map<String,List<Map<String,Object>>> findBookedClass(String startTime,String endTime,long teacherId){
        List<Map<String, Object>> baseDate = onlineClassDao.findTomorrowAllBook(startTime, endTime,teacherId);
        return findDbData(baseDate);
    }
    
    private Map<String,List<Map<String,Object>>> findDbData(List<Map<String, Object>> baseDate){
        Map<String, List<Map<String,Object>>> onlineClassMap = Maps.newHashMap();
        if (baseDate != null && baseDate.size() > 0) {
            for (Map<String,Object> onlineEntry : baseDate) {
                List<Map<String,Object>> list = onlineClassMap.get(onlineEntry.get("teacherId"));
                if (list == null) {
                    list = Lists.newArrayList();
                }
                list.add(onlineEntry);
                onlineClassMap.put(String.valueOf(onlineEntry.get("teacherId")), list);
            }
        }
        return onlineClassMap;
    }
    
}
