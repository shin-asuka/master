package com.vipkid.trpm.controller.passport;

import com.google.api.client.util.Lists;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.UserEnum;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.controller.AbstractController;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.security.SHA256PasswordEncoder;
import com.vipkid.trpm.service.passport.IndexService;
import com.vipkid.trpm.service.passport.PassportService;
import com.vipkid.trpm.service.passport.RemberService;
import com.vipkid.trpm.util.AES;
import com.vipkid.trpm.util.CookieUtils;
import com.vipkid.trpm.util.IpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

@Controller
@PreAuthorize("permitAll")
public class PassportController extends AbstractController {

	private static Logger logger = LoggerFactory.getLogger(PassportController.class);
	public static final String SIGN_WHITE_IP = PropertyConfigurer.stringValue("sign.in.Action.ip");
	@Resource
    SHA256PasswordEncoder sha256Encoder;

	@Autowired
	private PassportService passportService;

	@Autowired
	private IndexService indexService;

	@Autowired
	private RemberService remberService;

	@Autowired
    private LoginService loginService;

	/**
	 * 登陆入口
	 *
	 * @Author:ALong (ZengWeiLong)
	 * @param request
	 * @param response
	 * @param model
	 * @param strEmail
	 * @param strPwd
	 * @return String
	 * @date 2016年3月3日
	 */
	@RequestMapping("/signInAction")
	@Deprecated
	public String signInAction(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("email") String strEmail, @RequestParam("passwd") String strPwd,
			@RequestParam("remember") boolean remember) {
		// 对用户名进行解密
		String whiteIp = SIGN_WHITE_IP;
		if(StringUtils.isNotBlank(whiteIp)) {
			String[] ips = whiteIp.split(",");
			ArrayList<String> ipList = Lists.newArrayList();
			CollectionUtils.addAll(ipList, ips);
			if (!ipList.contains(IpUtils.getRemoteIP())) {
				logger.error(" User ip :{} is  Illegal", IpUtils.getRemoteIP());
				model.addAttribute("info", ApplicationConstant.AjaxCode.USER_ERROR);
				return jsonView(response, model.asMap());
			}
		}else{
			logger.error(" whiteIp : {} is Null ",whiteIp);
			model.addAttribute("info", ApplicationConstant.AjaxCode.USER_ERROR);
			return jsonView(response, model.asMap());
		}
		String _strEmail = new String(Base64.getDecoder().decode(strEmail));
		logger.info(" 请求参数 email ： " + _strEmail + ";password=" + strPwd + ",IP:" + IpUtils.getRemoteIP());
		User user = passportService.findUserByUsername(_strEmail);
		// 密码解密
		String _strPwd = new String(Base64.getDecoder().decode(strPwd));
		logger.info(" user checking " + _strEmail + ";password=" + _strPwd);
		// 根据email，检查是否有此账号。
		if (null == user) {
			logger.error(" User is Null " + _strEmail + ";password=" + _strPwd);
			model.addAttribute("info", ApplicationConstant.AjaxCode.USER_NULL);
			return jsonView(response, model.asMap());
		}
		if (StringUtils.isEmpty(user.getToken())) {
			user = this.passportService.updateUserToken(user);
		}

		logger.info("teacher null start!");
		Teacher teacher = this.passportService.findTeacherById(user.getId());
		if (teacher == null) {
			logger.error(" Username teacher error!" + _strEmail + ";password=" + _strPwd);
			model.addAttribute("info", ApplicationConstant.AjaxCode.TEACHER_NULL);
			return jsonView(response, model.asMap());
		}

		logger.info("登陆  FAIL start !");
		// 检查老师状态是否FAIL
		if (TeacherEnum.LifeCycle.FAIL.toString().equals(teacher.getLifeCycle())) {
			logger.error(" Username fail error!" + _strEmail + ";password=" + _strPwd);
			model.addAttribute("info", ApplicationConstant.AjaxCode.USER_FAIL);
			return jsonView(response, model.asMap());
		}

		logger.info("登陆  QUIT start !");
		// 检查老师状态是否QUIT
		if (TeacherEnum.LifeCycle.QUIT.toString().equals(teacher.getLifeCycle())) {
			logger.error(" Username fail error!" + _strEmail + ";password=" + _strPwd);
			model.addAttribute("info", ApplicationConstant.AjaxCode.USER_QUIT);
			return jsonView(response, model.asMap());
		}

		// 检查用户状态是否锁住
		logger.info("登陆  isLocked start !");
		if (UserEnum.Status.isLocked(user.getStatus())) {
			// 新注册的需要激活
			if (TeacherEnum.LifeCycle.SIGNUP.toString().equals(teacher.getLifeCycle())) {
				logger.error(" Username 没有激活 error!" + _strEmail + ";password=" + _strPwd);
				model.addAttribute("info", ApplicationConstant.AjaxCode.USER_ACTIVITY);
				return jsonView(response, model.asMap());
			} else {
				// 否则告诉被锁定
				logger.error(" Username locked error!" + _strEmail + ";password=" + _strPwd);
				model.addAttribute("info", ApplicationConstant.AjaxCode.USER_LOCKED);
				return jsonView(response, model.asMap());
			}
		}

		// 只有教师端老师登陆后才做强制修改密码判断
		logger.info("登陆  REGULAR start !");

		// 如果招聘Id不存在则set进去
		if (StringUtils.isEmpty(teacher.getRecruitmentId())) {
			teacher.setRecruitmentId(this.passportService.updateRecruitmentId(teacher));
		}
		model.addAttribute("loginToken", loginService.setLoginToken(response, user));
		model.addAttribute("info", "success-pass");
		model.addAttribute("uuid",
				AES.encrypt(user.getToken(), AES.getKey(AES.KEY_LENGTH_128, ApplicationConstant.AES_128_KEY)));

		logger.info("登陆  remember start !");
		// 对remberMe的处理
		//remberService.remberMe(remember, _remberme, request, response, user);

		// 写入24小时提示Cookie
		CookieUtils.setCookie(response, CookieKey.TRPM_HOURS_24, String.valueOf(user.getId()), null);

		logger.info("checked end !");
		// 处理跳转的页面
		return jsonView(response, model.asMap());
	}

