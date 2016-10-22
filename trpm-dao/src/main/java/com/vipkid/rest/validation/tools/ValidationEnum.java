package com.vipkid.rest.validation.tools;

public class ValidationEnum {

    public enum Type {

        NOT_NULL,
      
    }
    
    public enum Message{
        
        SUCCESS("Ok!"),
        
        ERROR("The field is required !");
        
        private String val;
        
        public String val(){
            return val;
        }
        
        private Message(String val) {
            this.val = val;
        }
    }

}
