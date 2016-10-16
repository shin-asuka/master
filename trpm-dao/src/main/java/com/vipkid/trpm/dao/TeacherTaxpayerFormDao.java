package com.vipkid.trpm.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.community.dao.support.Query;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.Page;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;
import com.vipkid.trpm.entity.TeacherTaxpayerFormDetail;
import com.vipkid.trpm.entity.TeacherTaxpayerView;

@Repository
public class TeacherTaxpayerFormDao extends MapperDaoTemplate<TeacherTaxpayerForm> {

	@Autowired
	public TeacherTaxpayerFormDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, TeacherTaxpayerForm.class);
	}
	public TeacherTaxpayerForm findById(Long id) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("id", id);
		paramsMap.put("DEL_FLAG_NORMAL", TeacherTaxpayerFormDetail.DEL_FLAG_NORMAL);
		return selectOne("findById", paramsMap);
	}
	
	public TeacherTaxpayerForm findByTeacherIdAndType(Long teacherId,Integer formType) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("teacherId", teacherId);
		paramsMap.put("formType", formType);
		paramsMap.put("DEL_FLAG_NORMAL", TeacherTaxpayerFormDetail.DEL_FLAG_NORMAL);
		return selectOne("findByTeacherIdAndType", paramsMap);
	}
	

	public List<TeacherTaxpayerForm> findList(TeacherTaxpayerForm taxpayerForm){
		Map<String, Object> paramsMap = Maps.newHashMap();
		//paramsMap.put("id", id);
		paramsMap.put("DEL_FLAG_NORMAL", TeacherTaxpayerFormDetail.DEL_FLAG_NORMAL);
		paramsMap.put("param", taxpayerForm);
		List<TeacherTaxpayerForm> list = selectList("findList", paramsMap);
		return list;
	}
	
	public List<TeacherTaxpayerForm> findListByIds(List<Long> idList){
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("DEL_FLAG_NORMAL", TeacherTaxpayerFormDetail.DEL_FLAG_NORMAL);
		paramsMap.put("idList", idList);
		List<TeacherTaxpayerForm> list = selectList("findListByIds", paramsMap);
		return list;
	}
	
	public List<TeacherTaxpayerView> findTaxpayerViewList(Page<TeacherTaxpayerView> page,TeacherTaxpayerView teacherTaxpayerView){
		logger.info("查询 TaxpayerView");
		Map<String, Object> paramsMap = Maps.newHashMap();
		//paramsMap.put("id", id);
		
		paramsMap.put("param", teacherTaxpayerView);
		paramsMap.put("DEL_FLAG_NORMAL", TeacherTaxpayerFormDetail.DEL_FLAG_NORMAL);
		
		paramsMap.put("page", page);
		List<TeacherTaxpayerView> list = getSqlSession().selectList("findTaxpayerViewList", paramsMap);
		return list;
	}
	
	public Integer getTaxpayerViewCount(TeacherTaxpayerView teacherTaxpayerView){
		Map<String, Object> paramsMap = Maps.newHashMap();
		//paramsMap.put("id", id);
		paramsMap.put("DEL_FLAG_NORMAL", TeacherTaxpayerFormDetail.DEL_FLAG_NORMAL);
		paramsMap.put("param", teacherTaxpayerView);
		Integer count = selectCount("getTaxpayerViewCount", paramsMap);
		return count;
	}
	
	public Integer getCount(TeacherTaxpayerForm taxpayerForm){
		Map<String, Object> paramsMap = Maps.newHashMap();
		//paramsMap.put("id", id);
		paramsMap.put("DEL_FLAG_NORMAL", TeacherTaxpayerFormDetail.DEL_FLAG_NORMAL);
		Integer count = selectCount("getCount", paramsMap);
		return count;
	}
	
	public int insert(TeacherTaxpayerForm taxpayerForm){
		if(taxpayerForm.getDelFlag()==null){
			taxpayerForm.setDelFlag(TeacherTaxpayerFormDetail.DEL_FLAG_NORMAL);
		}
		if(taxpayerForm.getCreateBy() == null){
			taxpayerForm.setCreateBy(0L);
		}
		if(taxpayerForm.getCreateTime()==null){
			taxpayerForm.setCreateTime(new Date());
		}
		if(taxpayerForm.getUpdateBy() == null){
			taxpayerForm.setUpdateBy(0L);
		}
		if(taxpayerForm.getUpdateTime() == null){
			taxpayerForm.setUpdateTime(new Date());
		}
		return super.save(taxpayerForm);
	}
	
	public int update(TeacherTaxpayerForm taxpayerForm){
		if(taxpayerForm.getDelFlag()==null){
			taxpayerForm.setDelFlag(TeacherTaxpayerFormDetail.DEL_FLAG_NORMAL);
		}
		if(taxpayerForm.getCreateBy() == null){
			taxpayerForm.setCreateBy(0L);
		}
		if(taxpayerForm.getCreateTime()==null){
			taxpayerForm.setCreateTime(new Date());
		}
		if(taxpayerForm.getUpdateBy() == null){
			taxpayerForm.setUpdateBy(0L);
		}
		if(taxpayerForm.getUpdateTime() == null){
			taxpayerForm.setUpdateTime(new Date());
		}
		return super.update(taxpayerForm);
	}
	
	
}
