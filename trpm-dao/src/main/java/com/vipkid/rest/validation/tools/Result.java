package com.vipkid.rest.validation.tools;

import com.vipkid.rest.validation.tools.ValidateEnum.Type;

public class Result {

    public String name;
    
    public Type messageType;
    
    public String messages;
    
    public boolean result;
    
    private Result(String name,Type messageType,boolean result){
        this.name = name;
        this.messageType = messageType;
        this.messages = messageType.message();
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getMessageType() {
        return messageType;
    }

    public void setMessageType(Type messageType) {
        this.messageType = messageType;
        this.messages = messageType.message();
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
    
    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public static Result bulider(String name,Type message,boolean result){
        return new Result(name,message,result);
    }
    
    public static Result bulider(){
        return bulider("", Type.SUCCESS, false);
    }
}
