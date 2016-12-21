package com.vipkid.enums;

public class UserEnum {

    public static final String DEFAULT_TEACHER_PASSWORD = "vipkid";

    public enum Status {
        NORMAL("NORMAL"), // 正常
        LOCKED("LOCKED"), // 冻结
        TEST("TEST"); // 测试账号，用于教师招聘约课程
        private String value;

        private Status(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }

        public static boolean isLocked(String status) {
            if (null == status) {
                return false;
            }
            if (Status.LOCKED.toString().equals(status)) {
                return true;
            }
            return false;
        }
    }

    public enum AccountType {
        NORMAL("NORMAL"), // 正式账号
        TEST("TEST"); // 员工创建的测试账号，用于测试流程

        private String value;

        private AccountType(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }

    public enum Gender {
        MALE("MALE"), // 男
        FEMALE("FEMALE"); // 女

        private String value;

        private Gender(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }

    public enum Dtype {

        TEACHER("Teacher"), STAFF("Staff"),PARTNER("Partner");

        private String value;

        private Dtype(String value) {
            this.value = value;
        }

        public String val() {
            return this.value;
        }
    }
    
    /**
     * 角色
     */
    public enum Role {
        PARENT("PARENT"), // 家长
        STUDENT("STUDENT"), // 学生
        TEACHER("TEACHER"), // 老师
        PARTNER("PARTNER"), //合伙人
        STAFF_CXO("STAFF_CXO"), // 员工 - 管理层
        STAFF_ADMIN("STAFF_ADMIN"), // 员工 - 管理员
        STAFF_MARKETING("STAFF_MARKETING"), // 员工 - 市场
        STAFF_MARKETING_MANAGER("STAFF_MARKETING_MANAGER"), // 员工 - 市场管理
        STAFF_OPERATION("STAFF_OPERATION"), // 员工 - 教务（fireman）
        STAFF_OPERATION_MANAGER("STAFF_OPERATION_MANAGER"), // 员工 - 教务管理
        STAFF_FINANCE("STAFF_FINANCE"),  // 员工 - 财务
        STAFF_SALES("STAFF_SALES"), // 员工 - 销售
        STAFF_SALES_MANAGER("STAFF_SALES_MANAGER"), // 员工 - 销售管理
        STAFF_SALES_DIRECTOR("STAFF_SALES_DIRECTOR"), // 员工 - 销售经理
        STAFF_CHINESE_LEAD_TEACHER("STAFF_CHINESE_LEAD_TEACHER"), // 员工 - 中教班主任
        STAFF_CHINESE_LEAD_TEACHER_MANAGER("STAFF_CHINESE_LEAD_TEACHER_MANAGER"), // 员工 - 中教班主任管理
        STAFF_FOREIGN_LEAD_TEACHER("STAFF_FOREIGN_LEAD_TEACHER"), // 员工 - 外教班主任
        STAFF_TMK("STAFF_TMK"), // 员工 - TMK
        STAFF_TMK_MANAGER("STAFF_TMK_MANAGER"), // 员工 - TMK管理
        STAFF_TMK_DIRECTOR("STAFF_TMK_DIRECTOR"), // 员工 - TMK经理
        STAFF_EDUCATION("STAFF_EDUCATION"), // 员工 - 教研
        STAFF_EDUCATION_MANAGER("STAFF_EDUCATION_MANAGER"), // 员工 - 教研管理
        STAFF_IT_SUPPORT("STAFF_IT_SUPPORT"), // 员工 - TMK
        STAFF_IT_SUPPORT_MANAGER("STAFF_IT_SUPPORT_MANAGER"), // 员工 - TMK管理
        STAFF_CURRICULUM_DEVELOPER("STAFF_CURRICULUM_DEVELOPER"), // 员工 - 课程开发人员
        STAFF_CURRICULUM_MANAGER("STAFF_CURRICULUM_MANAGER"), // 员工 - 课程管理人员
        STAFF_DATA_EXPERT("STAFF_DATA_EXPERT"), // 员工 - 数据
        STAFF_DEVELOPER("STAFF_DEVELOPER"), // 员工 - 开发
        STAFF_TESTER("STAFF_TESTER"), // 员工 - 测试
        STAFF_PRODUCT("STAFF_PRODUCT"), // 员工 - 产品
        STAFF_DESIGN_ART("STAFF_DESIGN_ART"), // 员工 - 设计
        
        INVESTOR("INVESTOR"), // 投资人
        
        TI_PARTNER("TI_PARTNER"), //teacher interview
        TP_PARTNER("TP_PARTNER"), // teacher practicum
        TE_PARTNER("TE_PARTNER"); // teacher evaluation
        
        private String value;
    
        private Role(String value) {
            this.value = value;
        }
    
        public String toString() {
            return this.value;
        }
    }
}
