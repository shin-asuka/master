package com.vipkid.enums;

/**
 * Created by LP-813 on 2017/4/25.
 */
public class TeacherGloryEnum {

    public enum Status {

        UNFINISH("0"), // 未达成
        FINISH("1"), // 已达成,待展示
        SHOWN("2"),//已展示
        EXPIRED("3"); // 已达成，已过期，未展示

        private String value;

        private Status(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }

        public String value(){
            return value;
        }
    }
}