	/**
	 * 注册页面进入
	 *
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/signUp")
	@Deprecated
	public String signUp(HttpServletRequest request, HttpServletResponse response, Model model) {
		// 推荐人
		if (!StringUtils.isEmpty(request.getParameter(ApplicationConstant.REFEREEID))) {
			CookieUtils.setCookie(response, ApplicationConstant.REFEREEID,
                    request.getParameter(ApplicationConstant.REFEREEID), null);
		}
		if (!StringUtils.isEmpty(request.getParameter(ApplicationConstant.PARTNERID))) {
			CookieUtils.setCookie(response, ApplicationConstant.PARTNERID,
                    request.getParameter(ApplicationConstant.PARTNERID), null);
		}

		model.addAttribute("tosignin", "button");
		model.addAttribute("pageName", "Sign Up");
		return "passport/sign_up";
	}

	/**
	 * 注册逻辑实现
	 *
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/signUpAction")
	@Deprecated
	public String signUpAction(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("email") String email, @RequestParam("privateCode") String privateCode) {
		logger.info("sign up teacher email = {" + email + "," + new String(Base64.getDecoder().decode(privateCode))
				+ "}");
		User user = passportService.findUserByUsername(email);
		// 1.用户名存在，反馈
		if (user != null) {
			model.addAttribute("info", ApplicationConstant.AjaxCode.USER_EXITS);
			// 2.用户名可用，执行业务，
		} else {
			// 执行业务逻辑
			Object reid = CookieUtils.getValue(request, ApplicationConstant.REFEREEID);
			Object partnerid = CookieUtils.getValue(request, ApplicationConstant.PARTNERID);
			model.addAllAttributes(passportService.saveSignUp(email, privateCode, Long.valueOf(reid+""),  Long.valueOf(partnerid+"")));
			CookieUtils.removeCookie(response, ApplicationConstant.REFEREEID, null, null);
		}
		return jsonView(response, model.asMap());
	}

	@Deprecated
	@RequestMapping("/activation")
	public String userActivation(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("uuid") String privateCode) throws IOException {
		if (!StringUtils.isEmpty(privateCode)) {
			String result = this.passportService.userActivation(privateCode);
			if (!StringUtils.isEmpty(result)) {
				logger.info("User execute Activation link for Email 。。。");
			}
		}
		return "redirect:/index.shtml";
	}

	/**
	 * 重置密码的请求页面进入
	 *
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/resetPassword")
	@Deprecated
	public String resetPassword(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("tosignin", "button");
		model.addAttribute("pageName", "Password Reset Center");
		return "passport/reset_password";
	}

	/**
	 * 重置密码的响应操作： 发送重置密码邮件，邮件中带有重置密码的url链接 --> modify password
	 *
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/resetPasswordAction")
	@Deprecated
	public String resetPasswordAction(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("email") String strEmail) {
		// 根据email，检查是否有此账号。
		User user = this.passportService.findUserByUsername(strEmail);
		if (null == user) {
			model.addAttribute("info", ApplicationConstant.AjaxCode.USER_NULL);
			return jsonView(response, model.asMap());
		}
		// 检查用户类型
		if (!UserEnum.Dtype.TEACHER.val().equals(user.getDtype())) {
			model.addAttribute("info", ApplicationConstant.AjaxCode.DTYPE_ERROR);
			return jsonView(response, model.asMap());
		}

		Teacher teacher = this.passportService.findTeacherById(user.getId());
		if (teacher == null) {
			model.addAttribute("info", ApplicationConstant.AjaxCode.TEACHER_NULL);
			return jsonView(response, model.asMap());
		}
		// 检查老师状态是否FAIL
		if (TeacherEnum.LifeCycle.FAIL.toString().equals(teacher.getLifeCycle())) {
			model.addAttribute("info", ApplicationConstant.AjaxCode.USER_FAIL);
			return jsonView(response, model.asMap());
		}

		// 检查用户状态是否锁住
		if (UserEnum.Status.isLocked(user.getStatus())) {
			// 新注册的需要激活
			if (TeacherEnum.LifeCycle.SIGNUP.toString().equals(teacher.getLifeCycle())) {
				model.addAttribute("info", ApplicationConstant.AjaxCode.USER_ACTIVITY);
				return jsonView(response, model.asMap());
			} else {
				// 否则告诉被锁定
				model.addAttribute("info", ApplicationConstant.AjaxCode.USER_LOCKED);
				return jsonView(response, model.asMap());
			}
		}
		// 检查老师状态是否QUIT
		if (TeacherEnum.LifeCycle.QUIT.toString().equals(teacher.getLifeCycle())) {
			model.addAttribute("info", ApplicationConstant.AjaxCode.USER_QUIT);
			return jsonView(response, model.asMap());
		} else {
			model.addAllAttributes(this.passportService.senEmailForPassword(user));
			return jsonView(response, model.asMap());
		}
	}

	/**
	 * 重置密码进入 从邮箱进入
	 *
	 * @Author:ALong (ZengWeiLong)
	 * @param request
	 * @param response
	 * @param model
	 * @param strToken
	 * @return String
	 * @date 2016年3月3日
	 */
	@RequestMapping("/modifyPassword")
	@Deprecated
	public String modifyPassword(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("validate_token") String strToken) {
		if (StringUtils.isEmpty("strToken")) {
			return "redirect:/index.shtml";
		}
		Teacher teacher = this.passportService.findByRecruitmentId(strToken);
		if (teacher == null) {
			return "redirect:/index.shtml";
		}
		model.addAttribute("pageName", "Password Reset Center");
		model.addAttribute("validate_token", teacher.getRecruitmentId());
		return "passport/modify_password";
	}

