package com.vipkid.rest.validation.tools;

import com.vipkid.rest.validation.annotation.EnumList.Type;


public class Result {
    
    /**字段名称*/
    private String name;
    
    /**检查类型*/
    private Type type;
    
    /**提示语句*/
    private String messages;
    
    /**检查结果 true:有,false:无*/
    private boolean result;
    
    private Result(String name,Type type,String message,boolean result){
        this.name = name;
        this.type = type;
        this.messages = message;
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static Result bulider(String name,Type type,String message,boolean result){
        return new Result(name,type,message,result);
    }
    
    public static Result bulider(){
        return bulider("",Type.OK,"", false);
    }
}
