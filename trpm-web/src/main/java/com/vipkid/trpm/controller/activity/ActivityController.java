package com.vipkid.trpm.controller.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vipkid.http.vo.ThirdYearAnniversaryData;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.controller.AbstractController;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.activity.ActivityService;
import com.vipkid.trpm.service.passport.IndexService;
import com.vipkid.trpm.util.AES;

@Controller
public class ActivityController extends AbstractController{
    
    private static Logger logger = LoggerFactory.getLogger(ActivityController.class);
    
    @Autowired
    private ActivityService activityService;
    
    @Autowired
    private IndexService indexService;
    
    
    //上线时间
    private String searchdate = "2016-04-12 00:00:00";

    
    @RequestMapping("activity")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model){
        if(!this.showActivity()){
            return "passport/sign_in";
        }
        String token = AES.encrypt(String.valueOf(indexService.getUser(request).getId()), AES.getKey(AES.KEY_LENGTH_128,ApplicationConstant.AES_128_KEY));
        String shareUrl = PropertyConfigurer.stringValue("teacher.www")+"share/"+token+".shtml";
        model.addAttribute("shareUrl",shareUrl);
        model.addAttribute("www",PropertyConfigurer.stringValue("teacher.www"));
        try {
            Teacher teacher = this.activityService.findTeacherById(indexService.getUser(request).getId());
            if( teacher != null){
                Map<String,Object> map = this.activityService.readInfo(teacher, searchdate);
                if("1".equals(map.get("info"))){
                    model.addAllAttributes(map);
                    return "activity/index";
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            logger.error("读取老师信息异常："+e.getMessage(),e);
        }
        return "activity/errormessage";
    }
    
    @RequestMapping("share/{token}")
    @PreAuthorize("permitAll")
    public String facebook(HttpServletRequest request, HttpServletResponse response, Model model,@PathVariable String token){
        if(!this.showActivity()){
            model.addAttribute("shareMe","yes");
        }
        if(!StringUtils.isEmpty(token)){
            String shareUrl = PropertyConfigurer.stringValue("teacher.www")+"share/"+token+".shtml";
            model.addAttribute("www",PropertyConfigurer.stringValue("teacher.www"));
            model.addAttribute("shareUrl",shareUrl);
            try{
                String teacherId = AES.decrypt(token, AES.getKey(AES.KEY_LENGTH_128,ApplicationConstant.AES_128_KEY));
                if(teacherId.matches("[1-9]\\d+")){
                    Teacher teacher = this.activityService.findTeacherById(Long.valueOf(teacherId));
                    if( teacher != null){
                        Map<String,Object> map = this.activityService.readInfo(teacher, searchdate);
                        if("1".equals(map.get("info"))){
                            model.addAllAttributes(map);
                            return "activity/facebook";
                        }
                    }
                }
                
            } catch (Exception e) {
                logger.error("用户访问一个不合法的url，系统解密无效，不允许访问："+e.getMessage(),e);
                return "activity/errormessage"; 
            }
        }
        return "activity/errormessage"; 
    }
    
    // 超过2016-04-20天将不再显示
    private boolean showActivity(){
        Integer end = Integer.parseInt("20160420");
        Integer ctime = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        return ctime < end;
    }
    
 
    
   /**
    * 三周年庆的教师历程数据接口，活动页前端请求此接口获取json数据
    * @param token 加密后的teacherId(非必填)
    */
    @ResponseBody
    @RequestMapping(value = "/getThirdYearAnniversaryData", method = RequestMethod.GET)
	public Object getData(HttpServletRequest request, @RequestParam( required = false) String token){
    	if(!activityService.isDuringThirdYeayAnniversary()) return null;//到期后接口功能失效
    	long teacherId = 0;
    	if(StringUtils.isEmpty(token)){
    		User u ;
    		try {
				u = indexService.getUser(request);
			} catch (NullPointerException e) {
				return null;
			}
        	if(u==null) return null;
        	teacherId = u.getId();
    	}
    	else{
    		teacherId = activityService.decode(token);
    	}
    	if(teacherId<=0) {
    		return null;
    	}
    	ThirdYearAnniversaryData data = activityService.getThirdYearAnniversaryData(teacherId);
    	return data;
	}
}
