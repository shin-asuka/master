package com.vipkid.portal.activity.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.vipkid.email.template.TemplateUtils;
import com.vipkid.enums.ShareActivityExamEnum;
import com.vipkid.enums.ShareActivityExamEnum.StatusEnum;
import com.vipkid.portal.activity.dto.ClickHandleDto;
import com.vipkid.portal.activity.dto.SubmitHandleDto;
import com.vipkid.portal.activity.vo.CheckUrlVo;
import com.vipkid.portal.activity.vo.StartHandleVo;
import com.vipkid.portal.activity.vo.SubmitHandleVo;
import com.vipkid.teacher.tools.repository.dao.MyBatisTools;
import com.vipkid.teacher.tools.utils.NumericUtils;
import com.vipkid.teacher.tools.utils.ReturnMapUtils;
import com.vipkid.trpm.constant.ApplicationConstant.RedisConstants;
import com.vipkid.trpm.dao.ShareActivityExamDao;
import com.vipkid.trpm.dao.ShareExamDetailDao;
import com.vipkid.trpm.dao.ShareLinkSourceDao;
import com.vipkid.trpm.dao.ShareRecordDao;
import com.vipkid.trpm.entity.ShareActivityExam;
import com.vipkid.trpm.entity.ShareExamDetail;
import com.vipkid.trpm.entity.ShareLinkSource;
import com.vipkid.trpm.entity.ShareRecord;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.proxy.RedisProxy;

@Service
public class ReferralActivityService {
	
	private final static Logger logger = LoggerFactory.getLogger(ReferralActivityService.class);
	
	@Autowired
	private ShareLinkSourceDao shareLinkSourceDao;
	
	@Autowired
	private ShareRecordDao shareRecordDao;
	
	@Autowired
	private ShareActivityExamDao shareActivityExamDao;
	
	@Autowired
	private ShareExamDetailDao shareExamDetailDao;

    @Autowired
    private RedisProxy redisProxy;
	
	/**
	 * link入口被单击次数的更新
	 * @param linkSourceId
	 */
	public Map<String,Object> updateLinkSourceClick(Long linkSourceId){
		Map<String,Object> paramMap = Maps.newHashMap();
		if(NumericUtils.isNotNullOrZeor(linkSourceId)){
			ShareLinkSource bean = this.shareLinkSourceDao.getById(linkSourceId);
			if(NumericUtils.isNull(bean)){
				paramMap.put("status", false);
				return paramMap;
			}
			bean.setLinkClick(bean.getLinkClick()+1);
			this.shareLinkSourceDao.updateById(bean);
			paramMap.put("status", true);
		}
		return paramMap;
	}
	
	/**
	 * 通过分享ID 更新分享链接被单击次数
	 * @param linkSourceId
	 */
	public Map<String,Object> updateShareRecordClick(Long linkSourceId,Long shareRecordId){
		Map<String,Object> paramMap = Maps.newHashMap();
		if(NumericUtils.isNotNullOrZeor(linkSourceId)){
			ShareRecord bean = this.shareRecordDao.getById(shareRecordId);
			if(NumericUtils.isNull(bean)){
				paramMap.put("status", false);
				return paramMap;
			}
			bean.setCountClick(bean.getCountClick()+1);
			this.shareRecordDao.updateById(bean);
			paramMap.put("status", true);
		}
		return paramMap;
	}
	
