package com.vipkid.trpm.service.portal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.entity.TeacherAddress;

@Service
public class TeacherAddressService {

	private Logger logger = LoggerFactory.getLogger("TeacherAddressService");

	@Resource
	private TeacherAddressDao teacherAddressDao;

	/**
	 * 获取teacher address数据
	 * 
	 * @param teacherId
	 * @return
	 */
	public TeacherAddress findById(long id) {
		logger.info("findById with id: {}", id);
		if (0 == id) {
		    return null;
		}
		int nId = (int) id;
		return teacherAddressDao.findById(nId);
	}

	/**
	 * 更新teacherAddress操作
	 * 
	 * @param teacherAddress
	 */
	public int update(TeacherAddress teacherAddress) {
		return teacherAddressDao.updateOrSave(teacherAddress);
	}

}
