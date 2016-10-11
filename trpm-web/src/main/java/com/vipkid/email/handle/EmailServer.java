package com.vipkid.email.handle;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailServer {
    
    private static Logger logger = LoggerFactory.getLogger(EmailServer.class);
    
    private static int sendMessage(String from,Session _session,EmailEntity emailEntity){
        try {
            String toMail = emailEntity.getToMail(); // 邮件接收者的地址
            String title = emailEntity.getMailSubject(); // 邮件主题
            final String content = emailEntity.getMailBody();//邮件内容
            String personalName = emailEntity.getPersonalName(); // 我的称乎
            if(StringUtils.isNotBlank(personalName)){
                personalName = from.split("@")[0];
            }
            logger.info("Email send start ====>：");
            MimeMessage message = new MimeMessage(_session);            
            MimeMultipart mimeMultipart = new MimeMultipart();
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(content, "text/html; charset=utf-8");// 设置邮件格式
            //bodyPart.addHeader("Content-Transfer-Encoding", "base64");// 设置邮件标题
            mimeMultipart.addBodyPart(bodyPart);
            message.setContent(mimeMultipart);
            message.setSubject(MimeUtility.encodeText(title, StandardCharsets.UTF_8.toString(), "B")); // 设置邮件主题
            message.setSentDate(new Date()); // 设置邮件发送时期
            message.setFrom(new InternetAddress(from, personalName)); // 设置邮件发送者的地址
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toMail)); // 设置邮件接收者的地址
            logger.info("Email send ing ====>："+toMail+"_"+title);
            Transport.send(message);
        } catch (Exception e) {
            logger.error("Emial send Exceprion:", e);
            return 0;
        }
        logger.info("Email send complete ====>：");
        return 1;
    }
    
    static class Teachvip{ 
    
        private static Properties _props = System.getProperties();        //获取系统环境
        
        private static Session _session = null;
    
        static{
            _props.setProperty("mail.smtp.port", "587");
            _props.setProperty("mail.smtp.auth", "true");
            _props.setProperty("mail.transport.protocol", "smtp");
            _props.setProperty("mail.smtp.host", EmailConfig.HOST);
            _props.setProperty("mail.debug", "false");
            _session = Session.getInstance(_props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EmailConfig.TC_USERNAME, EmailConfig.TC_PASSWORD);
                }
            });
        }
        
        public static int sendMail(EmailEntity emailEntity){
            return sendMessage(EmailConfig.TC_FROM, _session, emailEntity);
        } 
    }
    
    
    static class Education{ 
        
        private static Properties _props = System.getProperties();        //获取系统环境
        
        private static Session _session = null;
    
        static{
            _props.setProperty("mail.smtp.port", "587");
            _props.setProperty("mail.smtp.auth", "true");
            _props.setProperty("mail.transport.protocol", "smtp");
            _props.setProperty("mail.smtp.host", EmailConfig.HOST);
            _props.setProperty("mail.debug", "false");
            _session = Session.getInstance(_props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EmailConfig.ED_USERNAME, EmailConfig.ED_PASSWORD);
                }
            });
        }
        
        public static int sendMail(EmailEntity emailEntity){
            return sendMessage(EmailConfig.ED_FROM, _session, emailEntity);
        }
    }
    
    
}
