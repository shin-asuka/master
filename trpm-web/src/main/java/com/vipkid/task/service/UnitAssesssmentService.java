/**
 * 
 */
package com.vipkid.task.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
import com.vipkid.email.template.TemplateUtils;
import com.vipkid.http.service.AssessmentHttpService;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.vo.OnlineClassVo;
import com.vipkid.task.utils.UADateUtils;
import com.vipkid.trpm.dao.LessonDao;
import com.vipkid.trpm.dao.OnlineClassDao;

/**
 * @author zouqinghua
 * @date 2016年8月19日  下午5:28:11
 *
 */
@Service
public class UnitAssesssmentService {

	private static final Logger logger = LoggerFactory.getLogger(UnitAssesssmentService.class);
	
	@Autowired
    private OnlineClassDao onlineClassDao;
	
	@Autowired
    private LessonDao lessonDao;

	@Autowired
	private AssessmentHttpService assessmentHttpService;
	
	public void remindTeacherUnitAssessmentFor6Hour(){
		
		//查询出6个小时以前已经AS_SCHEDULED的课程
		Date startDate = UADateUtils.getCFUARemindTimeRange(7);
		Date endDate = UADateUtils.getCFUARemindTimeRange(6);
		
		String startTime = UADateUtils.format(startDate, UADateUtils.defaultFormat) ;
		String endTime = UADateUtils.format(endDate, UADateUtils.defaultFormat) ;
		
		logger.info("查询出6个小时以前已经AS_SCHEDULED的课程  startTime = {},endTime = {}",startTime,endTime);
		
		List<Map<String, Object>> list = onlineClassDao.findMajorCourseListByStartTimeAndEndTime(startTime, endTime ,null);
		logger.info("Get 6h unSubmit OnlineClass list = {}",JsonUtils.toJSONString(list));
		
		List<OnlineClassVo> onlineClassVos = getOnlineClassVoList(list);
		
		OnlineClassVo onlineClassVo = new OnlineClassVo();
		Map<Long,OnlineClassVo> ocMap = Maps.newHashMap();
		if(CollectionUtils.isNotEmpty(onlineClassVos)){
			for (OnlineClassVo oc : onlineClassVos) { //数据格式转换
				Long id = oc.getId();
				onlineClassVo.getIdList().add(id);
				ocMap.put(id, oc);
			}
			onlineClassVo.setIdListStr(StringUtils.join(onlineClassVo.getIdList(),","));
			onlineClassVo.getIdList().clear();
			
			//调用homework服务查询为完成UA报告的课程
			OnlineClassVo onlineClassVoUnSubmit = assessmentHttpService.findUnSubmitonlineClassVo(onlineClassVo );
			logger.info("Result 6h unSubmit OnlineClass  = {}",JsonUtils.toJSONString(onlineClassVoUnSubmit));
			sendEmail(onlineClassVoUnSubmit, ocMap,"UARemindTeacher6hour.html","UARemindTeacher6hourTitle.html");
		}
		
	}
	
	public void remindTeacherUnitAssessmentFor12Hour(){
		
		//查询出12个小时以前已经AS_SCHEDULED的课程
		Date startDate = UADateUtils.getCFUARemindTimeRange(13);
		Date endDate = UADateUtils.getCFUARemindTimeRange(12);
		
		String startTime = UADateUtils.format(startDate, UADateUtils.defaultFormat) ;
		String endTime = UADateUtils.format(endDate, UADateUtils.defaultFormat) ;
		
		logger.info("查询出12个小时以前已经AS_SCHEDULED的课程  startTime = {},endTime = {}",startTime,endTime);
		
		List<Map<String, Object>> list = onlineClassDao.findMajorCourseListByStartTimeAndEndTime(startTime, endTime, null);
		logger.info("Get 12h unSubmit OnlineClass list = {}",JsonUtils.toJSONString(list));
		
		List<OnlineClassVo> onlineClassVos = getOnlineClassVoList(list);
		
		OnlineClassVo onlineClassVo = new OnlineClassVo();
		Map<Long,OnlineClassVo> ocMap = Maps.newHashMap();
		if(CollectionUtils.isNotEmpty(onlineClassVos)){
			for (OnlineClassVo oc : onlineClassVos) { //数据格式转换
				Long id = oc.getId();
				onlineClassVo.getIdList().add(id);
				ocMap.put(id, oc);
			}
			onlineClassVo.setIdListStr(StringUtils.join(onlineClassVo.getIdList(),","));
			onlineClassVo.getIdList().clear();

			//调用homework服务查询为完成UA报告的课程
			OnlineClassVo onlineClassVoUnSubmit = assessmentHttpService.findUnSubmitonlineClassVo(onlineClassVo);
			logger.info("Result 12h unSubmit OnlineClass  = {}",JsonUtils.toJSONString(onlineClassVoUnSubmit));
			sendEmail(onlineClassVoUnSubmit, ocMap,"UARemindTeacher12hour.html","UARemindTeacher12hourTitle.html");
		}
		
	}

