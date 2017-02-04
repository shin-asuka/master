/**
 * 
 */
package com.vipkid.trpm.service.portal;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vipkid.cache.service.TeacherLockService;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.FormType;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.rest.exception.ServiceException;
import com.vipkid.trpm.dao.TeacherTaxpayerFormDao;
import com.vipkid.trpm.dao.TeacherTaxpayerFormDetailDao;
import com.vipkid.trpm.entity.Page;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;
import com.vipkid.trpm.entity.TeacherTaxpayerFormDetail;
import com.vipkid.trpm.entity.TeacherTaxpayerView;
import com.vipkid.trpm.entity.personal.TaxpayerView;

/**
 * @author zouqinghua
 * @date 2016年10月14日  下午5:38:12
 *
 */
@Service
public class TeacherTaxpayerFormService {

	private Logger logger = LoggerFactory.getLogger(TeacherTaxpayerFormService.class);
	
	@Autowired
    private TeacherTaxpayerFormDao teacherTaxpayerFormDao;
	
	@Autowired
    private TeacherTaxpayerFormDetailDao teacherTaxpayerFormDetailDao;
	
	@Autowired
    private TeacherLockService teacherLockService;
	
	public List<TeacherTaxpayerForm> findListByIds(List<Long> idList){
		logger.info("查询TeacherTaxpayerForm  findListByIds idList = {}",idList);
		List<TeacherTaxpayerForm> list = teacherTaxpayerFormDao.findListByIds(idList);
		
		return list;
	}
	
	public List<TeacherTaxpayerFormDetail> findDetailListByIds(List<Long> idList){
		logger.info("查询TeacherTaxpayerFormDetail  findListByIds idList = {}",idList);
		List<TeacherTaxpayerFormDetail> list = teacherTaxpayerFormDetailDao.findListByIds(idList);
		
		return list;
	}
	
	public Page<TeacherTaxpayerView> findPage(Page<TeacherTaxpayerView> page, TeacherTaxpayerView teacherTaxpayerView){
		logger.info("查询TeacherTaxpayerView = {} ,page = {}" , JsonUtils.toJSONString(teacherTaxpayerView),JsonUtils.toJSONString(page));
		Integer count = teacherTaxpayerFormDao.getTaxpayerViewCount(teacherTaxpayerView);
		page.setCount(count);
		if(count > 0){
			List<TeacherTaxpayerView> list = teacherTaxpayerFormDao.findTaxpayerViewList(page,teacherTaxpayerView);
			page.setList(list);
		}
		
		return page;
	}
		
	public TeacherTaxpayerForm getTeacherTaxpayerForm(Long teacherId,Integer formType){
		 
		TeacherTaxpayerForm teacherTaxpayerForm = teacherTaxpayerFormDao.findByTeacherIdAndType(teacherId, formType);
		
		return teacherTaxpayerForm;
	}
	
	public TaxpayerView getTeacherTaxpayerView(Long teacherId){ 
		TaxpayerView view = new TaxpayerView();
		TeacherTaxpayerForm taxpayerForm = new TeacherTaxpayerForm();
		taxpayerForm.setTeacherId(teacherId);
		List<TeacherTaxpayerForm> list = teacherTaxpayerFormDao.findList(taxpayerForm );
		
		for (TeacherTaxpayerForm teacherTaxpayerForm : list) {
			if(TeacherEnum.FormType.W9.val().equals(teacherTaxpayerForm.getFormType())){
				view.setFormW9(teacherTaxpayerForm);
			}else if(TeacherEnum.FormType.T4A.val().equals(teacherTaxpayerForm.getFormType())){
				view.setFormT4A(teacherTaxpayerForm);
			}
		}
		
		return view;
	}
	
