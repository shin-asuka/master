package com.vipkid.portal.activity.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.vipkid.dataSource.annotation.Slave;
import com.vipkid.file.utils.DateUtils;
import com.vipkid.portal.activity.vo.DrawListVo;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.User;

@Service
public class PrizeHandleService {
	
	private final static Logger logger = LoggerFactory.getLogger(PrizeHandleService.class);	

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private OnlineClassDao onlineClassDao;
	
	/**
	 * 课程限制开始时间 2017-04-01 00:00:00
	 */
	public final static Date CLASS_START_DATE = DateUtils.parseDate("2017-04-01 00:00:00");
	
	/**
	 * 
	 * @return
	 */
	@Slave
	public List<DrawListVo> findTeacherName(JSONObject reslultJson){
		List<DrawListVo> list = JsonTools.readValue(reslultJson.getString("data"), new TypeReference<List<DrawListVo>>(){});
		List<DrawListVo> listBak = new ArrayList<DrawListVo>();
		if(CollectionUtils.isNotEmpty(list)){
			for (DrawListVo bean : list) {
				if(StringUtils.equalsIgnoreCase(bean.getName(),"name")){
					User user = this.userDao.findById(bean.getTeacherId());
					bean.setName(user.getName());
				}
				listBak.add(bean);
			}
		}
		return listBak;
	}
	
	/**
	 * 检查课程时间是不是在活动时间内上的 如果是则可以进行分享并获得抽奖卷
	 * @param onlineClassId
	 * @return
	 */
	public boolean checkOnlineClass(Long onlineClassId) {
    	OnlineClass onlineClass = onlineClassDao.findById(onlineClassId);
    	if(onlineClass == null){
    		logger.warn("该课程ID不存在:" + onlineClassId);
    		return false;
    	}
		Date date = new Date(onlineClass.getScheduledDateTime().getTime());
    	if(date.after(CLASS_START_DATE)){
    		return true;
    	}
    	return false;
	}
	
}
