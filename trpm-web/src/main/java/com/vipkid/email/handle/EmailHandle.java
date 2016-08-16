package com.vipkid.email.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.email.handle.EmailConfig.EmailFormEnum;

public class EmailHandle {

    private Logger logger = LoggerFactory.getLogger(EmailHandle.class);

    // 发送目标地址
    protected String email;
    // 发送标题
    protected String subject;
    // 发送内容
    protected String content;
    // 发送人
    protected EmailFormEnum emailForm;

    public EmailHandle(String email, String subject, String content, EmailFormEnum emailForm) {
        this.email = email;
        this.subject = subject;
        this.content = content;
        this.emailForm = emailForm;
    }

    public String sendMail() {
        if (this.emailForm == EmailFormEnum.TEACHVIP) {
            return this.sendForTeachvip();
        } else if (this.emailForm == EmailFormEnum.EDUCATION) {
            return this.sendForEducation();
        }
        return "ERROR!" + email;
    }

    /**
     * 通过Teachvip发送邮件
     *
     * @throws InterruptedException
     * @Author:ALong (ZengWeiLong) void
     * @date 2016年3月7日
     */
    public String sendForTeachvip() {
        EmailEntity reviceEntity = new EmailEntity().setHost(EmailConfig.HOST)
                .setFromMail(EmailConfig.TC_FROM).setMailBody(content).setToMail(email)
                .setMailSubject(subject).setPersonalName(EmailConfig.TC_FROM.split("@")[0]);
        int i = 0;
        int result = 0;
        while (i < EmailConfig.REPLY_COUNT) {
            i++;
            if (i % 2 == 0) {
                result = new EmailServer().sendMail(reviceEntity,
                        new EmailAuthenticator(EmailConfig.TC_USERNAME, EmailConfig.TC_PASSWORD));
            } else {
                result = new EmailServer("25").sendMail(reviceEntity,
                        new EmailAuthenticator(EmailConfig.TC_USERNAME, EmailConfig.TC_PASSWORD));
            }
            if (result == 1) {
                break;
            }
            logger.error("TEACHVIP：这个邮箱地址 【" + email + "】发送失败了【" + i + "】次了，等一下我继续发送");
            try {
                Thread.sleep(EmailConfig.DEFAULT_TIME);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (result == 0) {
            logger.info("TEACHVIP：【" + email + "】这家伙的邮件发送失败,尝试发送到你测试环境的邮箱："
                    + EmailConfig.TEST_EMIAL_TO);
            reviceEntity.setToMail(EmailConfig.TEST_EMIAL_TO);
            reviceEntity
                    .setMailSubject("【SEND FAIL】(" + email + ")" + reviceEntity.getMailSubject());
            new EmailServer().sendMail(reviceEntity,
                    new EmailAuthenticator(EmailConfig.TC_USERNAME, EmailConfig.TC_PASSWORD));
            return "ERROR!" + email;
        }
        logger.info(
                "TEACHVIP 最终这个Email[" + email + "] 的发送结果:[" + result + "],这是内容:[" + content + "]");
        return "SUCCESS!";
    }

    /**
     * 通过Education发送邮件
     *
     * @throws InterruptedException
     * @Author:ALong (ZengWeiLong) void
     * @date 2016年3月7日
     */
    public String sendForEducation() {
        EmailEntity reviceEntity = new EmailEntity().setHost(EmailConfig.HOST)
                .setFromMail(EmailConfig.ED_FROM).setMailBody(content).setToMail(email)
                .setMailSubject(subject).setPersonalName(EmailConfig.ED_FROM.split("@")[0]);
        int i = 0;
        int result = 0;
        while (i < EmailConfig.REPLY_COUNT) {
            i++;
            if (i % 2 == 0) {
                result = new EmailServer().sendMail(reviceEntity,
                        new EmailAuthenticator(EmailConfig.ED_USERNAME, EmailConfig.ED_PASSWORD));
            } else {
                result = new EmailServer("25").sendMail(reviceEntity,
                        new EmailAuthenticator(EmailConfig.ED_USERNAME, EmailConfig.ED_PASSWORD));
            }
            if (result == 1) {
                break;
            }
            logger.error("EDUCATION：这个邮箱地址 【" + email + "】发送失败了【" + i + "】次了，等一下我继续发送");
        }
        if (result == 0) {
            logger.info("EDUCATION：【" + email + "】这家伙的邮件发送失败,尝试发送到你测试环境的邮箱："
                    + EmailConfig.TEST_EMIAL_TO);
            reviceEntity.setToMail(EmailConfig.TEST_EMIAL_TO);
            reviceEntity
                    .setMailSubject("【SEND FAIL】(" + email + ")" + reviceEntity.getMailSubject());
            new EmailServer().sendMail(reviceEntity,
                    new EmailAuthenticator(EmailConfig.ED_USERNAME, EmailConfig.ED_PASSWORD));
            return "ERROR!" + email;
        }
        logger.info(
                "EDUCATION 最终这个Email[" + email + "] 的发送结果:[" + result + "],这是内容:[" + content + "]");
        return "SUCCESS!";
    }
}
