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
}
