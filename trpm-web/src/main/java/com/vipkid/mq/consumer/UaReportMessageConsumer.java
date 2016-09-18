package com.vipkid.mq.consumer;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

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
        logger.info("调用payrollItemService.createPayrollItemForNewVIPKIDEnroll 创建工资项 ：message = {}", message.toString());
        if (message != null && message instanceof TextMessage) {
            TextMessage textMsg = (TextMessage) message;
            UaReportMessage uaReportMessage;
            try {
                uaReportMessage = (UaReportMessage) JsonMapper.fromJsonString(textMsg.getText(), UaReportMessage.class);
                checkMessage(uaReportMessage);
                AssessmentReport assessmentReport = converAssessmentReportFromMessage(uaReportMessage);
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

    private AssessmentReport converAssessmentReportFromMessage(UaReportMessage uaReportMessage) {
        AssessmentReport assessmentReport = null;
        if (null != uaReportMessage) {
            assessmentReport = new AssessmentReport();
            try {
                BeanUtils.copyProperties(assessmentReport, uaReportMessage);
            } catch (IllegalAccessException e) {
                logger.error("UA Report消息转为UA Report实体时出现异常", uaReportMessage.toString());
                return null;
            } catch (InvocationTargetException e) {
                logger.error("UA Report消息转为UA Report实体时出现异常", uaReportMessage.toString());
                return null;
            }
        }
        return assessmentReport;
    }
}
