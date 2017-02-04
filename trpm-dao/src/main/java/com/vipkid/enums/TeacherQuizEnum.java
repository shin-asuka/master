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
    
    public enum Version{

        ADMIN_QUIZ(1),
        
        TRAINING_QUIZ(2);
        
        private int version;
        
        private Version(int version){
            this.version = version;
        }
        
        public int val(){
            return version;
        }
    }
}
