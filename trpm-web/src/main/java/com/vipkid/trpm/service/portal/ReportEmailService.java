package com.vipkid.trpm.service.portal;

import com.google.common.collect.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.templete.TempleteUtils;
import com.vipkid.payroll.service.StudentService;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.*;
import com.vipkid.trpm.util.LessonSerialNumber;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportEmailService {

    private static final Logger logger = LoggerFactory.getLogger(ReportEmailService.class);

    @Autowired
    private TeacherCommentDao teacherCommentDao;
    @Autowired
    private StudentService studentService;

    public void sendEmail4PerformanceAdjust2CLT(long studentId, String serialNumber, String scheduledDateTime){
        if (studentId == 0 || StringUtils.isEmpty(serialNumber) || StringUtils.isEmpty(scheduledDateTime)){
            logger.info("sendEmail4PerformanceAdjust2CLT 参数不符 studentId = {}; serialNumber = {}; scheduledDateTime = {} ", studentId, serialNumber, scheduledDateTime);
            return;
        }
        sendEmail2CLT(getStudentName(studentId), getReason(1), getTableDetail(serialNumber,scheduledDateTime));
    }

    public void sendEmail4Performance2CLT(long studentId, String serialNumber){
        if (studentId == 0 || StringUtils.isEmpty(serialNumber) ||
                !((serialNumber.startsWith("C1") || serialNumber.startsWith("MC")) && serialNumber.contains("-U1-"))){
            logger.info("sendEmail4Performance2CLT 参数不符 studentId = {}; serialNumber = {} ", studentId, serialNumber);
            return;
        }
        //一个学生在第一个unit被标记为very difficult 或者 very easy的
        String lessonSnPrefix = serialNumber.substring(0, serialNumber.indexOf("-U1-") + ("-U1-").length()) + "%";
        List<Map<String, Object>> lessonSnList = teacherCommentDao.findLessonSn4PerformanceByStudentAndUnit(studentId, lessonSnPrefix);
        logger.info("sendEmail4Performance2CLT findLessonSn4PerformanceByStudentAndUnit lessonSnList = {} ", lessonSnList);

        if (CollectionUtils.isNotEmpty(lessonSnList) && lessonSnList.size() >= 3){

            List<Integer> lessonNoList = new ArrayList<>();
            StringBuffer tableDetails = new StringBuffer();

            for (Map<String, Object> lessonSn: lessonSnList){
                lessonNoList.add(LessonSerialNumber.getLessonNoFromSn(lessonSn.get("serial_number").toString()));
                tableDetails.append(getTableDetail(lessonSn.get("serial_number").toString(), lessonSn.get("scheduled_date_time").toString()));
            }

            List<Integer>  sortedLessonNoList = lessonNoList.stream().parallel().sorted().collect(Collectors.toList());
            int size = sortedLessonNoList.size();
            int[][] rules = {{3,3},{3,6},{6,12}};
            //rules[0]: 前3节课，有3节课被标记
            //rules[1]: 前6节课，有3节课被标记
            //rules[2]: 前12节课，有6节课被标记
            for (int[] rule : rules){
                if (size >= rule[0] && sortedLessonNoList.get(rule[0]-1) <= rule[1]){
                    sendEmail2CLT(getStudentName(studentId), getReason(rule[1]), tableDetails.toString());
                    break;
                }
            }
        }
    }

    private void sendEmail2CLT(String studentName, String reason, String tableDetails) {
        Map<String, String> paramsMap = Maps.newHashMap();
        paramsMap.put("studentName", studentName);
        paramsMap.put("reason", reason);
        paramsMap.put("tableDetails", tableDetails);

        Map<String, String> emailMap = new TempleteUtils().readTemplete("FeedbackAdjustRemindCLT.html", paramsMap, "FeedbackAdjustRemindCLT-Title.html");
        // TODO: 2016/9/14 replace the email to CTL's
        new EmailEngine().addMailPool("moyonglin@vipkid.com.cn", emailMap, EmailConfig.EmailFormEnum.EDUCATION);
    }

    private String getStudentName(long studentId){
        Student student = studentService.getById(studentId);
        String studentName = "";
        if (student != null){
            studentName = student.getName() + " - " + student.getEnglishName();
        }
        return studentName;
    }

    private String getReason(int reasonNo){
        String reason;
        switch(reasonNo) {
            case 3 : reason = "3 times in first 3 lessons"; break;
            case 6 : reason = "3 times in first 6 lessons"; break;
            case 12 : reason = "6 times in first 12 lessons"; break;
            default : reason = "老师建议";
        }
        return reason;
    }

    private String getTableDetail(String serialNumber, String scheduledDateTime){
        StringBuffer tableDetail = new StringBuffer("<tr><td>");
        tableDetail.append(serialNumber).append("</td><td>")
                .append(scheduledDateTime.split("\\.")[0]).append("</td></tr>");
        return tableDetail.toString();
    }

    // for testing
    public static void main (String [] args){
        String serialNumber = "C1-L1-U1-LC11-2";
        System.out.println(LessonSerialNumber.getLessonNoFromSn(serialNumber));
        String scheduledDateTime = "2016-05-13 12:00:00.0";
        System.out.println(scheduledDateTime.split("\\.")[0]);
        System.out.println(serialNumber.substring(0, serialNumber.indexOf("-U1-") + ("-U1-").length()) + "%");
        List<String> lessonSnList =  Arrays.asList(
            "C1-L1-U1-LC1-2",
            "C1-L1-U1-LC1-10",
            "C1-L1-U1-LC1-3",
            "C1-L1-U1-LC2-7",
            "C1-L1-U1-LC2-11",
            "C1-L1-U1-LC2-12");
        if (CollectionUtils.isNotEmpty(lessonSnList) && lessonSnList.size() >= 3) {
            List<Integer> lessonNoList = new ArrayList<>();
            lessonSnList.forEach(x -> lessonNoList.add(LessonSerialNumber.getLessonNoFromSn(x)));
            //List<Integer> lessonNoList = Arrays.asList(1,1,3);
            List<Integer> sortedLessonNoList = lessonNoList.stream().parallel().sorted().collect(Collectors.toList());
            System.out.println(sortedLessonNoList.toString());
            if (sortedLessonNoList.size() >= 3 && sortedLessonNoList.get(2) == 3) {
                System.out.println("3-----------");
            } else if (sortedLessonNoList.size() >= 3 && sortedLessonNoList.get(2) <= 6) {
                System.out.println("6-----------");
                System.out.println(sortedLessonNoList.get(2));
            } else if (sortedLessonNoList.size() >= 6 && sortedLessonNoList.get(5) <= 12) {
                System.out.println("12-----------");
                System.out.println(sortedLessonNoList.get(5));
            }
        }
    }
}
