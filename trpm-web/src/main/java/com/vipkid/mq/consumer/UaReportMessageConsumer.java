package com.vipkid.mq.consumer;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.alibaba.fastjson.JSON;
import com.vipkid.mq.message.FinishOnlineClassMessage;
import com.vipkid.mq.message.UaReportMessage;
import com.vipkid.mq.service.PayrollMessageService;
import com.vipkid.payroll.utils.JsonMapper;
import com.vipkid.trpm.entity.AssessmentReport;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import java.lang.reflect.InvocationTargetException;

/**
 * 教师填写UA报告消息消费者
 *
 */
public class UaReportMessageConsumer implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(UaReportMessageConsumer.class);

    @Resource
    private PayrollMessageService payrollMessageService;

    @Override
    public void onMessage(Message message) {
        logger.info("接收新UA报告消息，message = {}", message.toString());
        if (message != null && message instanceof TextMessage) {
            TextMessage textMsg = (TextMessage) message;
            UaReportMessage uaReportMessage;
            try {
                uaReportMessage = (UaReportMessage) JsonMapper.fromJsonString(textMsg.getText(), UaReportMessage.class);
                logger.info("UA报告消息内容，UaReportMessage = {}", JSON.toJSONString(uaReportMessage));
                checkMessage(uaReportMessage);
                AssessmentReport assessmentReport = convertAssessmentReportFromMessage(uaReportMessage);
                if (null != assessmentReport) {
                    payrollMessageService.sendFinishOnlineClassMessage(assessmentReport,
                            assessmentReport.getOnlineClassId(),
                            FinishOnlineClassMessage.OperatorType.ADD_UNIT_ASSESSMENT);
                }
            } catch (Exception e) {
                logger.error("接收UA Report消息并发送FinishOnlineClassMessage时出现问题", e);
            }
        }
    }

    private void checkMessage(UaReportMessage message) {
        Preconditions.checkArgument(null != message, "UaReport信息不能为空！");
        Preconditions.checkArgument(message.getOnlineClassId() > 0, "UaReport的OnlineClassId不能为空！");
    }

    private AssessmentReport convertAssessmentReportFromMessage(UaReportMessage uaReportMessage) {
        AssessmentReport assessmentReport = null;
        if (null != uaReportMessage) {
            assessmentReport = new AssessmentReport();
            assessmentReport.setOnlineClassId(uaReportMessage.getOnlineClassId());
            assessmentReport.setCreateDateTime(uaReportMessage.getSubmitDateTime());
            assessmentReport.setHasUnitAssessment(uaReportMessage.getHasUnitAssessment());
        }
        return assessmentReport;
    }
}
