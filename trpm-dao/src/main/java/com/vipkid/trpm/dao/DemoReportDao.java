package com.vipkid.trpm.dao;

import org.community.dao.support.MapperDaoTemplate;
import org.community.tools.JsonTools;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.trpm.entity.DemoReport;

@Repository
public class DemoReportDao extends MapperDaoTemplate<DemoReport> {
	
	private static final Logger logger = LoggerFactory.getLogger(DemoReportDao.class);

	@Autowired
	public DemoReportDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, DemoReport.class);
	}

	/**
	 * 通过studentId和onlineClassId查询<br/>
	 * 
	 * @param studentId
	 * @param onlineClassId
	 * @return DemoReport
	 */
	public DemoReport findByStudentIdAndOnlineClassId(long studentId, long onlineClassId) {
		logger.info(" select One DemoReportDao for studentId:{} And classId:{}",studentId,onlineClassId);
		return selectOne(new DemoReport().setStudentId(studentId).setOnlineClassId(onlineClassId));
	}
	
	/**
	 * 通过Id主键查询<br/>
	 * @Author:ALong
	 * @param id
	 * @return DemoReport
	 * @date 2015年12月14日
	 */
	public DemoReport findById(long id){
		logger.info(" select One DemoReportDao for id:{}",id);
		return selectOne(new DemoReport().setId(id));
	} 
	
	/**
	 * 更新DemoReport <br/>
	 * @Author:ALong
	 * @param report
	 * @return int
	 * @date 2015年12月14日
	 */
	public int updateDemoReport(DemoReport report){
		logger.info(" update the DemoReportDao for id:{},content:{}",report.getId(),JsonTools.getJson(report));
		return super.update(report);
	}

}
