package com.vipkid.trpm.service.portal;

import java.util.List;

import javax.annotation.Resource;

import com.google.api.client.util.Lists;
import org.apache.commons.collections.CollectionUtils;
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
		logger.info("getLocationList by parent id = {}, level = {}", parentId, level );

		List<TeacherLocation> locations = TeacherLocationDao.findByParentId(parentId, level);
		if (CollectionUtils.isNotEmpty(locations)) {
			StringBuffer sb = new StringBuffer();
			for (TeacherLocation one : locations) {
				sb.append("locationId=");
				sb.append(one.getId());
				sb.append(",");
				sb.append("name=");
				sb.append(one.getName());
				sb.append(";");
			}
			logger.info("teacherLocationDao#findByParentId:" + sb.toString());
		} else {
			locations = Lists.newArrayList();
			logger.info("teacherLocationDao#findByParentId: none");
		}
		
		return locations;
	}

}
