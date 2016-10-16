package com.vipkid.trpm.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;
import com.vipkid.trpm.entity.TeacherTaxpayerFormDetail;

@Repository
public class TeacherTaxpayerFormDetailDao extends MapperDaoTemplate<TeacherTaxpayerFormDetail> {

	@Autowired
	public TeacherTaxpayerFormDetailDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, TeacherTaxpayerFormDetail.class);
	}
	public TeacherTaxpayerFormDetail findById(Long id) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("id", id);
		return selectOne("findById", paramsMap);
	}
	
	public List<TeacherTaxpayerFormDetail> findList(TeacherTaxpayerFormDetail taxpayerFormDetail){
		
		Map<String, Object> paramsMap = Maps.newHashMap();
		
		return selectList("findList", paramsMap);
	}
	
	public int insert(TeacherTaxpayerFormDetail taxpayerFormDetail){
		if(taxpayerFormDetail.getDelFlag()==null){
			taxpayerFormDetail.setDelFlag(TeacherTaxpayerFormDetail.DEL_FLAG_NORMAL);
		}
		if(taxpayerFormDetail.getCreateBy() == null){
			taxpayerFormDetail.setCreateBy(0L);
		}
		if(taxpayerFormDetail.getCreateTime()==null){
			taxpayerFormDetail.setCreateTime(new Date());
		}
		return super.save(taxpayerFormDetail);
	}

	public int update(TeacherTaxpayerFormDetail taxpayerFormDetail){
		if(taxpayerFormDetail.getDelFlag()==null){
			taxpayerFormDetail.setDelFlag(TeacherTaxpayerFormDetail.DEL_FLAG_NORMAL);
		}
		if(taxpayerFormDetail.getCreateBy() == null){
			taxpayerFormDetail.setCreateBy(0L);
		}
		if(taxpayerFormDetail.getCreateTime()==null){
			taxpayerFormDetail.setCreateTime(new Date());
		}
		return super.update(taxpayerFormDetail);
	}
	public List<TeacherTaxpayerFormDetail> findListByIds(List<Long> idList) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("DEL_FLAG_NORMAL", TeacherTaxpayerFormDetail.DEL_FLAG_NORMAL);
		paramsMap.put("idList", idList);
		List<TeacherTaxpayerFormDetail> list = selectList("findListByIds", paramsMap);
		return list;
	}
}
