package com.vipkid.email.handle;

public class EmailEntity {
    
    private String host; 			// 邮件服务器设置  
    
    private String toMail;			// 邮件接收者的地址 
    
    private String fromMail;		// 邮件发送者的地址
    
    private String mailSubject;	    // 邮件主题
    
    private String mailBody;		// 邮件内容设置
    
    private String personalName;	// 发送人昵称

    public String getHost() {
        return host;
    }

    public EmailEntity setHost(String host) {
        this.host = host;
        return this;
    }

    public String getToMail() {
        return toMail;
    }

    public EmailEntity setToMail(String toMail) {
        this.toMail = toMail;
        return this;
    }

    public String getFromMail() {
        return fromMail;
    }

    public EmailEntity setFromMail(String fromMail) {
        this.fromMail = fromMail;
        return this;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public EmailEntity setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
        return this;
    }

    public String getMailBody() {
        return mailBody;
    }

    public EmailEntity setMailBody(String mailBody) {
        this.mailBody = mailBody;
        return this;
    }

    public String getPersonalName() {
        return personalName;
    }

    public EmailEntity setPersonalName(String personalName) {
        this.personalName = personalName;
        return this;
    }
}
