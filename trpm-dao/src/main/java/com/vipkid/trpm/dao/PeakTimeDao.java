package com.vipkid.trpm.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.PeakTime;

@Repository
public class PeakTimeDao extends MapperDaoTemplate<PeakTime> {

	@Autowired
	public PeakTimeDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, PeakTime.class);
	}

	/**
	 * 按开始时间和结束时间查询PeakTime列表
	 * 
	 * @param fromTime
	 * @param toTime
	 * @return List<PeakTime>
	 */
	public List<PeakTime> findByFromAndToTime(Date fromTime, Date toTime) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("fromTime", fromTime);
		paramsMap.put("toTime", toTime);

		return selectList("findByFromAndToTime", paramsMap);
	}

	/**
	 * 通过时间点查询PeakTime
	 * 
	 * @param timePoint
	 * @return PeakTime
	 */
	public PeakTime findByTimePoint(Timestamp timePoint) {
		return selectOne(new PeakTime().setTimePoint(timePoint));
	}

	/**
	 * 通过老师ID，开始时间和结束时间查询数量
	 * 
	 * @param fromTime
	 * @param toTime
	 * @param teacherId
	 * @return int
	 */
	public int countDaoByTeacherIdAndFromWithToTime(Date fromTime, Date toTime, long teacherId) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("fromTime", fromTime);
		paramsMap.put("toTime", toTime);
		paramsMap.put("teacherId", teacherId);

		return selectCount("countDaoByTeacherIdAndFromWithToTime", paramsMap);
	}

}
