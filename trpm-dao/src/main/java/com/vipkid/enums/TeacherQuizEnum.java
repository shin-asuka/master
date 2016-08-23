package com.vipkid.enums;

public class TeacherQuizEnum {

    public enum Status{
        
        NOQUIZ(0),
        
        FAIL(1),
        
        PASS(2);
        
        private int val;

        private Status(int val) {
            this.val = val;
        }
        public int val() {
            return this.val;
        }
    }
}