	/**
	 * 执行密码修改
	 *
	 * @Author:ALong (ZengWeiLong)
	 * @param request
	 * @param response
	 * @param model
	 * @param strToken
	 * @return String
	 * @date 2016年3月3日
	 */
	@RequestMapping("/modifyPasswordAction")
	@Deprecated
	public String modifyPasswordAction(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("privateCode") String password, @RequestParam("strToken") String strToken) {
		// 修改成功后strToken替换成最新
		if (StringUtils.isEmpty(strToken)) {
		    model.addAttribute("info", ApplicationConstant.AjaxCode.TOKEN_ERROR);
			return jsonView(response, model.asMap());
		}
		if (!this.passportService.checkTokenTimeout(strToken)) {
		    model.addAttribute("info", ApplicationConstant.AjaxCode.TOKEN_OVERDUE);
			return jsonView(response, model.asMap());
		}
		Teacher teacher = this.passportService.findByRecruitmentId(strToken);
		if (teacher == null) {
		    model.addAttribute("info", ApplicationConstant.AjaxCode.TEACHER_NULL);
			return jsonView(response, model.asMap());
		}
		Map<String,Object> retMap = this.passportService.updatePassword(teacher, password);
		if (ReturnMapUtils.isFail(retMap)) {
			return jsonView(response, model.asMap());
		}
		model.addAttribute("lifeCycle", teacher.getLifeCycle());
		String webappPath = PropertyConfigurer.stringValue("recruitment.www");
		if (TeacherEnum.LifeCycle.REGULAR.toString().equals(teacher.getLifeCycle())) {
			webappPath = PropertyConfigurer.stringValue("teacher.www");
		}
		model.addAttribute("url", webappPath + "signlogin.shtml?token=" + strToken);
		model.addAttribute("info", "OK");
		return jsonView(response, model.asMap());
	}

