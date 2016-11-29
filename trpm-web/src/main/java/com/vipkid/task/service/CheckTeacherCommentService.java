/**
 * 
 */
package com.vipkid.task.service;

import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
import com.vipkid.email.templete.TempleteUtils;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.vo.OnlineClassVo;
import com.vipkid.task.utils.UADateUtils;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherCommentDao;
import com.vipkid.trpm.entity.TeacherComment;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xingxuelin
 * @date 2016年9月9日  下午5:28:11
 *
 */
@Service
public class CheckTeacherCommentService {

	private static final Logger logger = LoggerFactory.getLogger(CheckTeacherCommentService.class);
	
	@Autowired
    private OnlineClassDao onlineClassDao;
	
	@Autowired
	private TeacherCommentDao teacherCommentDao;
	
	public void remindTeacherComment(int hours){
		
		//查询出hours个小时以前已经AS_SCHEDULED的课程
		Date startDate = UADateUtils.getDateByBeforeHours(hours+1);
		Date endDate = UADateUtils.getDateByBeforeHours(hours);
		
		String startTime = UADateUtils.format(startDate, UADateUtils.defaultFormat) ;
		String endTime = UADateUtils.format(endDate, UADateUtils.defaultFormat) ;
		
		logger.info("查询出"+hours+"个小时以前已经AS_SCHEDULED的课程  startTime = {},endTime = {}",startTime,endTime);
		
		List<Map<String, Object>> list = onlineClassDao.findOnlineClassList4CheckTeacherComment(startTime, endTime ,null);
		logger.info("Get unSubmit OnlineClass list = {}", JsonUtils.toJSONString(list));
		
		List<OnlineClassVo> onlineClassVos = getOnlineClassVoList(list);
		
		OnlineClassVo onlineClassVo = new OnlineClassVo();
		Map<Long,OnlineClassVo> ocMap = Maps.newHashMap();
		if(CollectionUtils.isNotEmpty(onlineClassVos)){
			for (OnlineClassVo oc : onlineClassVos) { //数据格式转换
				Long id = oc.getId();
				onlineClassVo.getIdList().add(id);
				ocMap.put(id, oc);
			}
			
			//调用homework服务查询为完成TeacherComment的课程
			List<TeacherComment> teacherCommentSubmit = teacherCommentDao.batchGetByOnlineClassIds(onlineClassVo.getIdList());
			OnlineClassVo onlineClassVoUnSubmit = new OnlineClassVo();
			onlineClassVoUnSubmit.setIdList(onlineClassVo.getIdList());
			teacherCommentSubmit.forEach(x->onlineClassVoUnSubmit.getIdList().remove(x.getOnlineClassId()));
			logger.info("Result unSubmit OnlineClass  = {}", JsonUtils.toJSONString(onlineClassVoUnSubmit));
			sendEmail(onlineClassVoUnSubmit, ocMap,"FeedbackRemindTeacher"+hours+"hour.html","FeedbackRemindTeacher"+hours+"hourTitle.html");
		}
		
	}

	public void sendEmail(OnlineClassVo onlineClassVoUnSubmit, Map<Long,OnlineClassVo> ocMap, String contentTemplete, String titleTemplete){
		if(onlineClassVoUnSubmit!=null && CollectionUtils.isNotEmpty(onlineClassVoUnSubmit.getIdList())){
			List<Long> idList = onlineClassVoUnSubmit.getIdList();
			for (Long id : idList) {
				OnlineClassVo oc = ocMap.get(id);
				if(oc!=null && StringUtils.isNoneBlank(oc.getTeacherEmail())){
					String email = oc.getTeacherEmail(); //获取教师邮箱发送邮件
					String name = oc.getTeacherName();
					logger.info("send Email to teacher name= {},email = {} , contentTemplete = {}, titleTemplete = {}",name,email,contentTemplete,titleTemplete);
					String scheduledDateTime = UADateUtils.format(oc.getScheduledDateTime(), "MM/dd/YYYY") ;
					scheduledDateTime +=" at "+ UADateUtils.format(oc.getScheduledDateTime(), "HH:mm") ;
					try {
	                    Map<String, String> paramsMap = Maps.newHashMap();
	                    paramsMap.put("scheduledDateTime", scheduledDateTime);

	                    Map<String, String> emailMap = new TempleteUtils().readTemplete(contentTemplete, paramsMap, titleTemplete);
	                    new EmailEngine().addMailPool(email, emailMap, EmailFormEnum.EDUCATION);
	                    //EmailHandle emailHandle = new EmailHandle(email, emailMap.get("title"), emailMap.get("content"), EmailFormEnum.TEACHVIP);
	                    //emailHandle.sendMail();  
	                    logger.info("send Email success  teacher = {},email = {},contentTemplete = {}, titleTemplete = {}",name,email,contentTemplete,titleTemplete);
	                } catch (Exception e) {
	                    logger.error("Send TQ mail error", e);
	                }
				}
			}
		}
	}
	
	public List<OnlineClassVo> getOnlineClassVoList(List<Map<String, Object>> list){
		List<OnlineClassVo> onlineClassVos = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(list)){
			for (Map<String,Object> map : list) {
				JSONObject jsonObject = (JSONObject) JSONObject.toJSON(map);
				
				OnlineClassVo onlineClassVo = new OnlineClassVo();
				Long id = jsonObject.getLong("id");
				
				onlineClassVo.setId(id);
				onlineClassVo.setTeacherId(jsonObject.getLong("teacherId"));
				onlineClassVo.setLessonId(jsonObject.getLong("lessonId"));
				onlineClassVo.setTeacherName(jsonObject.getString("teacherName"));
				onlineClassVo.setTeacherEmail(jsonObject.getString("teacherEmail"));
				onlineClassVo.setScheduledDateTime(jsonObject.getDate("scheduledDateTime"));
				onlineClassVos.add(onlineClassVo);
			}
		}
		return onlineClassVos;
	}
}
