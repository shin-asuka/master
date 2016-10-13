package com.vipkid.clickrate;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vipkid.trpm.service.activity.ActivityService;

@Controller
public class ClickRateController extends MapperDaoTemplate<ClickRate> {//用于统计三周年活动页按钮的点击率，数据存在张觥提供一个数据库里
	private static Logger logger = LoggerFactory.getLogger(ClickRateController.class);
	private static ArrayList<String> names =new ArrayList<String>();
	
	@Autowired
	ActivityService activityService;
	
	static{
		names.add("PC-joinUs");
		names.add("PC-shareFaceBook");
		names.add("H5-joinUs");
		names.add("H5-shareFaceBook");
	}
	
	@Autowired
	public ClickRateController(SqlSessionTemplate sqlSessionTemplate) {
		super(sqlSessionTemplate, ClickRate.class);
	}
	
	@RequestMapping(value="thirdYearAnniversaryClickRate", method = RequestMethod.GET)
	public void clickOneTime(HttpServletRequest request, HttpServletResponse response, @RequestParam String name){
		if(StringUtils.isEmpty(name)) return;
		if(!names.contains(name)) return;
		if(!activityService.isDuringThirdYeayAnniversary()) return;//不在三周年活动期间，接口无效
		String ip = request.getRemoteAddr();
		ClickRate cr = new ClickRate();
		cr.setName(name);
		cr.setIp(ip);
		super.save(cr);
		logger.info("Third year anniversary web page button click, name={}, ip={}",name,ip);
	}
}