	/**
	 * 用户注册成功
	 *
	 * @Author:ALong (ZengWeiLong)
	 * @param request
	 * @param response
	 * @param model
	 * @param strToken
	 * @return String
	 * @date 2016年3月3日
	 */
	@RequestMapping("/signupSuccess")
	@Deprecated
	public String signupSuccess(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("uuid") String strToken) {
		if (StringUtils.isEmpty(strToken)) {
			return "redirect:/index.shtml";
		}
		try {
			strToken = AES.decrypt(strToken, AES.getKey(AES.KEY_LENGTH_128, ApplicationConstant.AES_128_KEY));
		} catch (Exception e) {
			return "redirect:/index.shtml";
		}
		Teacher teacher = this.passportService.findByRecruitmentId(strToken);
		if (teacher == null) {
			return "redirect:/index.shtml";
		}
		model.addAttribute("tosignout", "button");
		model.addAttribute("pageName", "Sign Up");
		return "passport/sign_success";
	}

	@Deprecated
	@RequestMapping("/signlogin")
	public String signlogin(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		String token = ServletRequestUtils.getStringParameter(request, "token", null);
		token = AES.decrypt(token, AES.getKey(AES.KEY_LENGTH_128, ApplicationConstant.AES_128_KEY));
		User user = this.passportService.findByToken(token);
		if (user != null) {
			Teacher teacher = this.passportService.findTeacherById(user.getId());
			if (teacher != null && TeacherEnum.LifeCycle.REGULAR.toString().equals(teacher.getLifeCycle())) {
				this.passportService.updateRecruitmentId(teacher);

				indexService.setLoginCooke(response, user);
				/* 设置老师能教的课程类型列表 */
				indexService.setCourseTypes(user.getId(), indexService.getCourseType(user.getId()));

				return "redirect:/bookings.shtml";
			}
		}
		return "redirect:/index.shtml";
	}

	@RequestMapping("/privacy")
	@Deprecated
	public String privacy(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("tosignin", "button");
		return "passport/privacy";
	}
}
