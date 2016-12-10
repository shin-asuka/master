package com.vipkid.rest.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.vipkid.email.EmailUtils;
import com.vipkid.enums.OnlineClassEnum.CourseType;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.TeacherModuleEnum.RoleClass;
import com.vipkid.enums.TeacherPageLoginEnum.LoginType;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.config.TeacherInfo;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.dao.CourseDao;
import com.vipkid.trpm.dao.StaffDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherModuleDao;
import com.vipkid.trpm.dao.TeacherPageLoginDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Staff;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherPageLogin;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.util.CacheUtils;
import com.vipkid.trpm.util.CookieUtils;
import com.vipkid.trpm.util.IpUtils;

@Service
public class LoginService {
    
    private static final String LOGIN_ACTIVATION_EMAIL_KEY = "TRPM_REST_ACTIVATION_EMAIL_KEY:%s";

    private static final int EXPIRED_SECONDS = 30;

    private final static Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private TeacherPageLoginDao teacherLoginTypeDao;

    @Autowired
    private RedisProxy redisProxy;

    @Autowired
    private TeacherModuleDao teacherModuleDao;
    
    @Autowired
    private StaffDao staffDao;
    
    /**
     * 判断User是否有PE权限
     * 
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @return boolean
     * @date 2016年7月4日
     */
    public Map<String,Object> getAllRole(long teacherId) {
        String result = teacherModuleDao.findByTeacherModule(teacherId);
        Map<String,Object> roles = Maps.newHashMap();
        roles.put(RoleClass.PES, false);
        roles.put(RoleClass.TE, false);
        roles.put(RoleClass.TES, false);
        if(result.indexOf(","+RoleClass.PE+",") > -1){
            roles.put(RoleClass.PES,true);
        }
        if(result.indexOf(","+RoleClass.TE+",") > -1){
            roles.put(RoleClass.TE,true);
        }
        if(result.indexOf(","+RoleClass.TES+",") > -1){
            roles.put(RoleClass.TES,true);
        }
        return roles;
    }
    
    public Staff getStaff(Long id){
    	Staff staff = null;
    	if(id!=null){
    		staff = staffDao.findById(id);
    	}
    	return staff;
    }

    /**
     * 获取当前登录的老师
     * 
     * @return Teacher
     */
    public Teacher getTeacher() {
    	Teacher teacher = null;
    	User user = getPreUserByRedis();
    	if(user != null){
    		teacher = teacherDao.findById(user.getId());  
    	}
        return teacher; 
    }
    
    /**
     * 获取当前登录用户
     * 
     * @return User
     */
    public User getUser() {
    	User currentUser = null;
        User user = getPreUserByRedis();
        if(user != null){
        	currentUser = userDao.findById(user.getId());
        	if(currentUser !=null){
        		currentUser.setIp(user.getIp());
        	}
        }
        return currentUser;
    }
    
    /**
     * 从redis中获取用户ID
     * 
     * @return
     */
    private User getPreUserByRedis(){
    	User user = null;
    	String token = getToken();
    	String key = CacheUtils.getUserTokenKey(token);
    	if(StringUtils.isNotBlank(key)){
    		String json = redisProxy.get(key);
    		if(StringUtils.isNotBlank(json)){
    			user = JsonTools.readValue(json, User.class);
            }
    	}
    	if(user!=null){
    		Integer timeout = CacheUtils.getLoginTimeout();
            redisProxy.expire(key, timeout); //延长有效期
    	}
    	return user;
    }
    
