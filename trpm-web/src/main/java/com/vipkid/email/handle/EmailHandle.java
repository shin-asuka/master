package com.vipkid.email.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.email.handle.EmailConfig.EmailFormEnum;

public class EmailHandle {

    private static Logger logger = LoggerFactory.getLogger(EmailHandle.class);


    public static String switchMail(String email, String subject, String content, EmailFormEnum emailForm) {
        switch (emailForm) {
            case TEACHVIP:
                return sendForTeachvip(email,subject,content);
            case EDUCATION:
                return sendForEducation(email,subject,content);
            default:
                logger.info("枚举错误，无法发送邮件，email:{},subject:{},content:{}",email,subject,content);
                return null;
        }
    }

    /**
     * 通过Teachvip发送邮件
     *
     * @throws InterruptedException
     * @Author:ALong (ZengWeiLong) void
     * @date 2016年3月7日
     */
    public static String sendForTeachvip(String email,String subject, String content) {
        EmailEntity reviceEntity = new EmailEntity().setHost(EmailConfig.HOST).setFromMail(EmailConfig.TC_FROM).setMailBody(content).setToMail(email).setMailSubject(subject).setPersonalName(EmailConfig.TC_FROM.split("@")[0]);
        int i = 0;
        int result = 0;
        while (i < EmailConfig.REPLY_COUNT) {
            result = EmailServer.Teachvip.sendMail(reviceEntity);
            if (result == 1) {
                break;
            }
            i++;
            logger.error("TEACHVIP：这个邮箱地址 【" + email + "】发送失败了【" + i + "】次了，等一下我继续发送");
            try {
                Thread.sleep(EmailConfig.DEFAULT_TIME);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (result == 0) {
            logger.info("TEACHVIP：【" + email + "】邮件发送失败,尝试发送到你测试环境的邮箱："+ EmailConfig.TEST_EMIAL_TO);
            reviceEntity.setToMail(EmailConfig.TEST_EMIAL_TO);
            reviceEntity.setMailSubject("【SEND FAIL】(" + email + ")" + reviceEntity.getMailSubject());
            EmailServer.Teachvip.sendMail(reviceEntity);
            return "ERROR!" + email;
        }
        logger.info(
                "TEACHVIP 最终这个Email[" + email + "] 的发送结果:[" + result + "],内容:[" + content + "]");
        return "SUCCESS!";
    }

    /**
     * 通过Education发送邮件
     *
     * @throws InterruptedException
     * @Author:ALong (ZengWeiLong) void
     * @date 2016年3月7日
     */
    public static String sendForEducation(String email,String subject, String content) {
        EmailEntity reviceEntity = new EmailEntity().setHost(EmailConfig.HOST).setFromMail(EmailConfig.ED_FROM).setMailBody(content).setToMail(email).setMailSubject(subject).setPersonalName(EmailConfig.ED_FROM.split("@")[0]);
        int i = 0;
        int result = 0;
        while (i < EmailConfig.REPLY_COUNT) {
            result = EmailServer.Education.sendMail(reviceEntity);
            if (result == 1) {
                break;
            }
            i++;
            logger.error("EDUCATION：这个邮箱地址 【" + email + "】发送失败了【" + i + "】次了，等一下我继续发送");
        }
        if (result == 0) {
            logger.info("EDUCATION：【" + email + "】邮件发送失败,尝试发送到你测试环境的邮箱："+ EmailConfig.TEST_EMIAL_TO);
            reviceEntity.setToMail(EmailConfig.TEST_EMIAL_TO);
            reviceEntity.setMailSubject("【SEND FAIL】(" + email + ")" + reviceEntity.getMailSubject());
            EmailServer.Education.sendMail(reviceEntity);
            return "ERROR!" + email;
        }
        logger.info(
                "EDUCATION 最终这个Email[" + email + "] 的发送结果:[" + result + "],这是内容:[" + content + "]");
        return "SUCCESS!";
    }
}
