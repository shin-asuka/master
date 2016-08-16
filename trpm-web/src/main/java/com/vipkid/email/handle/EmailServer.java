package com.vipkid.email.handle;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailServer {
 private static Logger logger = LoggerFactory.getLogger(EmailServer.class);
    
    private Properties props = System.getProperties();        //获取系统环境
    
    private String port = "587";

    public EmailServer(){
        this.start(port);
    }
    
    public EmailServer(String port){
        this.port = port;
        this.start(port);
    }
    
    private void start(String port){
        props.setProperty("mail.smtp.port", port);
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", port);
        props.setProperty("mail.store.protocol", "pop3");
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.debug", "false");
    }
    
    /**
     * 邮件发送
     * @param reviceEntity 发送信息对象
     * @param authenticator 登录信息对象
     * @return
     * @throws javax.mail.SendFailedException
     */
    public int sendMail(EmailEntity reviceEntity,EmailAuthenticator authenticator){
      try {
           String toMail = reviceEntity.getToMail();                       // 邮件接收者的地址 
           String fromMail = reviceEntity.getFromMail();                   // 邮件发送者的地址
           String mailSubject = reviceEntity.getMailSubject();             // 邮件主题
           String mailBody = reviceEntity.getMailBody();                   // 邮件内容设置
           String personalName = reviceEntity.getPersonalName();           // 我的称乎 
           props.setProperty("mail.smtp.host", reviceEntity.getHost());
           logger.info("Email send start ====>：");
           Session session = Session.getDefaultInstance(props,authenticator); //设置session,和邮件服务器进行通讯  * 进行邮件服务用户认证
           MimeMessage message = new MimeMessage(session); 
           message.setContent(mailBody,"text/html; charset=utf-8");     //设置邮件格式 
           message.setSubject(mailSubject);                             //设置邮件主题 
           message.setHeader("Content-Transfer-Encoding", "base64");    //设置邮件标题 
           message.setSentDate(new Date());                             //设置邮件发送时期 
           message.setFrom(new InternetAddress(fromMail,personalName));                        //设置邮件发送者的地址 
           message.addRecipient(Message.RecipientType.TO,new InternetAddress(toMail));         //设置邮件接收者的地址
           logger.info("Email send ing ====>：");
           Transport.send(message); 
       } catch (Exception e) { 
           logger.error("Emial send Exceprion:",e);
           return 0;
       }  
       logger.info("Email send complete ====>：");
       return 1;
     } 
}
