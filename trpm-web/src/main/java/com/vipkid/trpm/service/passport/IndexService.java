package com.vipkid.trpm.service.passport;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.vipkid.rest.config.RestfulConfig.RoleClass;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.constant.ApplicationConstant.CourseType;
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
public class IndexService {

	private final static Logger logger = LoggerFactory.getLogger(IndexService.class);
			
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
     * 获取当前登录的老师
     *
     * @return Teacher
     */
    public Teacher getTeacher(HttpServletRequest request) {
        User user = getUser(request);
        Teacher teacher = teacherDao.findById(user.getId());

        return teacher;
    }
    
    public Staff getStaff(Long id){
    	Staff staff = null;
    	if(id!=null){
    		staff = staffDao.findById(id);
    	}
    	return staff;
    }

    /**
     * 获取当前登录用户
     *
     * @return User
     */
    public User getUser(HttpServletRequest request) {
        String token = CookieUtils.getValue(request, CookieKey.TRPM_TOKEN);
        String key = CacheUtils.getUserTokenKey(token);
        
        User user = null;
        if(StringUtils.isNotBlank(key) && StringUtils.isNotBlank(token)){
        	String json = redisProxy.get(key);
            user = JsonTools.readValue(json, User.class);
            
            //判断当前用户所在地区的ip是否变化，如果变化。则返回空用户，用户重新登陆
            String ip = IpUtils.getRequestRemoteIP();
            String redisIp = user.getIp();
            //logger.info("检测用户IP地址  getUserIP user = {}, redisIp = {}, currentIp = {}",user.getId()+"|"+user.getUsername(),redisIp,ip);
            Boolean isIpChange = IpUtils.checkUserIpChange(user);
            if(isIpChange){
            	logger.info("用户IP地址发生变化  getUser userIPChange token = {},user = {}, redisIp = {}, currentIp = {}",token,user.getId()+"|"+user.getUsername(),redisIp,ip);
            	user = null;
            }else{
            	user = userDao.findById(user.getId());
//            	Integer timeout = CacheUtils.getLoginTimeout();
//                redisProxy.expire(key, timeout); //延长有效期
            }
        }
        return user;
    }

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

    public void setLoginCooke(HttpServletResponse response, User user) {
        String token = CacheUtils.getTokenId();
        String key = CacheUtils.getUserTokenKey(token);
        
        if(StringUtils.isNotBlank(key) && user!=null){
        	String ip = IpUtils.getRequestRemoteIP();
        	user.setIp(ip); //注入远程请求ip地址
        	logger.info("setLoginCooke 设置登录Redis ,user = {} , key = {},ip = {}",user.getId()+"|"+user.getUsername(),key,ip);
        	redisProxy.set(key, JsonTools.getJson(user), 72 * 60 * 60);
        }
        logger.info("setLoginCooke 设置登录Cookie ,teacherID = {},token = {} , key = {}",user.getId(),token,key);
        CookieUtils.setCookie(response, CookieKey.TRPM_TOKEN, token, null);
    }

    public void removeLoginCooke(HttpServletRequest request, HttpServletResponse response) {
        String token = CookieUtils.getValue(request, CookieKey.TRPM_TOKEN);
        String key = CacheUtils.getUserTokenKey(token);
        if(StringUtils.isNotBlank(key)){
            redisProxy.del(key);
        }
        CookieUtils.removeCookie(response, CookieKey.TRPM_TOKEN, null, null);
    }

    public void setCourseTypes(long userId, List<String> courseTypes) {
        String key = CacheUtils.getCoursesKey(userId);
        redisProxy.set(key, JsonTools.getJson(courseTypes));
    }

    public boolean enabledPracticum(long userId) {
        String key = CacheUtils.getCoursesKey(userId);
        String json = redisProxy.get(key);
        List<String> courseTypes = JsonTools.readValue(json, new TypeReference<List<String>>() {});
        return courseTypes.contains(CourseType.PRACTICUM);
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
        CookieUtils.removeCookie(response, ApplicationConstant.CookieKey.TRPM_CHANGE_WINDOW, null,
                null);
        String matching = "^([a-zA-Z0-9])+$";
        String matching1 = "^(?:.*[A-Za-z].*)(?:.*[0-9].*)|(?:.*[0-9].*)(?:.*[A-Za-z].*).{0,}$";
        if (StringUtils.isEmpty(strPwd) || !strPwd.matches(matching)
                || !(strPwd.matches(matching1))) {
            CookieUtils.setCookie(response, ApplicationConstant.CookieKey.TRPM_CHANGE_WINDOW,
                    ApplicationConstant.CookieKey.TRPM_CHANGE_WINDOW, null);
        }

    }

}
