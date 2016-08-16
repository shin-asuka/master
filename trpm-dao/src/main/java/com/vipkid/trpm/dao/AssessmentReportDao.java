package com.vipkid.trpm.dao;

import static com.google.common.base.Preconditions.checkArgument;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.community.tools.JsonTools;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.trpm.entity.AssessmentReport;

@Repository
public class AssessmentReportDao extends MapperDaoTemplate<AssessmentReport> {
	
	private static final Logger logger = LoggerFactory.getLogger(AssessmentReportDao.class);

	@Autowired
	public AssessmentReportDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, AssessmentReport.class);
	}

	/**
	 * 判断UAReport是否存在，存在则返回本身，不存在则返回null<br/>
	 * @param  report
	 * @return AssessmentReport
	 * @date 2015年12月10日
	 */
	public AssessmentReport findReportByStudentIdAndName(String name,long studentId) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("name", name);
		paramsMap.put("studentId",studentId);
		logger.info(" select a assesmentReport for studentId:{} And name:{}",studentId,name);
		return  super.selectOne("findReportByStudentIdAndName", paramsMap);
	}
	
	/**
	 * 按照onlineClassId查询Report
	 * @param onlineClassId
	 * @return    
	 * AssessmentReport
	 * @date 2016年1月21日
	 */
	public AssessmentReport findReportByClassId(long onlineClassId){
		return super.selectOne(new AssessmentReport().setOnlineClassId(onlineClassId));
	}

	/**
	 * 更新Report<br/>
	 * @Author:ALong
	 * @param report
	 * @return int 
	 * @date 2015年12月10日
	 */
	public int update(AssessmentReport report) {
		checkArgument(0 != report.getId(), "Argument reportId equals 0");
		logger.info(" update The assesmentReport for id:{},content:{}",report.getId(),JsonTools.getJson(report));
		//更新数据更新时间
		report.setUpdateDateTime(new Timestamp(System.currentTimeMillis()));
		return super.update(report);
	}

	/**
	 * 保存Report<br/>
	 * @Author:ALong
	 * @param: report
	 * @return: int 
	 * @date 2015年12月10日
	 */
	public int save(AssessmentReport report) {
		logger.info(" insert The assesmentReport for id:{},content:{}",report.getId(),JsonTools.getJson(report));
		
		//插入数据设置创建时间和更新时间
		report.setCreateDateTime(new Timestamp(System.currentTimeMillis()));
		report.setUpdateDateTime(report.getCreateDateTime());
		return super.save(report);
	}

}
