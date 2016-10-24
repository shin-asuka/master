package com.vipkid.rest.validation.tools;

public class ValidateEnum {

    public enum Type {

        SUCCESS("OK"),
        
        NOT_NULL("The field is required !"),
        
        MAX_LENGTH("The field length is too long !"),
        
        MIN_LENGTH("The field length is too short !");
      
        private String message;
        
        public String message(){
            return message;
        }
        
        private Type(String message) {
            this.message = message;
        }
    }

}
