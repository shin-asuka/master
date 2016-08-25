package com.vipkid.rest.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.UserEnum;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.constant.ApplicationConstant.TeacherLifeCycle;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.security.SHA256PasswordEncoder;
import com.vipkid.trpm.service.passport.PassportService;
import com.vipkid.trpm.service.rest.LoginService;
import com.vipkid.trpm.util.AES;
import com.vipkid.trpm.util.IPUtils;

@RestController
@RequestMapping("/user")
public class LoginController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private PassportService passportService;

    @Autowired
    private LoginService loginService;

    /**
     * 1.用户名，密码认证 2.将用户token写入redis 3.将用户token写入Cookie,由客户端调用
     * 
     * @Author:ALong (ZengWeiLong)
     * @param email 登陆用户
     * @param password 登陆密码
     * @return String
     * @date 2016年5月16日
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> login(HttpServletRequest request, HttpServletResponse response,
            @RequestParam String email, @RequestParam String password,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "imageCode", required = false) String imageCode) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("status", RestfulConfig.HttpStatus.STATUS_403);
        logger.info(" 请求参数 email ： " + email + ";password=" + password);
        if (StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
            logger.warn("email or password is null password:" + password + ";email:" + email);
            result.put("info", ApplicationConstant.AjaxCode.ERROR_CODE);
            return result;
        }
        String ip = IPUtils.getIpAddress(request);
        logger.info("客户端IP：{}",ip);
        if (StringUtils.isEmpty(ip) || passportService.isExceedMaxLoginPerIP(ip)
                || passportService.isExceedMaxLoginFailed(email)) {
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(imageCode)) {
                logger.warn("同一IP超过最大登录次数，或登录失败次数超限，需要添加验证码,userName = {}", email);
                result.put("info", ApplicationConstant.AjaxCode.VERIFY_CODE);
                return result;
            } else if (!passportService.checkVerifyCode(key, imageCode)) {
                logger.warn("验证码错误，key = {},imageCode = {}", key, imageCode);
                result.put("info", ApplicationConstant.AjaxCode.VERIFY_CODE_ERROR);
                return result;
            }
        }
        if (StringUtils.isNotEmpty(ip)) {
            passportService.addLoginCountPerIP(ip);
        }
        User user = passportService.findUserByUsername(email);
        // 根据email，检查是否有此账号。
        if (null == user) {
            logger.warn(" User is Null " + email + ";password=" + password);
            result.put("info", ApplicationConstant.AjaxCode.ERROR_CODE);
            passportService.addLoginFailedCount(email);
            return result;
        }

        // token check
        if (StringUtils.isEmpty(user.getToken())) {
            user = this.passportService.updateUserToken(user);
        }

        logger.info("password check start!");
        // 密码验证
        SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
        String mypwd = encoder.encode(password);
        if (!mypwd.equals(user.getPassword())) {
            logger.warn(" Username or password  error!" + email + ";password=" + password);
            result.put("info", ApplicationConstant.AjaxCode.ERROR_CODE);
            passportService.addLoginFailedCount(email);
            return result;
        }

        logger.info("password Dtype start!");
        // 非教师在此登陆
        if (!(UserEnum.Dtype.TEACHER.toString()).equals(user.getDtype())) {
            logger.warn(" Username type error!" + email + ";password=" + password);
            result.put("info", ApplicationConstant.AjaxCode.TYPE_CODE);
            passportService.addLoginFailedCount(email);
            return result;
        }

        logger.info("teacher null start!");
        Teacher teacher = this.passportService.findTeacherById(user.getId());
        if (teacher == null) {
            logger.warn(" Username teacher error!" + email + ";password=" + password);
            result.put("info", ApplicationConstant.AjaxCode.ERROR_CODE);
            passportService.addLoginFailedCount(email);
            return result;
        }

        logger.info("登陆  FAIL start !");
        // 检查老师状态是否FAIL
        if (TeacherEnum.LifeCycle.FAIL.toString().equals(teacher.getLifeCycle())) {
            logger.warn(" Username fail error!" + email + ";password=" + password);
            result.put("info", ApplicationConstant.AjaxCode.QUIT_CODE);
            passportService.addLoginFailedCount(email);
            return result;
        }

        logger.info("登陆  QUIT start !");
        // 检查老师状态是否QUIT
        if (TeacherEnum.LifeCycle.QUIT.toString().equals(teacher.getLifeCycle())) {
            logger.warn(" Username quit error!" + email + ";password=" + password);
            result.put("info", ApplicationConstant.AjaxCode.QUIT_CODE);
            passportService.addLoginFailedCount(email);
            return result;
        }

        // 检查用户状态是否锁住
        logger.info("登陆  isLocked start !");
        if (UserEnum.Status.isLocked(user.getStatus())) {
            // 新注册的需要激活
            if (TeacherEnum.LifeCycle.SIGNUP.toString().equals(teacher.getLifeCycle())) {
                logger.warn(" Username 没有激活 error!" + email + ";password=" + password);
                result.put("info", ApplicationConstant.AjaxCode.LOCKED_CODE);
                passportService.addLoginFailedCount(email);
                return result;
            } else {
                // 否则告诉被锁定
                logger.warn(" Username locked error!" + email + ";password=" + password);
                result.put("info", ApplicationConstant.AjaxCode.QUIT_CODE);
                passportService.addLoginFailedCount(email);
                return result;
            }
        }

        // 如果招聘Id不存在则set进去
        if (StringUtils.isEmpty(teacher.getRecruitmentId())) {
            teacher.setRecruitmentId(this.passportService.updateRecruitmentId(teacher));
        }

        /* 判断老师的LifeCycle，进行项目跳转 */
        logger.info("登陆  REGULAR start !");
        if (TeacherLifeCycle.REGULAR.toString().equals(teacher.getLifeCycle())) {
            logger.info("to teacher !");
            // 只有教师端老师登陆后才做强制修改密码判断
            loginService.changePasswordNotice(response, password);
            loginService.setLoginCooke(response, user);
            /* 设置老师能教的课程类型列表 */
            loginService.setCourseTypes(user.getId(), loginService.getCourseType(user.getId()));
            result.put("portal", RestfulConfig.Port.TEACHER);
            result.put("LifeCycle", teacher.getLifeCycle());
            result.put("action", "schedule.shtml");
            // 招聘端跳转URL
        } else {
            logger.info("to recruitment !");
            result.put("portal", RestfulConfig.Port.RECRUITMENT);
            result.put("LifeCycle", teacher.getLifeCycle());
            result.put("action", "signlogin.shtml?token="
                    + AES.encrypt(user.getToken(), AES.getKey(AES.KEY_LENGTH_128, ApplicationConstant.AES_128_KEY)));
        }
        // 处理跳转的页面
        result.put("status", RestfulConfig.HttpStatus.STATUS_200);
        return result;
    }

    /**
     * 注册用户注册老师
     * 
     * @Author:ALong (ZengWeiLong)
     * @param email 注册用户
     * @param password 注册密码
     * @return String code码
     * @date 2016年5月16日
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> register(@RequestParam String email, @RequestParam String password,
            @RequestParam(value = "refereeId", required = false) String refereeId,
            @RequestParam(value = "key", required = true) String key,
            @RequestParam(value = "imageCode", required = true) String imageCode) {
        logger.info("sign up teacher email = {" + email + "," + password + "}");
        Map<String, Object> result = Maps.newHashMap();
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(imageCode)) {
            logger.warn("验证码为空或验证码key为空");
            result.put("info", ApplicationConstant.AjaxCode.VERIFY_CODE);
            result.put("status", HttpStatus.BAD_REQUEST.value());
            return result;
        }
        if (!passportService.checkVerifyCode(key,imageCode)) {
            logger.warn("验证码校验不通过！");
            result.put("info", ApplicationConstant.AjaxCode.VERIFY_CODE_ERROR);
            result.put("status", HttpStatus.UNAUTHORIZED.value());
            return result;
        }
        if (StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
            logger.warn("email or password is null password:" + password + ";email:" + email);
            result.put("info", ApplicationConstant.AjaxCode.EMAIL_CODE);
            result.put("status", RestfulConfig.HttpStatus.STATUS_403);
            return result;
            // 2.用户名可用，执行业务，
        }
        User user = passportService.findUserByUsername(email);
        // 1.用户名存在，反馈
        if (user != null) {
            logger.warn("Email 已经注册:" + email);
            result.put("info", ApplicationConstant.AjaxCode.ERROR_CODE);
            result.put("status", RestfulConfig.HttpStatus.STATUS_403);
            return result;
            // 2.用户名可用，执行业务，
        }
        // 执行业务逻辑
        result.putAll(passportService.doSignUp(email, password, refereeId));
        result.put("status", RestfulConfig.HttpStatus.STATUS_200);
        return result;
    }

    /**
     * 重置密码请求
     * 
     * @Author:ALong (ZengWeiLong)
     * @param request
     * @param response
     * @param email 重置用户
     * @return String code码
     * @date 2016年5月16日
     */
    @RequestMapping(value = "/resetPasswordRequest", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> resetPasswordRequest(HttpServletRequest request, HttpServletResponse response,
            @RequestParam String email) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("status", RestfulConfig.HttpStatus.STATUS_403);
        if (StringUtils.isBlank(email)) {
            logger.warn("email is null");
            result.put("info", ApplicationConstant.AjaxCode.ERROR_CODE);
            return result;
        }
        // 根据email，检查是否有此账号。
        User user = this.passportService.findUserByUsername(email);
        if (null == user) {
            logger.warn("user is null email:" + email);
            result.put("info", ApplicationConstant.AjaxCode.ERROR_CODE);
            return result;
        }
        // 检查用户类型
        if (!UserEnum.Dtype.TEACHER.toString().equals(user.getDtype())) {
            logger.warn("user not is teacher id = " + user.getId());
            result.put("info", ApplicationConstant.AjaxCode.TYPE_CODE);
            return result;
        }
        // techer null check
        Teacher teacher = this.passportService.findTeacherById(user.getId());
        if (teacher == null) {
            logger.warn("teacher is null id = " + user.getId());
            result.put("info", ApplicationConstant.AjaxCode.ERROR_CODE);
            return result;
        }
        // 检查老师状态是否FAIL
        if (TeacherEnum.LifeCycle.FAIL.toString().equals(teacher.getLifeCycle())) {
            logger.warn("teacher is fail id = " + user.getId());
            result.put("info", ApplicationConstant.AjaxCode.QUIT_CODE);
            return result;
        }

        // 检查用户状态是否锁住
        if (UserEnum.Status.isLocked(user.getStatus())) {
            // 新注册的需要激活
            if (TeacherEnum.LifeCycle.SIGNUP.toString().equals(teacher.getLifeCycle())) {
                logger.warn("teacher 未激活  id = " + user.getId());
                result.put("info", ApplicationConstant.AjaxCode.LOCKED_CODE);
                return result;
            } else {
                // 否则告诉被锁定
                logger.warn("teacher is 被锁定 id = " + user.getId());
                result.put("info", ApplicationConstant.AjaxCode.QUIT_CODE);
                return result;
            }
        }
        // 检查老师状态是否QUIT
        if (TeacherEnum.LifeCycle.QUIT.toString().equals(teacher.getLifeCycle())) {
            logger.warn("teacher is QUIT id = " + user.getId());
            result.put("info", ApplicationConstant.AjaxCode.QUIT_CODE);
            return result;
        }
        logger.info("resetPasswordRequest OK : " + email);
        result.putAll(this.passportService.senEmailForPassword(user));
        result.put("status", RestfulConfig.HttpStatus.STATUS_200);
        logger.info("send Email finished : " + email);
        return result;
    }

    /**
     * 重置密码提交
     * 
     * @Author:ALong (ZengWeiLong)
     * @param request
     * @param response
     * @param password 新密码
     * @param token 修改密码认证
     * @return String code码
     * @date 2016年5月16日
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> resetPassword(HttpServletRequest request, HttpServletResponse response,
            @RequestParam String password, @RequestParam String token) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("status", RestfulConfig.HttpStatus.STATUS_403);
        result.put("info", ApplicationConstant.AjaxCode.ERROR_CODE);
        if (StringUtils.isBlank(password) || StringUtils.isBlank(token)) {
            logger.warn("password or token is null password = " + password + ";token:" + token);
            return result;
        }
        if (!this.passportService.checkTokenTimeout(token)) {
            logger.warn("token 过期   token:" + token);
            return result;
        }
        Teacher teacher = this.passportService.findByRecruitmentId(token);
        if (teacher == null) {
            logger.warn("teacher is null:" + teacher);
            return result;
        }
        // 修改成功后strToken替换成最新
        token = this.passportService.updatePassword(teacher, password);
        if (StringUtils.isEmpty(token)) {
            logger.warn("update token fail ,token:" + token);
            return result;
        }
        // 重置密码后模拟登陆
        result.remove("info");
        
        result.put("status", RestfulConfig.HttpStatus.STATUS_200);
        result.put("portal", RestfulConfig.Port.RECRUITMENT);
        if (TeacherEnum.LifeCycle.REGULAR.toString().equals(teacher.getLifeCycle())) {
            result.put("portal", RestfulConfig.Port.TEACHER);
        }
        result.put("action", "signlogin.shtml?token=" + token);
        return result;
    }

    @RequestMapping(value = "/auth", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> auth(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = Maps.newHashMap();
        String token = request.getHeader(CookieKey.AUTOKEN);
        result.put("status", RestfulConfig.HttpStatus.STATUS_403);
        if (StringUtils.isBlank(token)) {
            logger.warn("auth : token is null");
            return result;
        }
        User user = loginService.getUser(request);
        if (user == null) {
            logger.warn("check auth user is null " + user);
            return result;
        }
        Teacher teacher = loginService.getTeacher(request);
        if (teacher == null) {
            logger.warn("check auth teacher is null " + teacher);
            return result;
        }
        
        logger.info("check teacher is PE:" + teacher.getId());
        String role = loginService.isPe(teacher.getId()) ? "":"PE,";
        
        logger.info("check teacher is PES:" + teacher.getId());
        role += loginService.isPes(teacher.getId()) ? "":"PE-Supervisor,";
        
        logger.info("check result is role:{},teacherId:{}",role,teacher.getId());
        result.put("role",role);
        
        String headsrc = teacher.getAvatar();
        if(StringUtils.isNotBlank(headsrc)){
            headsrc = PropertyConfigurer.stringValue("oss.url_preffix") + (headsrc.startsWith("/") ? headsrc:"/"+headsrc);
        }
        result.put("teacherId",teacher.getId());
        result.put("headsrc", headsrc);
        result.put("showName", user.getName());
        result.put("status", RestfulConfig.HttpStatus.STATUS_200);
        return result;
    }
}