    public String getToken(){
    	String token = null;
    	try {
    		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    		token = request.getHeader(CookieKey.AUTOKEN);
    		if(StringUtils.isBlank(token)){
    			token = CookieUtils.getValue(request, CookieKey.TRPM_TOKEN);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return token;
    }

    public boolean enabledPracticum(long userId) {
        String key = CacheUtils.getCoursesKey(userId);
        String json = redisProxy.get(key);
        List<String> courseTypes = JsonTools.readValue(json, new TypeReference<List<String>>() {});
        return courseTypes.contains(CourseType.PRACTICUM);
    }
    
    /**
     * 查询老师可以教的课程类型列表
     * 
     * @param teacherId
     * @return List<String>
     */
    public List<String> getCourseType(long teacherId) {
        List<String> courseTypes = Lists.newArrayList();

        courseDao.findByTeacherId(teacherId).stream().forEach((course) -> {
            courseTypes.add(course.getType());
        });
        return courseTypes;
    }

    /**
     * 处理不显示提示Layer逻辑
     * 
     * @author John
     *
     * @param pageLogin
     */
    public void doDisableLayer(TeacherPageLogin pageLogin) {
        teacherLoginTypeDao.saveTeacherPageLogin(pageLogin);
    }

    public User getStaff(String username, String password) {
        return userDao.selectOne(new User().setUsername(username).setPassword(password));
    }

    public User selectOne(User user) {
        return this.userDao.selectOne(user);
    }

    public User getLoginUser(String username) {
        return userDao.findByLogin(username);
    }

    public String setLoginToken(HttpServletResponse response, User user) {
        String token = CacheUtils.getTokenId();
        String key = CacheUtils.getUserTokenKey(token);
        
        if(StringUtils.isNotBlank(key) && user!=null){
        	String ip = IpUtils.getRequestRemoteIP();
        	user.setIp(ip); //注入远程请求ip地址
        	
        	Integer timeout = CacheUtils.getLoginTimeout();
        	logger.info("setLoginCooke 设置登录Redis ,user = {} , key = {},ip = {},timeout = {} (second)",user.getId()+"|"+user.getUsername(),key,ip,timeout);
        	redisProxy.set(key, JsonTools.getJson(user), timeout);
        }
        logger.info("setLoginCooke 设置登录Cookie ,teacherID = {},token = {} , key = {}",user.getId(),token,key);
        return token;
    }

    public void removeLoginCooke(HttpServletRequest request, HttpServletResponse response) {
        String token = getToken();
        if(StringUtils.isNotBlank(token)){
        	String key = CacheUtils.getUserTokenKey(token);
            if(StringUtils.isNotBlank(key)){
                redisProxy.del(key);
            }
            CookieUtils.removeCookie(response, CookieKey.TRPM_TOKEN, null, null);
        }
    }

    public void setCourseTypes(long userId, List<String> courseTypes) {
        String key = CacheUtils.getCoursesKey(userId);
        redisProxy.set(key, JsonTools.getJson(courseTypes));
    }

    /**
     * 是否是PE
     *  
     * @Author:ALong (ZengWeiLong)
     * @param userId
     * @return    
     * boolean
     * @date 2016年8月23日
     */
    public boolean isPe(long userId) {
        String key = CacheUtils.getCoursesKey(userId);
        String json = redisProxy.get(key);
        List<String> courseTypes = JsonTools.readValue(json, new TypeReference<List<String>>() {
        });
        return courseTypes.contains(CourseType.PRACTICUM);
    }
        
    /**
     * 判断User拥有的权限
     * 
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @return boolean
     * @date 2016年7月4日
     */
    public void findByTeacherModule(TeacherInfo teacherinfo,String lifeCycle) {
        Map<String,Object> roles = teacherinfo.getRoles();
        
        if(LifeCycle.REGULAR.toString().equalsIgnoreCase(lifeCycle)){
            roles.put(RoleClass.PE, this.isPe(teacherinfo.getTeacherId()));
            String result = teacherModuleDao.findByTeacherModule(teacherinfo.getTeacherId());
            logger.info(" result module:{}",result);
            if(result.indexOf(","+RoleClass.PE+",") > -1){
                roles.put(RoleClass.PES,true);
            }
            if(result.indexOf(","+RoleClass.TE+",") > -1){
                roles.put(RoleClass.TE,true);
            }
            if(result.indexOf(","+RoleClass.TES+",") > -1){
                roles.put(RoleClass.TES,true);
            }
            teacherinfo.setRoles(roles);
            teacherinfo.setEvaluation(teacherLoginTypeDao.isType(teacherinfo.getTeacherId(),LoginType.EVALUATION.val()));
            teacherinfo.setEvaluationClick(teacherLoginTypeDao.isType(teacherinfo.getTeacherId(),LoginType.EVALUATION_CLICK.val()));
        }
    }  
    
    public Map<String,Object> sendActivationEmail(String email){
        
        String key = String.format(LOGIN_ACTIVATION_EMAIL_KEY, email);
        
        if (null != redisProxy.get(key)) {
            Map<String,Object> result = Maps.newHashMap();
            result.put("expire", redisProxy.ttl(key));
            return ReturnMapUtils.returnFail("The activation email ["+email+"] time is not expire",result);
        }
        
        Teacher teacher = teacherDao.findByEmail(email);
        if (0 == teacher.getId()) {
            return ReturnMapUtils.returnFail(" Email is null ");
        }
        
        EmailUtils.sendActivationEmail(teacher);
        redisProxy.set(key, "true", EXPIRED_SECONDS);
        return ReturnMapUtils.returnSuccess();
    }
    

    public User findUserById(long id) {
        return this.userDao.findById(id);
    }

    public User findUserByToken(String token) {
        return this.userDao.findByToken(token);
    }

    public Teacher findTeacherById(long id) {
        return this.teacherDao.findById(id);
    }

    /**
     * 根据recruitmentId 查询Teacher
     * 
     * @Author:ALong (ZengWeiLong)
     * @param recruitmentId
     * @return Teacher
     * @date 2016年3月2日
     */
    public Teacher findTeacherByRecruitmentId(String recruitmentId) {
        if (StringUtils.isEmpty(recruitmentId)) {
            return null;
        }
        Teacher resultEntity = this.teacherDao.findByRecruitToken(recruitmentId);
        return resultEntity;
    }

    public void changePasswordNotice(HttpServletResponse response, String strPwd) {
        CookieUtils.removeCookie(response, ApplicationConstant.CookieKey.TRPM_CHANGE_WINDOW,null,null);
        String matching = RestfulConfig.Validate.WD_REG;
        String matching1 = RestfulConfig.Validate.PASSWORD_REG;
        if (StringUtils.isEmpty(strPwd) || !strPwd.matches(matching) || !(strPwd.matches(matching1))) {
            CookieUtils.setCookie(response, ApplicationConstant.CookieKey.TRPM_CHANGE_WINDOW, ApplicationConstant.CookieKey.TRPM_CHANGE_WINDOW, null);
        }

    }

}