	/**
	 * 参与人分享逻辑
	 * 通过key 获取考试记录，得到上层分享ID，上层分享信息。通过上层分享信息插入本层分享信息
	 * 插入考试记录
	 * @param candidateKey
	 * @param candidateIp
	 * @param linkSourceId
	 * @param activityExamId
	 * @return
	 */
	public Map<String,Object> updateCandidateShare(String candidateKey,String candidateIp,Long linkSourceId, Long activityExamId){
		ShareActivityExam activityExam = this.shareActivityExamDao.getById(activityExamId);
		if(NumericUtils.isNull(activityExam)){
			return ReturnMapUtils.returnFail(-2, "没有找到考试记录,非法的分享");
		}
		if(!StringUtils.equals(candidateKey, activityExam.getCandidateKey())){
			return ReturnMapUtils.returnFail(-3, "非法提交，测试ID "+activityExamId+" 和 candidateKey "+candidateKey+"不匹配");
		}
		if(NumericUtils.isNull(activityExam.getShareRecordId())){
			return ReturnMapUtils.returnFail(-4, "考试记录中没有分享ID,非法的分享");
		}
		ShareRecord preRecord = this.shareRecordDao.getById(activityExam.getShareRecordId());
		if(NumericUtils.isNull(preRecord)){
			return ReturnMapUtils.returnFail(-5, "考试记录中没有找到上层分享数据,非法的分享","分享ID:"+activityExam.getShareRecordId());
		}
		ShareRecord newRecord = new ShareRecord();
		newRecord.setCandidateKey(candidateKey);
		newRecord.setCandidateIp(candidateIp);
		newRecord.setCountClick(0L);
		newRecord.setExamVersion(preRecord.getExamVersion());
		newRecord.setLinkSourceId(linkSourceId);
		newRecord.setTeacherId(preRecord.getTeacherId());
		newRecord.setShareLevel(preRecord.getShareLevel()+1);
		newRecord.setShareTime(new Date());
		newRecord.setActivityExamId(activityExam.getId());
		this.shareRecordDao.insertSelective(newRecord);
		Map<String,Object> resultMap = Maps.newHashMap();
		resultMap.put("shareRecordId", newRecord.getId());
		return ReturnMapUtils.returnSuccess(resultMap);
	}
	
	
	/**
	 * 老师的分享
	 * @param candidateKey
	 * @param candidateIp
	 * @param linkSourceId
	 * @param activityExamId
	 * @return
	 */
	public Map<String,Object> updateTeacherShare(String candidateKey,String candidateIp,Long linkSourceId ,Long activityExamId){
		ShareActivityExam activityExam = this.shareActivityExamDao.getById(activityExamId);
		if(NumericUtils.isNull(activityExam)){
			return ReturnMapUtils.returnFail(-2, "没有找到考试记录,非法的分享");
		}
		if(!StringUtils.equals(candidateKey, activityExam.getCandidateKey())){
			logger.info("candidateKey:" + candidateKey + " , " + activityExam.getCandidateKey());
			return ReturnMapUtils.returnFail(-3, "非法提交，测试ID:"+activityExamId+" 和 candidateKey="+candidateKey+"不匹配");
		}
		ShareRecord newRecord = new ShareRecord();
		newRecord.setCandidateKey(candidateKey);
		newRecord.setCandidateIp(candidateIp);
		newRecord.setCountClick(0L);
		newRecord.setExamVersion(this.getExamVersion());
		newRecord.setLinkSourceId(linkSourceId);
		newRecord.setTeacherId(Long.valueOf(candidateKey));
		newRecord.setShareLevel(1L);
		newRecord.setShareTime(new Date());
		newRecord.setActivityExamId(activityExam.getId());
		this.shareRecordDao.insertSelective(newRecord);
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("shareRecordId", newRecord.getId());
		return ReturnMapUtils.returnSuccess(resultMap);
	}
	
	/**
	 * 一般参与者
	 * 开始考试,生成试卷 测试
	 * @param shareRecordId 分享ID
	 * @param candidateKey 参与人Key
	 * @return
	 */
	public StartHandleVo updateStartEaxm(Long shareRecordId, String candidateKey,String candidateIp,int index){
		if(StringUtils.isBlank(candidateKey)){
			candidateKey = UUID.randomUUID().toString().replace("-", "").toUpperCase();
		}
		ShareRecord preRecord = this.shareRecordDao.getById(shareRecordId);
		ShareActivityExam bean = new ShareActivityExam();
		bean.setExamVersion(preRecord.getExamVersion());
		bean.setStartDateTime(new Date());
		bean.setCandidateKey(candidateKey);
		bean.setCandidateIp(candidateIp);
		bean.setLinkSourceId(preRecord.getLinkSourceId());
		bean.setShareRecordId(shareRecordId);
		bean.setStatus(0);
		this.shareActivityExamDao.insertSelective(bean);
		
		String questionId = getExamPageContentForIndex(this.getExamVersion(),index);
		ShareExamDetail shareExamDetail = new ShareExamDetail();
		shareExamDetail.setActivityExamId(bean.getId());
		shareExamDetail.setQuestionId(questionId);
		shareExamDetail.setQuestionIndex(1L);
		shareExamDetail.setStartDateTime(new Date());
		shareExamDetail.setStatus(0);
		this.shareExamDetailDao.insertSelective(shareExamDetail);
		StartHandleVo beanVo = new StartHandleVo();
		beanVo.setActivityExamId(bean.getId());
		beanVo.setCandidateKey(bean.getCandidateKey());
		beanVo.setQuestionId(questionId);
		beanVo.setQuestionIndex(index);
		beanVo.setRefereeId(preRecord.getTeacherId());
		return beanVo;
	}
	
