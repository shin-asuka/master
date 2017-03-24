package com.vipkid.portal.activity.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.vipkid.teacher.tools.utils.NumericUtils;
import com.vipkid.teacher.tools.utils.ReturnMapUtils;
import com.vipkid.trpm.dao.ShareActivityExamDao;
import com.vipkid.trpm.dao.ShareLinkSourceDao;
import com.vipkid.trpm.dao.ShareRecordDao;
import com.vipkid.trpm.entity.ShareActivityExam;
import com.vipkid.trpm.entity.ShareLinkSource;
import com.vipkid.trpm.entity.ShareRecord;

@Service
public class ReferralActivityService {
	
	@Autowired
	private ShareLinkSourceDao shareLinkSourceDao;
	
	@Autowired
	private ShareRecordDao shareRecordDao;
	
	@Autowired
	private ShareActivityExamDao shareActivityExamDao;
	
	/**
	 * link入口被单击次数的更新
	 * @param linkSourceId
	 */
	public void updateLinkSourceClick(Integer linkSourceId){
		if(NumericUtils.isNotNullOrZeor(linkSourceId)){
			ShareLinkSource bean = this.shareLinkSourceDao.getById(linkSourceId);
			bean.setLinkClick(bean.getLinkClick()+1);
			this.shareLinkSourceDao.updateById(bean);
		}
	}
	
	/**
	 * 通过分享ID 更新分享链接被单击次数
	 * @param linkSourceId
	 */
	public void updateShareRecordClick(Integer linkSourceId,Integer shareRecordId){
		if(NumericUtils.isNotNullOrZeor(linkSourceId)){
			ShareRecord bean = this.shareRecordDao.getById(shareRecordId);
			bean.setCountClick(bean.getCountClick()+1);
			this.shareRecordDao.updateById(bean);
		}
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
	public Map<String,Object> updateTeacherShare(Integer candidateKey,String candidateIp){
		
		
		return ReturnMapUtils.returnSuccess();
	}
	
}