	public void saveTeacherTaxpayerForm(TeacherTaxpayerForm teacherTaxpayerForm){
		logger.info("保存TeacherTaxpayerForm = {}",JsonUtils.toJSONString(teacherTaxpayerForm));
		Long teacherTaxpayerFormId = null;
		Long teacherId = teacherTaxpayerForm.getTeacherId();
		Integer formType = teacherTaxpayerForm.getFormType();
		FormType formTypeEnum = TeacherEnum.getFormTypeById(formType);
		
		
		Boolean isLock = teacherLockService.getTaxpayerLock(teacherId, formType);
		if(isLock){
			try {
				teacherTaxpayerFormId = teacherTaxpayerForm.getId();
				TeacherTaxpayerForm teacherTaxpayerFormOld = null;
				if(teacherTaxpayerFormId == null){
					teacherTaxpayerFormOld = teacherTaxpayerFormDao.findByTeacherIdAndType(teacherId, formType);
				}else{
					teacherTaxpayerFormOld = teacherTaxpayerFormDao.findById(teacherTaxpayerFormId);
				}
				if(teacherTaxpayerFormOld!=null){
					teacherTaxpayerForm.setId(teacherTaxpayerFormOld.getId());
					teacherTaxpayerForm.setCreateTime(teacherTaxpayerFormOld.getCreateTime());
					teacherTaxpayerForm.setCreateBy(teacherTaxpayerFormOld.getCreateBy());
				}
				if(teacherTaxpayerForm.getId()!=null){
					teacherTaxpayerFormDao.update(teacherTaxpayerForm);
				}else{
					teacherTaxpayerFormDao.insert(teacherTaxpayerForm);
				}
				
				teacherTaxpayerFormId = teacherTaxpayerForm.getId();
				TeacherTaxpayerFormDetail taxpayerFormDetail = teacherTaxpayerForm.getTeacherTaxpayerFormDetail();
				if(taxpayerFormDetail!=null){ //创建上传记录
					taxpayerFormDetail.setTaxpayerFormId(teacherTaxpayerFormId);
					taxpayerFormDetail.setFormName(formTypeEnum.name());
					taxpayerFormDetail.setFormType(formType);
					taxpayerFormDetail.setTeacherId(teacherTaxpayerForm.getTeacherId());
					taxpayerFormDetail.setUploader(teacherTaxpayerForm.getUploader());
					taxpayerFormDetail.setCreateBy(teacherTaxpayerForm.getUploader());
					taxpayerFormDetail.setUrl(teacherTaxpayerForm.getUrl());
					taxpayerFormDetail.setIsNew(teacherTaxpayerForm.getIsNew());
					if(taxpayerFormDetail.getId()==null){
						teacherTaxpayerFormDetailDao.insert(taxpayerFormDetail);
					}
					Long taxpayerFormDetailId = taxpayerFormDetail.getId();
					if(taxpayerFormDetailId!=null){ //更新最新文件记录Id
						teacherTaxpayerForm.setTaxpayerFormDetailId(taxpayerFormDetailId);
						teacherTaxpayerFormDao.update(teacherTaxpayerForm);
					}
				}
			} catch (Exception e) {
				logger.error("保存文件转换记录失败",e);
				throw new ServiceException("保存文件转换记录失败");
			}finally{
				teacherLockService.releaseTaxpayerLock(teacherId, formType);
			}
		}else{
			logger.info("获取分布式锁失败  getLockFail teacherId = {}, formType = {}",teacherId,formType);
			throw new ServiceException("获取分布式锁失败");
		}
		
	}
	
	public void updateTaxpayerFormStatus(Long teacherId ,Integer formType ,Integer isNew){
		logger.info("更新TaxpayerForm状态 teacherId = {},formType = {}, isNew = {}",teacherId,formType,isNew);
		TeacherTaxpayerForm teacherTaxpayerForm= teacherTaxpayerFormDao.findByTeacherIdAndType(teacherId, formType);
		logger.info("更新TaxpayerForm状态  teacherTaxpayerForm = {}",JsonUtils.toJSONString(teacherTaxpayerForm));
		if(teacherTaxpayerForm!=null){
			teacherTaxpayerForm.setIsNew(isNew);
			teacherTaxpayerFormDao.update(teacherTaxpayerForm);
		}
	}
	
	public void updateTaxpayerFormStatus(Long taxpayerFormId,Integer isNew){
		logger.info("更新TaxpayerForm状态 taxpayerFormId = {}, isNew = {}",taxpayerFormId,isNew);
		TeacherTaxpayerForm teacherTaxpayerForm= teacherTaxpayerFormDao.findById(taxpayerFormId);
		logger.info("更新TaxpayerForm状态  teacherTaxpayerForm = {}",JsonUtils.toJSONString(teacherTaxpayerForm));
		if(teacherTaxpayerForm!=null){
			teacherTaxpayerForm.setIsNew(isNew);
			teacherTaxpayerFormDao.update(teacherTaxpayerForm);
		}
	}
	
	public void updateTaxpayerStatus(Long taxpayerFormId,Long taxpayerDetailId,Integer isNew){
		logger.info("更新TaxpayerForm状态 taxpayerFormId = {}, isNew = {}",taxpayerFormId,isNew);
		TeacherTaxpayerForm teacherTaxpayerForm= teacherTaxpayerFormDao.findById(taxpayerFormId);
		if(teacherTaxpayerForm!=null && !isNew.equals(teacherTaxpayerForm.getIsNew())){
			logger.info("更新TaxpayerForm状态  teacherTaxpayerForm = {}",JsonUtils.toJSONString(teacherTaxpayerForm));
			teacherTaxpayerForm.setIsNew(isNew);
			teacherTaxpayerFormDao.update(teacherTaxpayerForm);
		}
		logger.info("更新TaxpayerForm状态 taxpayerDetailId = {}, isNew = {}",taxpayerDetailId,isNew);
		TeacherTaxpayerFormDetail detail = teacherTaxpayerFormDetailDao.findById(taxpayerDetailId);
		if(detail!=null &&  !isNew.equals(detail.getIsNew())){
			logger.info("更新TaxpayerForm状态  taxpayerDetail = {}",JsonUtils.toJSONString(teacherTaxpayerForm));
			detail.setIsNew(isNew);
			teacherTaxpayerFormDetailDao.update(detail);
		}
	}
}
