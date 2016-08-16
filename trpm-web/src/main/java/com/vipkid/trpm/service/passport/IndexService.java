package com.vipkid.trpm.service.passport;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.community.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.constant.ApplicationConstant.CourseType;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherModule;
import com.vipkid.trpm.entity.TeacherPageLogin;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.util.CacheUtils;
import com.vipkid.trpm.util.CookieUtils;

@Service
public class IndexService {

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

    /**
     * 获取当前登录用户
     *
     * @return User
     */
    public User getUser(HttpServletRequest request) {
        String token = CookieUtils.getValue(request, CookieKey.TRPM_TOKEN);
        String json = redisProxy.get(token);
        User user = JsonTools.readValue(json, User.class);
        return userDao.findById(user.getId());
    }

    /**
     * 判断User是否有PE权限
     * 
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @return boolean
     * @date 2016年7月4日
     */
    public boolean isPe(long teacherId) {
        List<TeacherModule> modulelist = teacherModuleDao.findByTeacherPe(teacherId);
        if (modulelist == null || modulelist.isEmpty()) {
            return false;
        }
        return true;
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
        String token = UUID.randomUUID().toString();
        redisProxy.set(token, JsonTools.getJson(user), 72 * 60 * 60);
        CookieUtils.setCookie(response, CookieKey.TRPM_TOKEN, token, null);
    }

    public void removeLoginCooke(HttpServletRequest request, HttpServletResponse response) {
        String token = CookieUtils.getValue(request, CookieKey.TRPM_TOKEN);
        if (token != null) {
            redisProxy.del(token);
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
