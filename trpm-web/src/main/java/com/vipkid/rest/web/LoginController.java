package com.vipkid.rest.web;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Preconditions;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.service.portal.TeacherService;
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
import com.vipkid.enums.UserEnum;
import com.vipkid.http.service.FileHttpService;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.config.TeacherInfo;
import com.vipkid.rest.dto.ForgetPasswordDto;
import com.vipkid.rest.dto.LoginDto;
import com.vipkid.rest.dto.RegisterDto;
import com.vipkid.rest.dto.ResetPasswordDto;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.service.AdminQuizService;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.teacher.tools.security.SHA256PasswordEncoder;
import com.vipkid.trpm.constant.ApplicationConstant.AjaxCode;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.huanxin.HuanxinService;
import com.vipkid.trpm.service.passport.PassportService;
import com.vipkid.trpm.util.Bean2Map;
import com.vipkid.trpm.util.CookieUtils;
import com.vipkid.trpm.util.DateUtils;
import com.vipkid.trpm.util.IpUtils;

@RestController
@RequestMapping("/user")
public class LoginController extends RestfulController {
    

	private static final int TH_21_DAY = 21;

	private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private PassportService passportService;
    
    @Autowired
    private LoginService loginService;

    @Autowired
    private FileHttpService fileHttpService;

    @Autowired
    private HuanxinService huanxinService;

    @Autowired
    private AdminQuizService adminQuizService;

    @Autowired
    private TeacherService teacherService;
        

