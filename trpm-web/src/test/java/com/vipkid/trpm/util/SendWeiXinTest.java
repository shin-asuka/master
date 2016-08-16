package com.vipkid.trpm.util;

import org.junit.Test;

import com.vipkid.trpm.entity.Student;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.weixin.MessageTools;

public class SendWeiXinTest {

    public static void main(String[] args) throws InterruptedException { 
        MessageTools ms = new MessageTools();
        //ms.sendFeedbackAsync("oaArPs5QdxpfYSfUrUKZgWe3R6J8,oaArPs7qxQTK6pQ5-a2WPyEucjo8");
        Student student = new Student();
        student.setId(123008);
        student.setEnglishName("Eatly");
        User teacher = new User();
        teacher.setId(880001);
        teacher.setName("LongTest");        
        ms.sendFeedbackSync("oaArPs5QdxpfYSfUrUKZgWe3R6J8,oaArPs7qxQTK6pQ5-a2WPyEucjo8",student,teacher,"新生体验课",1234567890);
        //System.out.println(TimeZone.getTimeZone("America/Mexico_City"));
    }
    
    
    @Test
    public void serialNumberTest(){
        System.out.println(LessonSerialNumber.serialNumber("A2-U1-LC1-L1"));
        System.out.println(LessonSerialNumber.serialNumber("C1-L4-U9-LC2-11"));
        System.out.println(LessonSerialNumber.serialNumber("IT1-U1-LC1-L1"));
        System.out.println(LessonSerialNumber.serialNumber("OPEN1-U7-LC1-L3"));
        System.out.println(LessonSerialNumber.serialNumber("P1-U1-LC1-L1"));
        System.out.println(LessonSerialNumber.serialNumber("R1-U1-LC1-L1"));
        System.out.println(LessonSerialNumber.serialNumber("SM-U1-LC1-L1"));
        System.out.println(LessonSerialNumber.serialNumber("T1-U1-LC1-L1"));
    }
}
