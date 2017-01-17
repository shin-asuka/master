package com.vipkid.email.handle;

import com.fasterxml.jackson.core.type.TypeReference;


import com.google.common.collect.Maps;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.community.http.client.HttpClientProxy;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EmailServer {

    private static Logger logger = LoggerFactory.getLogger(EmailServer.class);

    private static final String OK = "OK";

    private static final String REQUEST_URL = PropertyConfigurer.stringValue("mail.service.url");

    private static final boolean ENABLE_EMAIL = PropertyConfigurer.booleanValue("email.enable");

    private static int sendMessage(String from, Session _session, EmailEntity reviceEntity) {
        if (!ENABLE_EMAIL){
            logger.info("Email send skip ====>：");
            return 1;
        }
        try {
            logger.info("Email send start ====>：");
            int result = sendMail(reviceEntity);
            logger.info("Email send end ====>：{}", result);
        } catch (Exception e) {
            logger.error("Email send Exception:", e);
            return 0;
        }
        logger.info("Email send complete ====>：");
        return 1;
    }

    public static int sendMail(EmailEntity mailEntity) {
        Map<String, String> requestHeader = Maps.newHashMap();
        requestHeader.put("appKey", EmailConfig.APP_KEY);
        requestHeader.put("sign", getSignture(mailEntity));

        Map<String, String> requestParam = Maps.newHashMap();
        requestParam.put("from", mailEntity.getFromMail());
        //requestParam.put("to", mailEntity.getToMail());
        requestParam.put("to", "moyonglin1@gmail.com");
        requestParam.put("subject", mailEntity.getMailSubject());
        requestParam.put("content", mailEntity.getMailBody());
        try {
            String json = HttpClientProxy.post(REQUEST_URL, requestParam, requestHeader);
            logger.info("Email send result ====>：{}", json);

            if (StringUtils.isNotBlank(json)) {
                Map<String, String> resultMap =
                        JsonTools.readValue(json, new TypeReference<HashMap<String, String>>() {
                        });
                if (!resultMap.isEmpty() && OK.equals(resultMap.get("message"))) {
                    return 1;
                }
            }
        } catch (Exception e) {
            logger.error("Email send Exception:", e);
        }
        return 0;
    }

    private static String getSignture(EmailEntity mailEntity) {
        StringBuffer content = new StringBuffer();
        content.append(mailEntity.getFromMail());
        //content.append(mailEntity.getToMail());
        content.append("moyonglin1@gmail.com");
        content.append(mailEntity.getMailSubject());
        content.append(mailEntity.getMailBody());
        content.append(EmailConfig.APP_SECRECT);
        return DigestUtils.md5Hex(content.toString().getBytes(StandardCharsets.UTF_8));
    }

    static class Teachvip {

        private static Properties _props = System.getProperties();        //获取系统环境

        private static Session _session = null;

        static {
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

        public static int sendMail(EmailEntity emailEntity) {
            return sendMessage(EmailConfig.TC_FROM, _session, emailEntity);
        }
    }


    static class Education {

        private static Properties _props = System.getProperties();        //获取系统环境

        private static Session _session = null;

        static {
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

        public static int sendMail(EmailEntity emailEntity) {
            return sendMessage(EmailConfig.ED_FROM, _session, emailEntity);
        }
    }


}
