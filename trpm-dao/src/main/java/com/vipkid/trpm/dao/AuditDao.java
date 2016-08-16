package com.vipkid.trpm.dao;

import java.sql.Timestamp;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vipkid.trpm.entity.Audit;

@Repository
public class AuditDao extends MapperDaoTemplate<Audit> {
	
	private static final Logger logger = LoggerFactory.getLogger(AuditDao.class);

	@Autowired
	public AuditDao(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, Audit.class);
	}

	/**
	 * 新增操作日志 <br/>
	 * 
	 * @Author:ALong <br/>
	 * @param category
	 *            模块简称 <br/>
	 * @param level
	 *            日志级别 <br/>
	 * @param operation
	 *            操内容<br/>
	 * @param operator
	 *            操作者<br/>
	 * @param o
	 *            操作对象的数据
	 * @return int<br/>
	 * @date 2015年12月22日<br/>
	 */
	public int saveAudit(String category, String level, String operation, String operator, Object o,String IP) {
		logger.info("Save audit: category:{}, level:{}, operation:{}, operator:{}, IPAddr:{}",category,level,operation,operator,IP);
		Audit audit = new Audit();
		audit.setCategory(category);
		audit.setExecuteDateTime(new Timestamp(System.currentTimeMillis()));
		audit.setLevel(level);
		audit.setOperation(operation + " IPAddr:" +IP);
		audit.setOperator(operator);
		return super.save(audit);
	}
}
