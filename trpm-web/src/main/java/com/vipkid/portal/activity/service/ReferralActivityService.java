package com.vipkid.portal.activity.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.vipkid.enums.ShareActivityExamEnum;
import com.vipkid.portal.activity.dto.ClickHandleDto;
import com.vipkid.portal.activity.dto.SubmitHandleDto;
import com.vipkid.portal.activity.vo.StartHandleVo;
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
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.util.FilesUtils;

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
	 * @return
	 */
	public Map<String,Object> updateCandidateShare(String candidateKey,String candidateIp){
		ShareActivityExam selectExam = new ShareActivityExam();
		selectExam.setCandidateKey(candidateKey);
		List<ShareActivityExam> examList = this.shareActivityExamDao.selectByList(selectExam);
		if(CollectionUtils.isEmpty(examList)){
			return ReturnMapUtils.returnFail(-2, "没有找到考试记录,非法的分享");
		}
		ShareActivityExam exam = examList.get(0);		
		if(NumericUtils.isNull(exam.getShareRecordId())){
			return ReturnMapUtils.returnFail(-3, "考试记录中没有分享ID,非法的分享");
		}
		ShareRecord preRecord = this.shareRecordDao.getById(exam.getShareRecordId());
		if(NumericUtils.isNull(preRecord)){
			return ReturnMapUtils.returnFail(-4, "考试记录中没有找到上层分享数据,非法的分享","分享ID:"+exam.getShareRecordId());
		}
		ShareRecord newRecord = new ShareRecord();
		newRecord.setCandidateKey(candidateKey);
		newRecord.setCandidateIp(candidateIp);
		newRecord.setCountClick(0L);
		newRecord.setExamVersion(preRecord.getExamVersion());
		newRecord.setLinkSourceId(preRecord.getLinkSourceId());
		newRecord.setTeacherId(preRecord.getTeacherId());
		newRecord.setShareLevel(preRecord.getShareLevel()+1);
		newRecord.setShareTime(new Date());
		this.shareRecordDao.insert(newRecord);
		Map<String,Object> resultMap = Maps.newHashMap();
		resultMap.put("shareRecordId", newRecord.getId());
		return ReturnMapUtils.returnSuccess(resultMap);
	}
	
	
	/**
	 * 老师的分享
	 * @param candidateKey
	 * @param candidateIp
	 * @return
	 */
	public Map<String,Object> updateTeacherShare(String candidateKey,String candidateIp,Long linkSourceId){
		ShareRecord newRecord = new ShareRecord();
		newRecord.setCandidateKey(candidateKey);
		newRecord.setCandidateIp(candidateIp);
		newRecord.setCountClick(0L);
		newRecord.setExamVersion(this.getExamVersion());
		newRecord.setLinkSourceId(linkSourceId);
		newRecord.setTeacherId(Long.valueOf(candidateKey));
		newRecord.setShareLevel(1L);
		newRecord.setShareTime(new Date());
		this.shareRecordDao.insert(newRecord);
		return ReturnMapUtils.returnSuccess();
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
		this.shareActivityExamDao.insert(bean);
		
		String questionId = getExamPageContentForIndex(this.getExamVersion(),index);
		ShareExamDetail shareExamDetail = new ShareExamDetail();
		shareExamDetail.setActivityExamId(bean.getId());
		shareExamDetail.setQuestionId(questionId);
		shareExamDetail.setQuestionIndex(1L);
		shareExamDetail.setStartDateTime(new Date());
		shareExamDetail.setStatus(0);
		this.shareExamDetailDao.insert(shareExamDetail);
		StartHandleVo beanVo = new StartHandleVo();
		beanVo.setActivityExamID(bean.getId());
		beanVo.setCandidateKey(bean.getCandidateKey());
		beanVo.setPageContent(this.getExamContent(bean.getExamVersion()));
		beanVo.setQuestionId(questionId);
		beanVo.setQuestionIndex(index);
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
		ShareActivityExam bean = new ShareActivityExam();
		bean.setExamVersion(this.getExamVersion());
		bean.setStartDateTime(new Date());
		bean.setCandidateKey(teacherId+"");
		bean.setTeacherId(teacherId);
		bean.setCandidateIp(candidateIp);
		bean.setLinkSourceId(linkSourceId);
		bean.setShareRecordId(0L);
		bean.setStatus(0);
		this.shareActivityExamDao.insert(bean);
		
		String questionId = getExamPageContentForIndex(this.getExamVersion(),index);
		ShareExamDetail shareExamDetail = new ShareExamDetail();
		shareExamDetail.setActivityExamId(bean.getId());
		shareExamDetail.setQuestionId(questionId);
		shareExamDetail.setQuestionIndex(1L);
		shareExamDetail.setStartDateTime(new Date());
		shareExamDetail.setStatus(0);
		this.shareExamDetailDao.insert(shareExamDetail);
		StartHandleVo beanVo = new StartHandleVo();
		beanVo.setActivityExamID(bean.getId());
		beanVo.setCandidateKey(bean.getCandidateKey());
		beanVo.setPageContent(this.getExamContent(bean.getExamVersion()));
		beanVo.setQuestionId(questionId);
		beanVo.setQuestionIndex(index);
		return beanVo;
	}
	
	/**
	 * URL 参数验证
	 * @param bean
	 * @return
	 */
	public boolean checkUrl(ClickHandleDto bean){
		ShareLinkSource sLinkSource = this.shareLinkSourceDao.getById(bean.getLinkSourceId());
		if(NumericUtils.isNotNull(sLinkSource)){
			if(NumericUtils.isNull(bean.getShareRecordId())){
				return true;
			}else{
				ShareRecord shareRecord = this.shareRecordDao.getById(bean.getShareRecordId());
				// 该分享ID 来源ID 匹配为正确
				if(NumericUtils.isNotNull(shareRecord) && shareRecord.getLinkSourceId() == bean.getLinkSourceId()){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 1. 更新本题结果,如果没有答完成,则 插入下一道题的开始时间,
	 * 已经答题完成计算结果保存更新测试结束时间期间 插入时候需要
	 * 验证本次开始下一题是否存在,已经不在则不需要插入.
	 * @return
	 */
	public Map<String, Object> updateExamResult(SubmitHandleDto bean){
		ShareExamDetail shareExamDetail = new ShareExamDetail();
		shareExamDetail.setActivityExamId(bean.getActivityExamId());
		shareExamDetail.setQuestionId(bean.getQuestionId());
		ShareActivityExam shareActivityExam = this.shareActivityExamDao.getById(bean.getActivityExamId());
		if(NumericUtils.isNull(shareActivityExam)){
			return ReturnMapUtils.returnFail(-2, "没有找到创建的测试记录，activityExamId:"+shareActivityExam.getId()+"不正确");
		}
		if(ShareActivityExamEnum.StatusEnum.COMPLETE.val() == shareActivityExam.getStatus()){
			return ReturnMapUtils.returnFail(-3, "测试已经结束，请重新开始activityExamId:"+shareActivityExam.getId());
		}
		
		List<ShareExamDetail> list = this.shareExamDetailDao.selectByList(shareExamDetail);
		if(CollectionUtils.isNotEmpty(list)){
			 String questionId = getExamPageContentForIndex(shareActivityExam.getExamVersion(), bean.getQuestionIndex()+1);
			 if(StringUtils.isBlank(questionId)){
				 // 没有下一题 计算结果 返回前段
				 
			 }else{
				 //有下一题，返回下一题ID
				 
			 }
		}
		return Maps.newHashMap();
	}
	
	/**
	 * 获取考试最新版本
	 * @return
	 */
	public String getExamVersion(){
		//这里的配置不能缓存 及时获取
    	String contentJson = FilesUtils.readContent(this.getClass().getResourceAsStream("data/share/exam-vrsion.json"),StandardCharsets.UTF_8);
    	logger.info("读取到：" + contentJson);
    	if(StringUtils.isBlank(contentJson)){
    		logger.error("data/share/exam-vrsion.json,没有读取到内容。");
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
	public String getExamContent(String versionName){
		// 优先从缓存中读取
		String contentJson = redisProxy.get(RedisConstants.TRPM_SHARE_KEY + versionName);
		// 如果没有，则从文件中读取
		if(StringUtils.isBlank(contentJson)){
			contentJson = FilesUtils.readContent(this.getClass().getResourceAsStream("data/share/" + versionName),StandardCharsets.UTF_8);
			logger.info("读取到：" + contentJson);
			if(StringUtils.isNotBlank(contentJson)){
				redisProxy.set(RedisConstants.TRPM_SHARE_KEY + versionName, contentJson, RedisConstants.TRPM_SHARE_TIME);
			}else{
				logger.error("data/share/"+versionName+",没有读取到内容。");
			}
		}
		return StringUtils.trim(contentJson);
	}
	
	/**
	 * 获取指定题目的题ID
	 * @param index
	 * @return
	 */
	public String getExamPageContentForIndex(String versionName, int index){
		String contentJson = this.getExamContent(versionName);
		if(StringUtils.isNotBlank(contentJson)){
			JSONObject json = JSONObject.parseObject(contentJson);
			String pageContent = json.getString("pageContent");
			JSONArray jsons = JSONArray.parseArray(pageContent);
			JSONObject jsonObject = (JSONObject) jsons.get(index-1);
			return jsonObject.getString("id");
		}
		return null;
	}
	
}
