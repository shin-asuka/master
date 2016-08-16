package com.vipkid.trpm.service.portal;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.trpm.entity.TeacherLocation;

@Service
public class LocationService {

	private Logger logger = LoggerFactory.getLogger(LocationService.class);

	@Resource
	private com.vipkid.trpm.dao.TeacherLocationDao TeacherLocationDao;

	public List<TeacherLocation> getLocationList(int parentId, int level) {
		logger.info("getLocationList by parent id:{}", parentId);

		List<TeacherLocation> locations = TeacherLocationDao.findByParentId(parentId, level);
		return locations;
	}

}
