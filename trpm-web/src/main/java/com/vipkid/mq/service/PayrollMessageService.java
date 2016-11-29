/**
 * 
 */
package com.vipkid.mq.service;

import com.vipkid.mq.message.FinishOnlineClassMessage;
import com.vipkid.mq.message.FinishOnlineClassMessage.OperatorType;
import com.vipkid.trpm.entity.AssessmentReport;
import com.vipkid.trpm.entity.teachercomment.TeacherComment;

/**
 * 结束课程消息服务
 * 
 * @author zouqinghua
 * @date 2016年5月5日 下午4:37:15
 *
 */
public interface PayrollMessageService {

	/**
	 * 结束课程发送消息
	 * 
	 * @param onlineClassId
	 * @param operatorType
	 * @return
	 */
	public FinishOnlineClassMessage sendFinishOnlineClassMessage(TeacherComment teacherComment ,Long onlineClassId, OperatorType operatorType);

	
	/**
	 * 结束课程发送消息
	 * 
	 * @param onlineClassId
	 * @param operatorType
	 * @return
	 */
	public FinishOnlineClassMessage sendFinishOnlineClassMessage(AssessmentReport assessmentComment ,Long onlineClassId, OperatorType operatorType);

}
