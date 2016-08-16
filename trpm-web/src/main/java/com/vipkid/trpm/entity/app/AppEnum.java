package com.vipkid.trpm.entity.app;

public class AppEnum {

    public enum CourseType {
        IT_TEST(0),
        GUIDE(1),
        NORMAL(2),
        MAJOR(3),
        TEST(4),
        TEACHER_RECRUITMENT(5),
        PRACTICUM(6),
        TRIAL(7),
        DEMO(8),
        ASSESSMENT2(9),
        ELECTIVE_LT(10),
        OPEN1(11),
        REVIEW(12),
        OPEN2(13),
        ELECTIVE_SM(14),
        ELECTIVE_WH(15),
        CHRISTMAS_COURSE(16),
        US(17); 
        
        private Integer val;        
        private CourseType(Integer val) {
            this.val = val;
        }        
        public Integer val() {
            return val;
        }
    }
        
    public enum ClassStatus{
        
        AVAILABLE(0),        
        BOOKED(1),        
        FINISHED(2);
        
        private Integer val;        
        private ClassStatus(Integer val) {
            this.val = val;
        }        
        public Integer val() {
            return val;
        }
    }
    
    public enum StatusInfo{        
        None(0),        
        AS_SCHEDULED(1),        
        STUDENT_IT_PROBLEM(2),
        STUDENT_NO_SHOW(3),
        STUDENT_NO_SHOW_24H(4),
        SYSTEM_PROBLEM(5),
        TEACHER_CANCELLATION(6),
        TEACHER_CANCELLATION_24H(7),
        TEACHER_IT_PROBLEM(8),
        TEACHER_NO_SHOW(9),
        TEACHER_NO_SHOW_2H(10);
        
        private Integer val;        
        private StatusInfo(Integer val) {
            this.val = val;
        }        
        public Integer val() {
            return val;
        }
    }
    
    public enum Gender{        
        MALE(0),        
        FEMALE(1);
        
        private Integer val;        
        private Gender(Integer val) {
            this.val = val;
        }        
        public Integer val() {
            return val;
        }
    }
    
    public enum LoginStatus{
        OK(1),
        NO_REGULAR(2),
        FAIL(3),
        QUIT(4),
        ACTIVITY(5),
        LOCKED(6);
        
        private Integer val;        
        private LoginStatus(Integer val) {
            this.val = val;
        }        
        public Integer val() {
            return val;
        } 
    }
    
    public enum LifeCycle{
        SIGNUP(0),
        BASIC_INFO(1),
        INTERVIEW(2),
        SIGN_CONTRACT(3),
        TRAINING(4),
        PRACTICUM(5),
        REGULAR(6),
        FAIL(7),
        QUIT(8);
        
        private Integer val;        
        private LifeCycle(Integer val) {
            this.val = val;
        }        
        public Integer val() {
            return val;
        } 
    }
    
    public static <T extends Enum<T>> T valueOf(Class<T> clazz, int ordinal) {  
        return (T)clazz.getEnumConstants()[ordinal];  
    } 
}
