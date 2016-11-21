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
}
