package com.vipkid.rest.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.TeacherPageLoginEnum.LoginType;
import com.vipkid.enums.UserEnum;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.config.TeacherInfo;
import com.vipkid.rest.dto.LoginDto;
import com.vipkid.rest.dto.RegisterDto;
import com.vipkid.rest.dto.ResetPasswordDto;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.service.TeacherPageLoginService;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.security.SHA256PasswordEncoder;
import com.vipkid.trpm.service.passport.PassportService;
import com.vipkid.trpm.util.Bean2Map;
import com.vipkid.trpm.util.IpUtils;

@RestController
@RequestMapping("/user")
public class LoginController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private PassportService passportService;
    
    @Autowired
    private TeacherPageLoginService teacherPageLoginService;

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
            @RequestBody LoginDto bean) {
        try{
            List<Result> list = ValidateUtils.checkBean(bean, false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.info(list.get(0).getName() + "," + list.get(0).getMessages());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_ERROR);
            }
            
            logger.info(" 请求参数 Json ={} ",JsonTools.getJson(bean));
            
            String ip = IpUtils.getIpAddress(request);
            logger.info("客户端IP：{}",ip);
            if (StringUtils.isEmpty(ip) || passportService.isExceedMaxLoginPerIP(ip)
                    || passportService.isExceedMaxLoginFailed(bean.getEmail())) {
                if (StringUtils.isBlank(bean.getKey()) || StringUtils.isBlank(bean.getImageCode())) {
                    logger.warn("同一IP超过最大登录次数，或登录失败次数超限，需要添加验证码,userName = {}",bean.getEmail());
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.VERIFY_CODE);
                } else if (!passportService.checkVerifyCode(bean.getKey(), bean.getImageCode())) {
                    logger.warn("验证码错误，key = {},imageCode = {}", bean.getKey(), bean.getImageCode());
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.VERIFY_CODE_ERROR);
                }
            }
            if (StringUtils.isNotBlank(ip)) {
                passportService.addLoginCountPerIP(ip);
            }
            
            logger.info("user check start email:{}!",bean.getKey());
                        
            // 根据email，检查是否有此账号。
            User user = this.passportService.findUserByUsername(bean.getEmail());
            if (null == user) {
                //add
                passportService.addLoginFailedCount(bean.getEmail());
                logger.warn("user is null email:" + bean.getEmail());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_NULL);
            }
            // token check
            logger.info("token check start!");
            if (StringUtils.isEmpty(user.getToken())) {
                user = this.passportService.updateUserToken(user);
            }
            // 密码验证
            SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
            String mypwd = encoder.encode(bean.getPassword());
            if (!mypwd.equals(user.getPassword())) {
                //add
                passportService.addLoginFailedCount(bean.getEmail());
                logger.warn(" Username or password  error!" + bean.getEmail());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.PWD_ERROR);
            }
            // 检查用户类型
            if (!UserEnum.Dtype.TEACHER.val().equals(user.getDtype())) {
                //add
                passportService.addLoginFailedCount(bean.getEmail());
                logger.warn("user not is teacher id = " + user.getId());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.DTYPE_ERROR);
            }
            // techer null check
            Teacher teacher = this.passportService.findTeacherById(user.getId());
            if (teacher == null) {
                //add
                passportService.addLoginFailedCount(bean.getEmail());
                logger.warn("teacher is null id = " + user.getId());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.TEACHER_NULL);
            }
            logger.info("登陆  FAIL start:{} !",teacher.getLifeCycle());
            // 检查老师状态是否FAIL
            if (TeacherEnum.LifeCycle.FAIL.toString().equals(teacher.getLifeCycle())) {
                //add
                passportService.addLoginFailedCount(bean.getEmail());
                logger.warn(" Username fail error!" + bean.getEmail());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_FAIL);
            }    
            logger.info("登陆  QUIT start !");
            // 检查老师状态是否QUIT
            if (TeacherEnum.LifeCycle.QUIT.toString().equals(teacher.getLifeCycle())) {
                //add
                passportService.addLoginFailedCount(bean.getEmail());
                logger.warn(" Username quit error!" + bean.getEmail());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_QUIT);
            }
    
            // 检查用户状态是否锁住
            logger.info("登陆  isLocked start :{} !",user.getStatus());
            if (UserEnum.Status.isLocked(user.getStatus())) {
                // 新注册的需要激活
                if (TeacherEnum.LifeCycle.SIGNUP.toString().equals(teacher.getLifeCycle())) {
                    if(PropertyConfigurer.booleanValue("signup.send.mail.switch")){
                        //add
                        passportService.addLoginFailedCount(bean.getEmail());
                        logger.warn(" Username 没有激活 error!" + bean.getEmail());
                        response.setStatus(HttpStatus.BAD_REQUEST.value());
                        return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_ACTIVITY);
                    }else{
                        this.passportService.updateUserStatus(user);
                    }
                } else {
                    // 否则告诉被锁定
                    //add
                    passportService.addLoginFailedCount(bean.getEmail());
                    logger.warn(" Username locked error!" + bean.getEmail());
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_LOCKED);
                }            
            }
    
            // 如果招聘Id不存在则set进去
            if (StringUtils.isEmpty(teacher.getRecruitmentId())) {
                teacher.setRecruitmentId(this.passportService.updateRecruitmentId(teacher));
            }
    
            logger.info("登陆  LifeCycle check:{}!",teacher.getLifeCycle());
            
            //模拟登陆logger
            Map<String,Object> result = Maps.newHashMap();
            result.put("loginToken", loginService.setLoginToken(response, user));
            result.put("LifeCycle", teacher.getLifeCycle());
            // 只有正式老师登陆后才做强制修改密码判断
            if(LifeCycle.REGULAR.toString().equalsIgnoreCase(teacher.getLifeCycle())){
                loginService.changePasswordNotice(response, bean.getPassword());
                /* 设置老师能教的课程类型列表 */
                loginService.setCourseTypes(user.getId(), loginService.getCourseType(user.getId()));
            }
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage());
        }
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
    public Map<String, Object> register(HttpServletRequest request, HttpServletResponse response,
            @RequestBody RegisterDto bean) {
        List<Result> list = ValidateUtils.checkBean(bean, false);
        if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            logger.info(list.get(0).getName() + "," + list.get(0).getMessages());
            return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_ERROR);
        }
        String email = StringUtils.trim(bean.getEmail());
        logger.info("sign up teacher email = {" + email + "}");
        try{
            if (StringUtils.isBlank(bean.getKey()) || StringUtils.isBlank(bean.getImageCode())) {
                logger.warn("验证码为空或验证码key为空");
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.VERIFY_CODE);
            }
            if (!passportService.checkVerifyCode(bean.getKey(),bean.getImageCode())) {
                logger.warn("验证码校验不通过！");
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.VERIFY_CODE_ERROR);
            }
            if (StringUtils.isBlank(email) || StringUtils.isBlank(bean.getPassword())) {
                logger.warn("email or password is null email:" + email);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_ERROR);
                // 2.用户名可用，执行业务，
            }
            
            logger.info("user check start email:{}!",email);
            
            User user = passportService.findUserByUsername(email);
            // 1.用户名存在，反馈
            if (user != null) {
                logger.warn("Email 已经注册过:" + email);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_EXITS);
                // 2.用户名可用，执行业务，
            }
            // 执行业务逻辑
            Map<String, Object> result = passportService.saveSignUp(email, bean.getPassword(), bean.getRefereeId(), bean.getPartnerId());
            if(ReturnMapUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
                logger.warn("注册失败：{}",result);
                return result;
            }
            //注册成功
            user = (User)result.get("user");
            String loginToken = loginService.setLoginToken(response, user);
            result.put("loginToken", loginToken);
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage());
        }
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
            @RequestBody Map<String,String> pram) {
        try{
            String email = pram.get("email");
            if (StringUtils.isBlank(email)) {
                logger.warn("email is null");
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_ERROR);
            }
            
            logger.info("user check start email:{}!",email);
            
            // 根据email，检查是否有此账号。
            User user = this.passportService.findUserByUsername(email);
            if (null == user) {
                logger.warn("user is null email:" + email);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_NULL);
            }
            // 检查用户类型
            if (!UserEnum.Dtype.TEACHER.val().equals(user.getDtype())) {
                logger.warn("user not is teacher id = " + user.getId());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.DTYPE_ERROR);
            }
            // techer null check
            Teacher teacher = this.passportService.findTeacherById(user.getId());
            if (teacher == null) {
                logger.warn("teacher is null id = " + user.getId());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.TEACHER_NULL);
            }
            // 检查老师状态是否FAIL
            if (TeacherEnum.LifeCycle.FAIL.toString().equals(teacher.getLifeCycle())) {
                logger.warn("teacher is fail id = " + user.getId());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_FAIL);
            }
    
            // 检查用户状态是否锁住
            if (UserEnum.Status.isLocked(user.getStatus())) {
                // 新注册的需要激活
                if (TeacherEnum.LifeCycle.SIGNUP.toString().equals(teacher.getLifeCycle())) {
                    if(PropertyConfigurer.booleanValue("signup.send.mail.switch")){
                        logger.warn("teacher 未激活  id = " + user.getId());
                        response.setStatus(HttpStatus.BAD_REQUEST.value());
                        return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_ACTIVITY);
                    }else{
                        this.passportService.updateUserStatus(user);
                    }
                } else {
                    // 否则告诉被锁定
                    logger.warn("teacher is 被锁定 id = " + user.getId());
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_LOCKED);
                }
            }
            // 检查老师状态是否QUIT
            if (TeacherEnum.LifeCycle.QUIT.toString().equals(teacher.getLifeCycle())) {
                logger.warn("teacher is QUIT id = " + user.getId());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_QUIT);
            }
            logger.info("resetPasswordRequest OK : " + email);
            return this.passportService.senEmailForPassword(user);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage());
        }
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
            @RequestBody ResetPasswordDto bean) {
        try{
            
            List<Result> list = ValidateUtils.checkBean(bean, false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.info(list.get(0).getName() + "," + list.get(0).getMessages());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_ERROR);
            }
            
            if (!this.passportService.checkTokenTimeout(bean.getToken())) {
                logger.warn("token 过期   token:" + bean.getToken());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.TOKEN_OVERDUE);
            }
            Teacher teacher = this.passportService.findByRecruitmentId(bean.getToken());
            if (teacher == null) {
                logger.warn("teacher is null:" + teacher);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.TEACHER_NULL);
            }
            // 修改成功后strToken替换成最新，使原来的失效
            Map<String,Object> retMap = this.passportService.updatePassword(teacher, bean.getPassword());
            if (ReturnMapUtils.isFail(retMap)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.TOKEN_ERROR);
            }
            User user = this.passportService.findUserById(teacher.getId());
            String loginToken = loginService.setLoginToken(response, user);
            retMap.put("loginToken", loginToken);
            return retMap;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage());
        }
    }

    @RequestMapping(value = "/auth", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    @RestInterface
    public Map<String, Object> auth(HttpServletRequest request, HttpServletResponse response) {
        try{
            // 检查老师状态是否FAIL
            if (TeacherEnum.LifeCycle.FAIL.toString().equals(this.getTeacher(request).getLifeCycle())) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_FAIL);
            }
    
            // 检查老师状态是否QUIT
            if (TeacherEnum.LifeCycle.QUIT.toString().equals(this.getTeacher(request).getLifeCycle())) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_QUIT);
            }
            
            User user = this.getUser(request);
            // 检查用户类型
            if (!UserEnum.Dtype.TEACHER.val().equals(user.getDtype())) {
                logger.warn("user not is teacher id = " + user.getId());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.DTYPE_ERROR);
            }
            
            TeacherInfo teacherinfo = new TeacherInfo();
            teacherinfo.setTeacherId(this.getUser(request).getId());
            //权限判断 start
            logger.info("check module start teacherId:{}",this.getUser(request).getId());
            loginService.findByTeacherModule(teacherinfo,this.getTeacher(request).getLifeCycle());
            //其他信息       
            teacherinfo.setInfo(this.getTeacher(request),this.getUser(request));
            Map<String,Object> success = ReturnMapUtils.returnSuccess();
            success.putAll(Bean2Map.toMap(teacherinfo));
            success.put("evaluation",teacherPageLoginService.isType(this.getUser(request).getId(),LoginType.EVALUATION));
            success.put("evaluationClick",teacherPageLoginService.isType(this.getUser(request).getId(),LoginType.EVALUATION_CLICK));
            logger.info("check OK json:{}",JsonTools.getJson(success));
            return success;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage());
        }
    }     
    
    @RequestMapping(value = "/applyActivationEmail", method = RequestMethod.POST,produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> applyActivationEmail(HttpServletRequest request,
            HttpServletResponse response, @RequestParam(required = true) String email) {
        
        if (StringUtils.isBlank(email)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(" Email  is null ");
        }
        
        Map<String,Object> result = this.loginService.sendActivationEmail(email);
        if(ReturnMapUtils.isFail(result)){
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return result;
        }
        
        logger.info("Apply again activation email [{}] ok", email);
        return ReturnMapUtils.returnSuccess();
    }
    
    
    @RequestMapping(value = "/activation", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public String userActivation(HttpServletRequest request, HttpServletResponse response, @RequestParam("uuid") String privateCode) throws IOException {
        if (StringUtils.isNotBlank(privateCode)) {
            String result = this.passportService.userActivation(privateCode);
            if (StringUtils.isNotBlank(result)) {
                logger.info("User execute activation link success for email 。。。privateCode="+privateCode);
            }
        }
        return "redirect:/";
    }
}
