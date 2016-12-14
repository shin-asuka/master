package com.vipkid.recruitment.event;


import java.util.Map;

import javax.annotation.Resource;

import com.google.api.client.util.Maps;

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
                        logger.info("向用户 {} sendBasicInfoPass 的邮件", teacherId);
                        ret = auditEmailService.sendBasicInfoPass(teacherId);
                        break;
                    default:
                        break;
                }
                break;
            case "INTERVIEW":
                switch (result) {
                    case "PASS":
                        logger.info("向用户 {} sendInterviewPass 的邮件", teacherId);
                        ret = auditEmailService.sendInterviewPass(teacherId);
                        break;
                    case "REAPPLY":
                        logger.info("向用户 {} sendInterviewReapply 的邮件", teacherId);
                        ret = auditEmailService.sendInterviewReapply(teacherId);
                        break;
                    default:
                        break;
                }
                break;
            case "PRACTICUM":
                switch (result) {
                    case "PASS":
                        logger.info(" 向用户{} sendPracticumPass", teacherId);
                        ret = auditEmailService.sendPracticumPass(teacherId);
                        break;
                    case "REAPPLY":
                        logger.info("向用户{}sendPracticumReapply", teacherId);
                        ret = auditEmailService.sendPracticumReapply(teacherId);
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
                        logger.info(" 向用户{} sendContractInfoPass", teacherId);
                        ret = auditEmailService.sendContractInfoPass(teacherId);
                        break;
                    case "REAPPLY":
                        logger.info("向用户{}sendContractInfoReapply", teacherId);
                        ret = auditEmailService.sendContractInfoReapply(teacherId);
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
