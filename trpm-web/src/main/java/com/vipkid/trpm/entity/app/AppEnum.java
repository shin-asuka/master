package com.vipkid.trpm.entity.app;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

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
    
    /**学历*/
    public enum DegreeType{
        HIGH_SCHOOL,
        ASSOCIATES,
        BACHELORS,
        MASTERS,
        PHD,
        OTHER
    }
    
    /**招募端新增3种渠道分类*/
    public enum RecruitmentChannel {
        //2016-11 新增渠道分类
        TEACHER,
        PARENT,
        OTHER // 其他
    }
    
    /**
     * 根据index获取枚举
     * @Author:ALong (ZengWeiLong)
     * @param clazz
     * @param index
     * @return    
     * E
     * @date 2016年10月24日
     */
    public static <E extends Enum<E>> E getByIndex(final Class<E> enumClass, int index) {
        return (E)enumClass.getEnumConstants()[index];  
    } 
    
    /**
     * 根据名称获取枚举 
     * @Author:ALong (ZengWeiLong)
     * @param enumClass
     * @param name
     * @return    
     * E
     * @date 2016年10月24日
     */
    public static <E extends Enum<E>> E getByName(final Class<E> enumClass, String name) {  
        return EnumUtils.getEnum(enumClass, name);
    } 
    
    /**
     * 判断枚举类是否包含某个枚举
     * @Author:ALong (ZengWeiLong)
     * @param enumClass
     * @param name
     * @return    
     * boolean
     * @date 2016年10月24日
     */
    public static <E extends Enum<E>> boolean containsName(final Class<E> enumClass, String name) {
        return EnumUtils.isValidEnum(enumClass, name);
    } 

    @Deprecated
    public static <T> boolean containsNameold(final Class<T> enumClass, String name) {
        T[] enums = enumClass.getEnumConstants();
        for(T obj:enums){
            if(StringUtils.equals(obj.toString(), name)){
                return true;
            }
        }
        return false;
    }
}
