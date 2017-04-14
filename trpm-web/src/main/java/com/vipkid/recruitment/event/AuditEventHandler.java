package com.vipkid.recruitment.event;


import java.util.Map;

import javax.annotation.Resource;

import com.google.api.client.util.Maps;

import com.vipkid.recruitment.common.service.AuditPushMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vipkid.recruitment.common.service.AuditEmailService;

/**
 * 处理管理端在 TR 过程中的审核事件
 *
 * @author Austin.Cao  Date: 05/12/2016
 */
@Component
public class AuditEventHandler implements AuditHandler {

    private static Logger logger = LoggerFactory.getLogger(AuditEventHandler.class);

    @Resource
    private AuditEmailService auditEmailService;

    @Resource
    private AuditPushMessageService pushMessageService;

    @Override
    public Map<String, Object>  onAuditEvent(AuditEvent event) {
        Map<String, Object> ret = Maps.newHashMap();
        Long teacherId = event.getSourceId();
        String status = event.getStatus();
        String result = event.getAuditResult();
        logger.info("teacherId:{},  status:{}  result:{}",teacherId,status,result);
        switch (status) {
            case "BASIC_INFO":
                switch (result) {
                    case "PASS":
                        logger.info("向用户 {} sendBasicInfoPass 的邮件, 并且发送message", teacherId);
                        ret = auditEmailService.sendBasicInfoPass(teacherId);
                        pushMessageService.pushAndSaveMessage(teacherId);
                        break;
                    default:
                        break;
                }
                break;
            case "INTERVIEW":
                switch (result) {
                    case "PASS":
                        logger.info("向用户 {} sendInterviewPass 的邮件, 并且发送message", teacherId);
                        //ret = auditEmailService.sendInterviewPass(teacherId);
                        pushMessageService.pushAndSaveMessage(teacherId);
                        break;
                    case "REAPPLY":
                        logger.info("向用户 {} sendInterviewReapply 的邮件, 并且发送message", teacherId);
                        ret = auditEmailService.sendInterviewReapply(teacherId);
                        pushMessageService.pushAndSaveMessage(teacherId);
                        break;
                    default:
                        break;
                }
                break;
            case "TRAINING":
                switch (result) {
                    case "PASS":
                        logger.info("TEACHING_PREP.PASS, 向用户 {} 发送message", teacherId);
                        pushMessageService.pushAndSaveMessage(teacherId);
                        break;
                    default:
                        break;
                }
                break;
            case "PRACTICUM":
                switch (result) {
                    case "PASS":
                        logger.info(" 向用户{} sendPracticumPass，并且发送消息", teacherId);
                        ret = auditEmailService.sendPracticumPass(teacherId);
                        pushMessageService.pushAndSaveMessage(teacherId);
                        break;
                    case "REAPPLY":
                        logger.info("向用户{}sendPracticumReapply，并且发送消息", teacherId);
                        ret = auditEmailService.sendPracticumReapply(teacherId);
                        pushMessageService.pushAndSaveMessage(teacherId);
                        break;
                    case "PRACTICUM2":
                        logger.info("向用户{}sendPracticum2Start", teacherId);
                        ret = auditEmailService.sendPracticum2Start(teacherId);
                        break;
                    default:
                        break;
                }
                break;
            case "CONTRACT_INFO":
                switch (result) {
                    case "PASS":
                        logger.info(" 向用户{} sendContractInfoPass，并且发送消息", teacherId);
                        ret = auditEmailService.sendContractInfoPass(teacherId);
                        pushMessageService.pushAndSaveMessage(teacherId);
                        break;
                    case "REAPPLY":
                        logger.info("向用户{}sendContractInfoReapply，并且发送消息", teacherId);
                        ret = auditEmailService.sendContractInfoReapply(teacherId);
                        pushMessageService.pushAndSaveMessage(teacherId);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return ret;
    }
}
