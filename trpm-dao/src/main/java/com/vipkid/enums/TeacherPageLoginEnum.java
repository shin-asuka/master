package com.vipkid.enums;

public class TeacherPageLoginEnum {

    public enum LoginType {

        CLASSROOMS(0),

        PRACTICUM(1),
        
        ADMINQUIZ(2),
        
        EVALUATION(3),
        
        EVALUATION_CLICK(4);

        private Integer val;   
        
        private LoginType(Integer val) {
            this.val = val;
        }        
        public Integer val() {
            return val;
        }
    }
}