	public void remindTeacherUnitAssessmentFor24Hour(){

		//查询出24个小时以前已经AS_SCHEDULED的课程
		Date startDate = UADateUtils.getCFUARemindTimeRange(25);
		Date endDate = UADateUtils.getCFUARemindTimeRange(24);

		String startTime = UADateUtils.format(startDate, UADateUtils.defaultFormat) ;
		String endTime = UADateUtils.format(endDate, UADateUtils.defaultFormat) ;

		logger.info("查询出24个小时以前已经AS_SCHEDULED的课程  startTime = {},endTime = {}",startTime,endTime);

		List<Map<String, Object>> list = onlineClassDao.findMajorCourseListByStartTimeAndEndTime(startTime, endTime, null);
		logger.info("Get 24h unSubmit OnlineClass list = {}",JsonUtils.toJSONString(list));

		List<OnlineClassVo> onlineClassVos = getOnlineClassVoList(list);

		OnlineClassVo onlineClassVo = new OnlineClassVo();
		Map<Long,OnlineClassVo> ocMap = Maps.newHashMap();
		if(CollectionUtils.isNotEmpty(onlineClassVos)){
			for (OnlineClassVo oc : onlineClassVos) { //数据格式转换
				Long id = oc.getId();
				onlineClassVo.getIdList().add(id);
				ocMap.put(id, oc);
			}
			onlineClassVo.setIdListStr(StringUtils.join(onlineClassVo.getIdList(),","));
			onlineClassVo.getIdList().clear();

			//调用homework服务查询为完成UA报告的课程
			OnlineClassVo onlineClassVoUnSubmit = assessmentHttpService.findUnSubmitonlineClassVo(onlineClassVo );
			logger.info("Result 24h unSubmit OnlineClass  = {}",JsonUtils.toJSONString(onlineClassVoUnSubmit));
			sendEmail(onlineClassVoUnSubmit, ocMap,"UARemindTeacher24hour.html","UARemindTeacher24hourTitle.html");
		}

	}

	public void sendEmail(OnlineClassVo onlineClassVoUnSubmit,Map<Long,OnlineClassVo> ocMap,String contentTemplate,String titleTemplate){
		if(onlineClassVoUnSubmit!=null && CollectionUtils.isNotEmpty(onlineClassVoUnSubmit.getIdList())){
			List<Long> idList = onlineClassVoUnSubmit.getIdList();
			for (Long id : idList) {
				OnlineClassVo oc = ocMap.get(id);
				if(oc!=null && StringUtils.isNoneBlank(oc.getTeacherEmail())){
					String email = oc.getTeacherEmail(); //获取教师邮箱发送邮件
					String name = oc.getTeacherName();
					String timezone = oc.getTimezone();
					//email = "yangchao@vipkid.com.cn"; //
					logger.info("send Email to teacher name= {},email = {} , contentTemplate = {}, titleTemplate = {}",name,email,contentTemplate,titleTemplate);
					String scheduledDateTime = UADateUtils.format(oc.getScheduledDateTime(), "MM/dd/YYYY",timezone) ;
					scheduledDateTime +=" at "+UADateUtils.format(oc.getScheduledDateTime(), "HH:mm",timezone) ;
					try {
	                    Map<String, String> paramsMap = Maps.newHashMap();
	                    paramsMap.put("scheduledDateTime", scheduledDateTime);
						if (oc.getTeacherFirstName()!= null){
							paramsMap.put("teacherName", oc.getTeacherFirstName());
						}else if (oc.getTeacherName() != null){
							paramsMap.put("teacherName", oc.getTeacherName());
						}
	                    Map<String, String> emailMap = new TemplateUtils().readTemplate(contentTemplate, paramsMap, titleTemplate);
	                    EmailEngine.addMailPool(email, emailMap,EmailFormEnum.EDUCATION);
	                    //EmailHandle emailHandle = new EmailHandle(email, emailMap.get("title"), emailMap.get("content"), EmailFormEnum.TEACHVIP);
	                    //emailHandle.sendMail();  
	                    logger.info("send Email success  teacher = {},email = {},contentTemplate = {}, titleTemplate = {}",name,email,contentTemplate,titleTemplate);
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
				JSONObject jsonObject =(JSONObject) JSONObject.toJSON(map);
				OnlineClassVo onlineClassVo = new OnlineClassVo();
				onlineClassVo.setId(jsonObject.getLong("id"));
				onlineClassVo.setTeacherId(jsonObject.getLong("teacherId"));
				onlineClassVo.setLessonId(jsonObject.getLong("lessonId"));
				onlineClassVo.setTeacherFirstName(jsonObject.getString("teacherFirstName"));
				onlineClassVo.setTeacherName(jsonObject.getString("teacherName"));
				onlineClassVo.setTeacherEmail(jsonObject.getString("teacherEmail"));
				onlineClassVo.setScheduledDateTime(jsonObject.getDate("scheduledDateTime"));
				onlineClassVos.add(onlineClassVo);
			}
		}
		return onlineClassVos;
	}
}