	/**
	 * 老师参与
	 * 开始考试,生成试卷 测试
	 * @param teacherId 分享ID
	 * @param candidateKey 参与人Key
	 * @return
	 */
	public StartHandleVo updateStartEaxmForTeacher(Long teacherId, Long linkSourceId, String candidateIp,int index){
		ShareActivityExam selectBean = new ShareActivityExam();
		selectBean.setTeacherId(teacherId);
		selectBean.setStatus(StatusEnum.PENDING.val());
		Map<String, Object> paramMap = MyBatisTools.toMap(selectBean);
		List<ShareActivityExam> list = this.shareActivityExamDao.findByList("selectOrderById", paramMap);
		if(CollectionUtils.isEmpty(list)){
			ShareActivityExam bean = new ShareActivityExam();
			bean.setExamVersion(this.getExamVersion());
			bean.setStartDateTime(new Date());
			bean.setCandidateKey(teacherId+"");
			bean.setTeacherId(teacherId);
			bean.setCandidateIp(candidateIp);
			bean.setLinkSourceId(linkSourceId);
			bean.setShareRecordId(0L);
			bean.setStatus(0);
			this.shareActivityExamDao.insertSelective(bean);
			
			String questionId = getExamPageContentForIndex(this.getExamVersion(),index);
			ShareExamDetail shareExamDetail = new ShareExamDetail();
			shareExamDetail.setActivityExamId(bean.getId());
			shareExamDetail.setQuestionId(questionId);
			shareExamDetail.setQuestionIndex(1L);
			shareExamDetail.setStartDateTime(new Date());
			shareExamDetail.setStatus(0);
			this.shareExamDetailDao.insertSelective(shareExamDetail);
			StartHandleVo beanVo = new StartHandleVo();
			beanVo.setActivityExamId(bean.getId());
			beanVo.setCandidateKey(bean.getCandidateKey());
			beanVo.setQuestionId(questionId);
			beanVo.setQuestionIndex(index);
			beanVo.setRefereeId(teacherId);
			return beanVo;
		}else{
			return null;
		}
	}
	
	/**
	 * URL 参数验证
	 * 1.验证通过 如果存在
	 * @param bean
	 * @return
	 */
	public CheckUrlVo checkUrl(ClickHandleDto bean){
		ShareLinkSource sLinkSource = this.shareLinkSourceDao.getById(bean.getLinkSourceId());
		CheckUrlVo beanVo = new CheckUrlVo();
		if(NumericUtils.isNotNull(sLinkSource)){
			if(NumericUtils.isNullOrZeor(bean.getShareRecordId())){
				//第一次点击
				beanVo.setExamVersion(this.getExamVersion());
				beanVo.setLevel(0L);
				beanVo.setPageContent(this.getExamContent(beanVo.getExamVersion()));
				return beanVo;
			}else{
				ShareRecord shareRecord = this.shareRecordDao.getById(bean.getShareRecordId());
				// 该分享ID 来源ID 匹配为正确
				if(NumericUtils.isNotNull(shareRecord)){
					beanVo.setExamVersion(shareRecord.getExamVersion());
					beanVo.setLevel(shareRecord.getShareLevel());
					beanVo.setPageContent(this.getExamContent(beanVo.getExamVersion()));
					return beanVo;
				}
			}
		}
		return null;
	}
	
