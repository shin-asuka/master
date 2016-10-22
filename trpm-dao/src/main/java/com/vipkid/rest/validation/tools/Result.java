package com.vipkid.rest.validation.tools;

import com.vipkid.rest.validation.tools.ValidationEnum.Message;

public class Result {

    public String name;
    
    public Message messageType;
    
    public String messages;
    
    public boolean result;
    
    private Result(String name,Message messageType,boolean result){
        this.name = name;
        this.messageType = messageType;
        this.messages = messageType.val();
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Message getMessageType() {
        return messageType;
    }

    public void setMessageType(Message messageType) {
        this.messageType = messageType;
        this.messages = messageType.val();
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

    public static Result bulider(String name,Message message,boolean result){
        return new Result(name,message,result);
    }
    
    public static Result bulider(){
        return bulider("", Message.SUCCESS, false);
    }
}
