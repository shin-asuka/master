package com.vipkid.enums;

public class ShareActivityExamEnum {

    public enum StatusEnum {

    	PENDING(0),

        COMPLETE(1);

        private int val;

        private StatusEnum(int val) {
            this.val = val;
        }

        public int val() {
            return this.val;
        }
    }
    
    public enum RequestTypeEnum {
    	
        PC(0),
        
        APP(1);

        private int val;

        private RequestTypeEnum(int val) {
            this.val = val;
        }

        public int val() {
            return this.val;
        }
    }
}