	/**
	 * 1. 更新本题结果,如果没有答完成,则 插入下一道题的开始时间,
	 * 已经答题完成计算结果保存更新测试结束时间期间 插入时候需要
	 * 验证本次开始下一题是否存在,已经不在则不需要插入.
	 * @return
	 */
	public Map<String, Object> updateExamDetailResult(SubmitHandleDto bean){
		ShareActivityExam shareActivityExam = this.shareActivityExamDao.getById(bean.getActivityExamId());
		if(NumericUtils.isNull(shareActivityExam)){
			return ReturnMapUtils.returnFail(-2, "没有找到创建的测试记录，activityExamId:"+shareActivityExam.getId()+"不正确");
		}
		if(StatusEnum.COMPLETE.val() == shareActivityExam.getStatus()){
			return ReturnMapUtils.returnFail(-3, "测试已经结束，请重新开始，activityExamId:"+shareActivityExam.getId());
		}	
		
		ShareExamDetail selectExamDetail = new ShareExamDetail();
		selectExamDetail.setActivityExamId(bean.getActivityExamId());
		selectExamDetail.setQuestionId(bean.getQuestionId());
		List<ShareExamDetail> list = this.shareExamDetailDao.selectByList(selectExamDetail);
		if(CollectionUtils.isEmpty(list)){
			return ReturnMapUtils.returnFail(-4, "没有找到该题的考试信息，请从新提交，activityExamId:"+shareActivityExam.getId());
		}
		//考试不为空
		SubmitHandleVo beanVo = new SubmitHandleVo();
		//更新当前提交结果
		ShareExamDetail currentExamDetail = list.get(0);
		currentExamDetail.setQuestionResult(bean.getQuestionResult());
		currentExamDetail.setEndDateTime(new Date());
		currentExamDetail.setStatus(StatusEnum.COMPLETE.val());
		this.shareExamDetailDao.updateById(currentExamDetail);
		//下一道题的ID
		String questionId = getExamPageContentForIndex(shareActivityExam.getExamVersion(), bean.getQuestionIndex()+1);
		//更新本次考试结果，并返回结果
		if(StringUtils.isBlank(questionId)){
			 // 没有下一题 计算结果 返回前端
			 shareActivityExam = this.updateExamReturnResult(shareActivityExam, StatusEnum.COMPLETE);
		}else{
			 //有下一题,插入下一道题开始开始
			 shareActivityExam = this.updateExamReturnResult(shareActivityExam, StatusEnum.PENDING);
			 beanVo.setQuestionId(questionId);
			 beanVo.setQuestionIndex(bean.getQuestionIndex()+1);
			 
			 selectExamDetail.setQuestionId(questionId);
			 List<ShareExamDetail> nextList = this.shareExamDetailDao.selectByList(selectExamDetail);
			 if(CollectionUtils.isEmpty(nextList)){
				 ShareExamDetail nextExamDetail = new ShareExamDetail();
				 nextExamDetail.setActivityExamId(shareActivityExam.getId());
				 nextExamDetail.setQuestionId(questionId);
				 nextExamDetail.setQuestionIndex(bean.getQuestionIndex()+1L);
				 nextExamDetail.setStartDateTime(new Date());
				 nextExamDetail.setStatus(0); 
				 this.shareExamDetailDao.insertSelective(nextExamDetail);
			 }else{
				 logger.info("下一题已经存在不再插入");
			 }
		}
		//更新本次考试
		this.shareActivityExamDao.updateById(shareActivityExam);
		beanVo.setStatus(shareActivityExam.getStatus());
		beanVo.setExamResult(shareActivityExam.getExamResult());
		return ReturnMapUtils.returnSuccess(beanVo);
	}
	
	/**
	 * 更新考试结果
	 * @param activityExamId 考试ID
 	 * @param status 考试状态
	 * @return
	 */
	private ShareActivityExam updateExamReturnResult(ShareActivityExam shareActivityExam, StatusEnum status){
		shareActivityExam.setStatus(status.val());
		if(status.val() == StatusEnum.COMPLETE.val()){
			shareActivityExam.setEndDateTime(new Date());
		}
		ShareExamDetail selectBean = new ShareExamDetail();
		selectBean.setActivityExamId(shareActivityExam.getId());
		selectBean.setStatus(ShareActivityExamEnum.StatusEnum.COMPLETE.val());
		Map<String,Object> paramMap = MyBatisTools.toMap(selectBean);
		List<ShareExamDetail> list = this.shareExamDetailDao.findByList("selectOrderByIndex", paramMap);
		StringBuilder examResultString = new StringBuilder("");
		if(CollectionUtils.isNotEmpty(list)){
			for (ShareExamDetail beanDetail:list) {
				if(StringUtils.isNotBlank(beanDetail.getQuestionResult())){
					examResultString.append(StringUtils.trim(beanDetail.getQuestionResult()));
				}else {
					examResultString.append("-");
				}
			}
		}
		shareActivityExam.setExamResult(examResultString.toString());
		return shareActivityExam;
	}
	