    /**
     * 1.用户名，密码认证 2.将用户token写入redis 3.将用户token写入Cookie,由客户端调用
     * 
     * @Author:ALong (ZengWeiLong)
     * @param bean 登陆用户
     * @param bean 登陆密码
     * @return String
     * @date 2016年5月16日
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> login(HttpServletRequest request, HttpServletResponse response,
            @RequestBody LoginDto bean) {
        try{
            List<Result> list = ValidateUtils.checkBean(bean, false);
            logger.info("参数检查"); 
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                logger.info(list.get(0).getName() + "," + list.get(0).getMessages());
                return ReturnMapUtils.returnFail(AjaxCode.USER_ERROR,"验证结果:"+list.get(0).getName() + "," + list.get(0).getMessages());
            }

            String ip = IpUtils.getIpAddress(request);

            logger.info("IP检查:{}",ip); 
            
            if (StringUtils.isBlank(ip) || passportService.isExceedMaxLoginPerIP(ip)
                    || passportService.isExceedMaxLoginFailed(bean.getEmail())) {
                if (StringUtils.isBlank(bean.getKey()) || StringUtils.isBlank(bean.getImageCode())) {
                    logger.warn("同一IP超过最大登录次数，或登录失败次数超限，需要添加验证码,userName = {}",bean.getEmail());
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    return ReturnMapUtils.returnFail(AjaxCode.VERIFY_CODE);
                } else if (!passportService.checkVerifyCode(bean.getKey(), bean.getImageCode())) {
                    logger.warn("验证码错误，key = {},imageCode = {}", bean.getKey(), bean.getImageCode());
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    return ReturnMapUtils.returnFail(AjaxCode.VERIFY_CODE_ERROR);
                }
            }
            if (StringUtils.isNotBlank(ip)) {
                passportService.addLoginCountPerIP(ip);
            }
            
            logger.info("用户检查:{}",bean.getEmail());
                        
            // 根据email，检查是否有此账号。
            User user = this.passportService.findUserByUsername(bean.getEmail());
            if (null == user) {
                //add
                passportService.addLoginFailedCount(bean.getEmail());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.USER_NULL,"账户不存在或者已经过期."+bean.getEmail());
            }
            // token check
            logger.info("User 表Token生成检查:{}",bean.getEmail());            
            if (StringUtils.isBlank(user.getToken())) {
                user = this.passportService.updateUserToken(user);
            }
            // 密码验证
            logger.info("密码检查:{}",bean.getEmail());
            SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
            String mypwd = encoder.encode(bean.getPassword());
            if (!mypwd.equals(user.getPassword())) {
                //add
                passportService.addLoginFailedCount(bean.getEmail());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.PWD_ERROR,"用户名或密码错误."+bean.getEmail());
            }
            // 检查用户类型
            logger.info("用户类型检查:{}",bean.getEmail());
            if (!UserEnum.Dtype.TEACHER.val().equals(user.getDtype())) {
                //add
                passportService.addLoginFailedCount(bean.getEmail());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.DTYPE_ERROR,"账户Dtype不合法."+bean.getEmail());
            }
            
            logger.info("Teacher表数据检查:{}",bean.getEmail());
            Teacher teacher = this.passportService.findTeacherById(user.getId());
            if (teacher == null) {
                //add
                passportService.addLoginFailedCount(bean.getEmail());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.TEACHER_NULL,"Teacher账户不存在:"+bean.getEmail());
            }
            
            // 检查老师状态是否FAIL
            logger.info("检查老师状态是否FAIL:{}",bean.getEmail());
            if (TeacherEnum.LifeCycle.FAIL.toString().equals(teacher.getLifeCycle())) {
                //add
                passportService.addLoginFailedCount(bean.getEmail());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.USER_FAIL,"账户已经被Fail:"+bean.getEmail());
            }    
            
            // 检查老师状态是否QUIT
            logger.info("检查老师状态是否QUIT:{}",bean.getEmail());
            if (TeacherEnum.LifeCycle.QUIT.toString().equals(teacher.getLifeCycle())) {
            	boolean canLogin = getQuitStatus(user);
				if (!canLogin) {
					passportService.addLoginFailedCount(bean.getEmail());
					logger.warn(bean.getEmail() + ",账户status被QUIT.");
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					return ReturnMapUtils.returnFail(AjaxCode.USER_QUIT, "账户已经Quit:" + bean.getEmail());
				}
            }
    
            // 检查用户状态是否锁住
            logger.info("检查老师状态是否Locked:{}",bean.getEmail());
            if (UserEnum.Status.isLocked(user.getStatus())) {
                // 新注册的需要激活
                if (TeacherEnum.LifeCycle.SIGNUP.toString().equals(teacher.getLifeCycle())) {
                    if(PropertyConfigurer.booleanValue("signup.send.mail.switch")){
                        //add
                        passportService.addLoginFailedCount(bean.getEmail());
                        response.setStatus(HttpStatus.BAD_REQUEST.value());
                        return ReturnMapUtils.returnFail(AjaxCode.USER_ACTIVITY,"没有激活:"+bean.getEmail());
                    }else{
                        this.passportService.updateUserStatus(user);
                    }
                } else {
                    // 否则告诉被锁定
                    //add
                    passportService.addLoginFailedCount(bean.getEmail());
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    return ReturnMapUtils.returnFail(AjaxCode.USER_LOCKED,"账户被锁:"+bean.getEmail());
                }            
            }
    
            // 如果招聘Id不存在则set进去
            logger.info("RecruitmentId 检查:{}",bean.getEmail());
            if (StringUtils.isBlank(teacher.getRecruitmentId())) {
                teacher.setRecruitmentId(this.passportService.updateRecruitmentId(teacher));
            }
    
            logger.info(bean.getEmail()+",登陆检查结果(通过)准备登陆数据，LifeCycle="+teacher.getLifeCycle());
            
            //模拟登陆logger
            Map<String,Object> result = Maps.newHashMap();
            result.put("loginToken", loginService.setLoginToken(response, user));
            result.put("LifeCycle", teacher.getLifeCycle());
            // 只有正式老师登陆后才做强制修改密码判断
			if (LifeCycle.REGULAR.toString().equalsIgnoreCase(teacher.getLifeCycle())
					|| LifeCycle.QUIT.toString().equalsIgnoreCase(teacher.getLifeCycle())) {
				logger.warn(bean.getEmail() + ",登陆状态为REGULAR");
				// loginService.changePasswordNotice(response, bean.getPassword());
				/* 设置老师能教的课程类型列表 */
				loginService.setCourseTypes(user.getId(), loginService.getCourseType(user.getId()));
			}
            logger.info(bean.getEmail()+",登陆成功，LifeCycle="+teacher.getLifeCycle());
            
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }

	private boolean getQuitStatus(User user) {
		Timestamp lastEditTime = user.getLastEditDateTime();
		Date expireTime = null;
		Long editor = user.getLastEditorId();
		Date now = new Date();
		//Quit 老师登录
		boolean canLogin = false;
		if (editor != user.getId()) {
			expireTime = DateUtils.getTheDayOfNextMonth(lastEditTime,TH_21_DAY);
		}else{
			expireTime = DateUtils.getTheDayOfNextMonth(now,TH_21_DAY);
		}				
		String exStr = passportService.getQuitTeacherExpiredTime(user.getId(), expireTime.getTime());
		if(StringUtils.isNotEmpty(exStr)){
			Long ex = Long.parseLong(exStr);
			Date exDate = new Date(ex);
			if (now.before(exDate)) {
				canLogin = true;
			}				
		}
		return canLogin;
	}
   
	

    /**
     * 注册用户注册老师
     * 
     * @Author:ALong (ZengWeiLong)
     * @param bean 注册用户
     * @param bean 注册密码
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
            return ReturnMapUtils.returnFail(AjaxCode.USER_ERROR);
        }
        String email = StringUtils.trim(bean.getEmail());
        bean.setEmail(email);
        logger.info("sign up teacher email = {" + email + "},参数:"+JsonTools.getJson(bean));
        try{
            logger.info("验证数据检查:{}",bean.getEmail());
            if (StringUtils.isBlank(bean.getKey()) || StringUtils.isBlank(bean.getImageCode())) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.VERIFY_CODE,"验证码为空或验证码key为空" + email);
            }
            
            logger.info("验证码检查:{}",bean.getEmail());
            if (!passportService.checkVerifyCode(bean.getKey(),bean.getImageCode())) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.VERIFY_CODE_ERROR,"验证码校验不通过！" + email);
            }

            //检查ReferralCode
            String referralCode = bean.getReferralCode();
            logger.info("check ReferralCode:{}",referralCode);
            if (StringUtils.isNotBlank(bean.getReferralCode())){
                long referralId = teacherService.getTeacherIdWithReferralCode(referralCode);
                bean.setRefereeId(referralId);
                User user = passportService.findUserById(referralId);
                if (null == user){
                    return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(),"Referral code does not exist, please check.");
                }
            }
            
            logger.info("账户数据检查:{}",bean.getEmail());
            if (StringUtils.isBlank(email) || StringUtils.isBlank(bean.getPassword())) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.USER_ERROR,"用户名或密码错误" + email);
            }
            
            logger.info("账户存在检查:{}",bean.getEmail());
            User user = passportService.findUserByUsername(email);
            // 1.用户名存在，反馈
            if (user != null) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.USER_EXITS,"已经存在" + email);
                // 2.用户名可用，执行业务，
            }
            
            // 执行业务逻辑
            logger.info("账户检查完毕-(通过)-正在注册:{}",bean.getEmail());
            Map<String, Object> result = passportService.saveSignUp(bean);
            if(ReturnMapUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
                logger.warn("注册失败：{}",result);
                return result;
            }
            logger.info("注册完毕数据准备:{}",bean.getEmail());
            //注册成功
            user = (User)result.get("user");
            String loginToken = loginService.setLoginToken(response, user);
            result.put("loginToken", loginToken);
            
            logger.info("返回数据:{}",JsonTools.getJson(result));
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }

    /**
     * 重置密码请求
     * 
     * @Author:ALong (ZengWeiLong)
     * @param request
     * @param response
     * @param forgetPasswordDto 重置用户
     * @return String code码
     * @date 2016年5月16日
     */
    @RequestMapping(value = "/resetPasswordRequest", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> resetPasswordRequest(HttpServletRequest request, HttpServletResponse response,
                                                    @RequestBody ForgetPasswordDto forgetPasswordDto) {
        try{
            //String email = pram.get("email");
            String email = forgetPasswordDto.getEmail();
            String key = forgetPasswordDto.getKey();
            String imageCode = forgetPasswordDto.getImageCode();
            logger.info("参数验证:{}",email);            
            if (StringUtils.isBlank(email)) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.USER_ERROR,"账户不存在."+email);
            }
            
            logger.info("账号验证:{}",email);
            //验证验证码
            if (StringUtils.isBlank(key) || StringUtils.isBlank(imageCode)) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.VERIFY_CODE);
            } else if (!passportService.checkVerifyCode(key, imageCode)) {
                logger.warn("验证码错误，key = {},imageCode = {}", forgetPasswordDto.getKey(), forgetPasswordDto.getImageCode());
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.VERIFY_CODE_ERROR);
            }
            // 根据email，检查是否有此账号。
            User user = this.passportService.findUserByUsername(email);
            if (null == user) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.USER_NULL,"账户不存在或者已经过期."+email);
            }
            // 检查用户类型
            logger.info("检查用户类型:{}",email);
            if (!UserEnum.Dtype.TEACHER.val().equals(user.getDtype())) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.DTYPE_ERROR,"账户Dtype不合法."+email);
            }
            logger.info("检查teacher数据:{}",email);
            Teacher teacher = this.passportService.findTeacherById(user.getId());
            if (teacher == null) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.TEACHER_NULL,"Teacher 不存在."+email);
            }
            
            logger.info("检查teacher是否Fail:{}",email);
            if (TeacherEnum.LifeCycle.FAIL.toString().equals(teacher.getLifeCycle())) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.USER_FAIL,"账户status被Fail."+email);
            }
    
            // 检查用户状态是否锁住
            logger.info("检查用户状态是否锁住:{}",email);
            if (UserEnum.Status.isLocked(user.getStatus())) {
                // 新注册的需要激活
                if (TeacherEnum.LifeCycle.SIGNUP.toString().equals(teacher.getLifeCycle())) {
                    if(PropertyConfigurer.booleanValue("signup.send.mail.switch")){
                        response.setStatus(HttpStatus.BAD_REQUEST.value());
                        return ReturnMapUtils.returnFail(AjaxCode.USER_ACTIVITY,"没有激活."+user.getUsername());
                    }else{
                        this.passportService.updateUserStatus(user);
                    }
                } else {
                    // 否则告诉被锁定
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    return ReturnMapUtils.returnFail(AjaxCode.USER_LOCKED,"账户被锁."+user.getUsername());
                }
            }
            // 检查老师状态是否QUIT
            logger.info("检查用户状态是否QUIT:{}",email);
            
            if (TeacherEnum.LifeCycle.QUIT.toString().equals(teacher.getLifeCycle())) {
            	boolean canLogin = getQuitStatus(user);
				if (!canLogin) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
	                return ReturnMapUtils.returnFail(AjaxCode.USER_QUIT,"账户已经Quit."+user.getUsername());
				}                
            }
            
            logger.info("重置请求检查完毕-(通过)-正在发送Email:{}", email);
            
            Map<String,Object> result = this.passportService.senEmailForPassword(user);
            
            logger.info("发送程序结束:{}",JsonTools.getJson(result));
            
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }

    /**
     * 重置密码提交
     * 
     * @Author:ALong (ZengWeiLong)
     * @param request
     * @param response
     * @param bean 新密码
     * @param bean 修改密码认证
     * @return String code码
     * @date 2016年5月16日
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> resetPassword(HttpServletRequest request, HttpServletResponse response,
            @RequestBody ResetPasswordDto bean) {
        try{
            logger.info("参数校验");
            List<Result> list = ValidateUtils.checkBean(bean, false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.USER_ERROR,"result:"+list.get(0).getName() + "," + list.get(0).getMessages());
            }
            
            logger.info("token 校验:{}",bean.getToken());
            if (!this.passportService.checkTokenTimeout(bean.getToken())) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.TOKEN_OVERDUE,"token 过期   token:" + bean.getToken());
            }
            
            logger.info("Teacher 校验:{}",bean.getToken());
            Teacher teacher = this.passportService.findByRecruitmentId(bean.getToken());
            if (teacher == null) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.TEACHER_NULL,"teacher is null:"+bean.getToken());
            }
            // 修改成功后strToken替换成最新，使原来的失效
            logger.info("校验完毕，修改密码,teacherId:{}",teacher.getId());            
            Map<String,Object> retMap = this.passportService.updatePassword(teacher, bean.getPassword());            
            
            logger.info("修改完毕-结果:{}",JsonTools.getJson(retMap));
            if (ReturnMapUtils.isFail(retMap)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return ReturnMapUtils.returnFail(AjaxCode.TOKEN_ERROR,"用户ID:"+teacher.getId());
            }
            
            logger.info("修改成功,id:{}",teacher.getId());
            
            User user = this.passportService.findUserById(teacher.getId());
            String loginToken = loginService.setLoginToken(response, user);
            retMap.put("loginToken", loginToken);
            
            logger.info("修改完毕-返回:{}",JsonTools.getJson(retMap));
            
            return retMap;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }

    @RequestMapping(value = "/auth", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    @RestInterface
    public Map<String, Object> auth(HttpServletRequest request, HttpServletResponse response) {
        try{            
            User user = this.getUser(request);
            
            logger.info(user.getUsername()+",锁定检查.");
            if(UserEnum.Status.isLocked(user.getStatus())){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.USER_LOCKED,"账户被锁."+user.getUsername());
            }
            
            // 检查老师状态是否FAIL
            logger.info(user.getUsername()+",Fail检查.");
            Teacher teacher = this.getTeacher(request);
            if (TeacherEnum.LifeCycle.FAIL.toString().equals(teacher.getLifeCycle())) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.USER_FAIL,"账户被Fail."+user.getUsername());
            }
    
            // 检查老师状态是否QUIT
            logger.info(user.getUsername()+",Quit检查.");
			if (TeacherEnum.LifeCycle.QUIT.toString().equals(teacher.getLifeCycle())) {
				boolean canLogin = getQuitStatus(user);
				if (!canLogin) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					return ReturnMapUtils.returnFail(AjaxCode.USER_QUIT, "账户被Quit,并且已经过了登录期了；" + user.getUsername());
				}
//				if (getQuitStatus() == TeacherEnum.QuitStatusEnum.EXPIRED) {
//					response.setStatus(HttpStatus.BAD_REQUEST.value());
//					return ReturnMapUtils.returnFail(AjaxCode.USER_QUIT, "账户被Quit,并且已经过了登录期了；" + user.getUsername());
//				}
			}
            
            // 检查用户类型
            logger.info(user.getUsername()+",类型检查.");
            if (!UserEnum.Dtype.TEACHER.val().equals(user.getDtype())) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(AjaxCode.DTYPE_ERROR,",账户Dtype不合法."+user.getUsername());
            }
            
            logger.info(user.getUsername()+",检查完毕-通过-.获取权限等数据");            

            TeacherInfo teacherinfo = new TeacherInfo();
            teacherinfo.setTeacherId(this.getUser(request).getId());

            //添加 ReferralCode
            String referralCode = teacherService.getReferralCode(this.getUser(request).getId());
            teacherinfo.setReferralCode(referralCode);
            //权限判断 start
            loginService.findByTeacherModule(teacherinfo, teacher.getLifeCycle());
            //其他信息
            if(0 != teacher.getManager()) {
                User teacherManager = loginService.findUserById(teacher.getManager());
                teacherinfo.setTeacherManagerInfo(teacherManager);
            }
            teacherinfo.setInfo(teacher,user);
            teacherinfo.setHeadsrc(fileHttpService.queryTeacherFiles(user.getId()).getAvatar());
            Map<String,Object> success = ReturnMapUtils.returnSuccess();
            success.putAll(Bean2Map.toMap(teacherinfo));

            boolean isNeedQuiz = adminQuizService.findNeedQuiz(teacher.getId());
            logger.info("Check if {} need to take the quiz and get result {}", teacher.getId(), isNeedQuiz);
            success.put("isNeedQuiz", isNeedQuiz);

            logger.info("返回数据:{}", JsonTools.getJson(success));

            // 设置 24 小时提示 Cookie
            CookieUtils.setCookie(response, CookieKey.TRPM_HOURS_24, String.valueOf(teacher.getId()), null);

            return success;

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }     
    
    @RequestMapping(value = "/applyActivationEmail", method = RequestMethod.POST,produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> applyActivationEmail(HttpServletRequest request,
            HttpServletResponse response, @RequestParam(required = true) String email) {
        
        if (StringUtils.isBlank(email)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail("The a user is  not exist",email);
        }
        
        Map<String,Object> result = this.loginService.sendActivationEmail(email);
        if(ReturnMapUtils.isFail(result)){
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return ReturnMapUtils.returnFail("Email send fail",email);
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


    @RequestMapping(value = "/inithuanxinid")
    public Map<String, Object> inithuanxinid(String password,String teacherId ) {
        if(StringUtils.isNotBlank(teacherId)){
            huanxinService.signUpHuanxin(teacherId,teacherId);
            return ReturnMapUtils.returnSuccess();
        }
        //全量注册环信id
        else if(StringUtils.equals(password,"allregularteacherinithuanxinid")){

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    logger.info("inithuanxinid start!");
                    List<String> list = huanxinService.findAllRegularButNoHuanxinId();
                    if(CollectionUtils.isNotEmpty(list)){
                        for(String oneId : list){
                            huanxinService.signUpHuanxin(oneId,oneId);
                            // 0.1秒钟间隔发送请求,环信有ip限流
                            LockSupport.parkNanos(TimeUnit.NANOSECONDS.convert(100, TimeUnit.MILLISECONDS));
                        }
                    }
                    logger.info("inithuanxinid end!");
                }
            });
            thread.setName("init-huanxinId-thread");
            thread.start();
            return ReturnMapUtils.returnSuccess();
        }
        else{
            return ReturnMapUtils.returnFail("no right to request");
        }

    }

    @RequestMapping(value = "/getReferralCode")
    public Map<String, Object> getReferralCode(HttpServletRequest request, HttpServletResponse response, long referralId){
        Preconditions.checkArgument(null != Long.valueOf(referralId),"referralId can not be null");
        Map<String,Object> result = Maps.newHashMap();
        try {
            String referralCode = teacherService.getReferralCode(referralId);
            result.put("referralCode",referralCode);

        }catch (Exception e){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return result;
    }

}
