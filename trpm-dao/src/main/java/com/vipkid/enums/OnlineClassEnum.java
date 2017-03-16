package com.vipkid.enums;

public class OnlineClassEnum {
    
    public enum ClassStatus{
        AVAILABLE, // 可排课
        OPEN, // 一对多课程，可接受预约
        BOOKED, // 已预约
        FINISHED, // 已结束
        CANCELED, // 已取消
        EXPIRED, // 已过期
        REMOVED, // 已删除， 用于统计
        INVALID; // 换老师操作后，原课程变为INVALID
        
        public static boolean isAvailable(String status) {
            return AVAILABLE.toString().equals(status);
        }
        public static boolean isOpen(String status) {
            return OPEN.toString().equals(status);
        }
        public static boolean isBooked(String status) {
            return BOOKED.toString().equals(status);
        }
        public static boolean isFinished(String status) {
            return FINISHED.toString().equals(status);
        }
        public static boolean isCanceled(String status) {
            return CANCELED.toString().equals(status);
        }
        public static boolean isExpired(String status) {
            return EXPIRED.toString().equals(status);
        }
        public static boolean isRemoved(String status) {
            return REMOVED.toString().equals(status);
        }
        public static boolean isInvalid(String status) {
            return INVALID.toString().equals(status);
        }
    }
    /* OnlineClass类型定义 */
    public enum ClassType {

        MAJOR(0),
        
        PRACTICUM (1),
        
        INTERVIEW (2);
        
        private Integer val;   
        
        private ClassType(Integer val) {
            this.val = val;
        }        
        public Integer val() {
            return val;
        }

    }
    
    public enum CourseName{
    	
    	DEMOREPORT("DemoReport","a"),
    	
    	OPEN("Open","open"),
    	
    	TRIAL("Trial","t"),
    	
    	PRACTICUM1("Practicum1","l1"),
    	
    	PRACTICUM2("Practicum2","l2"),
    	
    	PRACTICUM("Practicum","p"),
    	
    	RECRUITMENT("Recruitment","r"),
    	
    	MAJOR2016("Major2016","mc"),
    	
    	MAJOR("Major","c"),
    	
    	IT_TEST("IT_Test","it"),
    	
    	UNKNOWN("Unknown","unknown");
    	
        private String val;
        
        private String show;
        
        private CourseName(String show,String val) {
            this.show = show;
            this.val = val;
        }        
        public String val() {
            return val;
        }
        public String show() {
            return show;
        }
        
        public static String obtainCourseName(String lessonSn){
            lessonSn = lessonSn.toLowerCase();
            for (CourseName name:CourseName.values()) {
	        	if(lessonSn.startsWith(CourseName.PRACTICUM.val())){
	        		 if(lessonSn.endsWith(CourseName.PRACTICUM1.val())){
	                 	return CourseName.PRACTICUM1.show();
	                 }else if(lessonSn.endsWith(CourseName.PRACTICUM2.val())){
	                 	return CourseName.PRACTICUM2.show();
	                 }
	                 return CourseName.PRACTICUM.show();
	        	}else if(lessonSn.startsWith(name.val())){
					return name.show();
				}
            }
            return CourseName.UNKNOWN.show();
		}
    }
    

    /* 课程类型定义 */
    public enum CourseType {

        PRACTICUM,
        MAJOR;

        public static boolean isPracticum(String courseType) {
            return PRACTICUM.toString().equals(courseType);
        }

        public static boolean isMajor(String courseType) {
            return MAJOR.toString().equals(courseType);
        }

    }
}
