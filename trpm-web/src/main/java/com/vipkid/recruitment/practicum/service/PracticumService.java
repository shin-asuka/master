package com.vipkid.recruitment.practicum.service;

import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherApplicationDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.TeacherApplication;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PracticumService {
    
    private static Logger logger = LoggerFactory.getLogger(PracticumService.class);

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private OnlineClassDao onlineClassDao;

    public TeacherApplication getPracticumTeacherApplication(long teacherId) {
        List<TeacherApplication> list = teacherApplicationDao.findApplictionForStatus(teacherId, TeacherApplicationEnum.Status.TRAINING.name());
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    /**
     * 查询  该教师 Current = 1 的步骤记录<br/>
     * @Author:ALong
     * @param teacherId
     * @return 2015年10月13日
     */
    public TeacherApplication findAppliction(long teacherId){
        List<TeacherApplication> list = teacherApplicationDao.findCurrentApplication(teacherId);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据result状态来判断practicum的状态
     * @Author:ALong
     * @return
     * TeacherApplication
     * @date 2015年12月28日
     */
    public TeacherApplication findAppByPracticum2(long teacherId){
        List<TeacherApplication> list = teacherApplicationDao.findApplictionForStatusResult(teacherId, null, TeacherApplicationEnum.Result.PRACTICUM2.name());
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    public Map<String, Map<String, Object>> getAvailableScheduled(Date fromTime, Date toTime,
                                                                  String timezone) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("fromTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fromTime));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(toTime);
        calendar.add(Calendar.DATE, 2);
        paramMap.put("toTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
        paramMap.put("toTZOffset", timezone);

        List<Map<String, Object>> availableScheduledList = null;//onlineClassDao.selectMaps(new OnlineClass(), "findPracticumRecruitment", paramMap);

        if(availableScheduledList != null){

            Collections.shuffle(availableScheduledList);


        }

        Map<String, Map<String, Object>> availableScheduled = new HashMap<String, Map<String, Object>>();

        DateTimeFormatter fromatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        for (Map<String, Object> map : availableScheduledList) {
            Date date = (Date) map.get("scheduled_date_time");
            availableScheduled.put(
                    fromatter.withZone(DateTimeZone.forID(timezone)).print(date.getTime()), map);
        }

        return availableScheduled;
    }
}
