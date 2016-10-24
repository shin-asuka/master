package com.vipkid.rest.validation.tools;

public class ValidateEnum {

    public enum Type {

        NOT_NULL,
        
        MAX_LENGTH,
        
        MIN_LENGTH
      
    }
    
    public enum Message{
        
        SUCCESS("Ok!"),
        
        NOT_NULL("The field is required !"),
        
        MAX_LENGTH("The field length is too long !"),
        
        MIN_LENGTH("The field length is too short !");
        
        private String val;
        
        public String val(){
            return val;
        }
        
        private Message(String val) {
            this.val = val;
        }
    }

}
