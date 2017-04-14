package com.vipkid.email.handle;

import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

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

    public static String switchMail(EmailEntity reviceEntity, EmailFormEnum emailForm) {
        switch (emailForm) {
            case TEACHVIP:
                return sendForSupplier(reviceEntity, (v)-> EmailServer.Teachvip.sendMail(reviceEntity));
            case EDUCATION:
                return sendForSupplier(reviceEntity, (v)-> EmailServer.Education.sendMail(reviceEntity));
            default:
                logger.info("枚举错误，无法发送邮件，email:{}, subject:{}, content:{}", reviceEntity.getToMail(),
                        reviceEntity.getMailSubject(), reviceEntity.getMailBody());
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
                "TEACHVIP 最终这个Email[" + email + "] 的发送结果:[" + result + "],这是标题:[" + subject + "],内容:[" + content + "]");
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
                "EDUCATION 最终这个Email[" + email + "] 的发送结果:[" + result + "],这是标题:[" + subject + "],这是内容:[" + content + "]");
        return "SUCCESS!";
    }

    public static String sendForSupplier(EmailEntity reviceEntity, Function<EmailEntity, Integer> function){
        reviceEntity.setHost(EmailConfig.HOST).setFromMail(EmailConfig.TC_FROM)
                .setPersonalName(EmailConfig.TC_FROM.split("@")[0]);
        // 重试机制
        int count = 0, result = 0;
        while (count < EmailConfig.REPLY_COUNT) {
            result = function.apply(reviceEntity);
            if (1 == result) {
                break;
            } else {
                count++;
                logger.error("邮箱地址 [" + reviceEntity.getToMail() + "] 发送失败了 [" + count + "] 次了，等待继续发送");
            }
        }
        // 发送失败，通知管理员
        if (0 == result) {
            logger.info("邮箱地址 [" + reviceEntity.getToMail() + "] 邮件发送失败, 尝试发送到你测试环境的邮箱: "
                    + EmailConfig.TEST_EMIAL_TO);

            reviceEntity.setToMail(EmailConfig.TEST_EMIAL_TO);
            reviceEntity.setMailSubject("【SEND FAIL】(" + reviceEntity.getToMail() + ")"
                    + reviceEntity.getMailSubject());

            function.apply(reviceEntity);
            return "ERROR!" + reviceEntity.getToMail();
        }

        logger.info("最终这个Email [" + reviceEntity.getToMail() + "] 的发送结果:[" + result + "], 标题:["
                + reviceEntity.getMailSubject() + "], 内容:[" + reviceEntity.getMailBody() + "]");
        return "SUCCESS!";
    }

}
