package com.vipkid.portal.activity.service;

import java.nio.charset.StandardCharsets;
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
import com.google.common.collect.Maps;
import com.vipkid.teacher.tools.utils.NumericUtils;
import com.vipkid.teacher.tools.utils.ReturnMapUtils;
import com.vipkid.trpm.constant.ApplicationConstant.RedisConstants;
import com.vipkid.trpm.dao.ShareActivityExamDao;
import com.vipkid.trpm.dao.ShareLinkSourceDao;
import com.vipkid.trpm.dao.ShareRecordDao;
import com.vipkid.trpm.entity.ShareActivityExam;
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
	
}
