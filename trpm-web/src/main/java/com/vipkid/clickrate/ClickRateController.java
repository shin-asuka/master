package com.vipkid.clickrate;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.vipkid.trpm.service.activity.ActivityService;

@Controller
public class ClickRateController {//用于统计三周年活动页按钮的点击率，数据存在张觥提供一个数据库里
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
	@RequestMapping(value="thirdYearAnniversaryClickRate", method = RequestMethod.GET)
	public void clickOneTime(HttpServletRequest request, HttpServletResponse response, @RequestParam String name){
		if(StringUtils.isEmpty(name)) return;
		if(!names.contains(name)) return;
		if(!activityService.isDuringThirdYeayAnniversary()) return;//不在三周年活动期间，接口无效
		String ip = request.getRemoteAddr();
		insert(name,ip);
		logger.info("Third year anniversary web page button click, name={}, ip={}",name,ip);
	}
	
	private static Connection getConn() {
	    String driver = "com.mysql.jdbc.Driver";
	    String url = PropertyConfigurer.stringValue("third_year_anniversary_click_rate_db_url");
	    String username = PropertyConfigurer.stringValue("third_year_anniversary_click_rate_db_username");
	    String password = PropertyConfigurer.stringValue("third_year_anniversary_click_rate_db_password");
	    if(StringUtils.isEmpty(url)||StringUtils.isEmpty(username)||StringUtils.isEmpty(password)){
	    	return null;
	    }
	    Connection conn = null;
	    try {
	        Class.forName(driver); //classLoader,加载对应驱动
	        conn = (Connection) DriverManager.getConnection(url, username, password);
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return conn;
	}
	
	private static int insert(String name, String ip) {
	    Connection conn = getConn();
	    if(conn==null) return 0;
	    int i = 0;
	    String sql = "insert into third_year_anniversary_click_rate (button_name,ip) values(?,?)";
	    PreparedStatement pstmt;
	    try {
	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
	        pstmt.setString(1, name);
	        pstmt.setString(2, ip);
	        i = pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return i;
	}
}