	/**
	 * 检查老师测试状态
	 * 1.如果有pending中的获取pending的测试
	 * 2.如果没有pending的，获取最后一次完成的考试
	 * 3.如果没有考试过进入开始考试页面
	 * @param teacher 
	 * @return
	 */
	public ShareActivityExam checkTeacherExamStratus(Teacher teacher){
		ShareActivityExam selectBean = new ShareActivityExam();
		selectBean.setTeacherId(teacher.getId());
		Map<String, Object> paramMap = MyBatisTools.toMap(selectBean);
		List<ShareActivityExam> list = this.shareActivityExamDao.findByList("selectOrderById", paramMap);
		//有考试过
		if(CollectionUtils.isNotEmpty(list)){ 
			List<ShareActivityExam> waitList = list.stream().filter(bean -> bean.getStatus() == StatusEnum.PENDING.val()).collect(Collectors.toList());
			logger.info("waitList:"+waitList.size());
			//有待考记录，返回未完成的考试
			if(CollectionUtils.isNotEmpty(waitList)){
				return waitList.get(waitList.size()-1);
			//全部完成则返回最后一条考试记录
			}else{
				return list.get(list.size()-1);
			}
		}
		return null;
	}
	
	
	public ShareExamDetail findPendingByExamId(Long activityExamId){
		ShareExamDetail selectBean = new ShareExamDetail();
		selectBean.setActivityExamId(activityExamId);
		selectBean.setStatus(StatusEnum.PENDING.val());
		List<ShareExamDetail> list = this.shareExamDetailDao.selectByList(selectBean);
		if(CollectionUtils.isEmpty(list)){
			logger.error("未找到未完成的测试试题");
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 获取考试最新版本
	 * @return
	 */
	public String getExamVersion(){
		//这里的配置不能缓存 及时获取
    	String contentJson = TemplateUtils.readTemplatePath("data"+File.separator +"share"+File.separator +"exam-version.json").toString();
    	logger.info("读取到：" + contentJson);
    	if(StringUtils.isBlank(contentJson)){
    		logger.error("data/share/exam-version.json,没有读取到内容。");
    		return contentJson;
    	}
    	JSONObject json = JSONObject.parseObject(contentJson);
    	//获取考试最新版本
    	String examVersion = json.getString("version");
    	logger.info("解析到最新版本：" + examVersion);
    	return StringUtils.trim(examVersion);
	}	
	
	/**
	 * 获取考试内容
	 * @param versionName
	 * @return
	 */
	public JSONObject getExamContent(String versionName){
		// 优先从缓存中读取
		String contentJson = redisProxy.get(RedisConstants.TRPM_SHARE_KEY + versionName);
		// 如果没有，则从文件中读取
		if(StringUtils.isBlank(contentJson)){
			contentJson = TemplateUtils.readTemplatePath("data"+File.separator +"share" + File.separator + versionName).toString();
			logger.info("读取到：" + contentJson);
			if(StringUtils.isNotBlank(contentJson)){
				redisProxy.set(RedisConstants.TRPM_SHARE_KEY + versionName, contentJson, RedisConstants.TRPM_SHARE_TIME);
			}else{
				logger.error("data/share/"+versionName+",没有读取到内容。");
			}
		}
		JSONObject json = JSONObject.parseObject(contentJson);
		return json;
	}
	
	/**
	 * 获取指定题目的题ID
	 * @param index
	 * @return
	 */
	public String getExamPageContentForIndex(String versionName, int index){
		JSONObject json = this.getExamContent(versionName);
		String pageContent = json.getString("pageContent");
		JSONArray jsons = JSONArray.parseArray(pageContent);
		logger.info("jsons:" + jsons.size() + "; index:" + index);
		if(jsons.size() >= index){
			JSONObject jsonObject = (JSONObject) jsons.get(index-1);
			return jsonObject.getString("id");
		}
		return null;
	}
	
}
