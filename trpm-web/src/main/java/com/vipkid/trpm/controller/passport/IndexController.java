package com.vipkid.trpm.controller.passport;

import com.vipkid.enums.UserEnum;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.TeacherLifeCycle;
import com.vipkid.trpm.controller.AbstractController;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherPageLogin;
import com.vipkid.trpm.service.passport.IndexService;
import com.vipkid.trpm.service.passport.PassportService;
import com.vipkid.trpm.service.passport.RemberService;
import com.vipkid.trpm.service.rest.LoginService;
import com.vipkid.trpm.util.AES;
import com.vipkid.trpm.util.CookieUtils;
import org.apache.commons.lang.StringUtils;
import org.community.config.PropertyConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@PreAuthorize("permitAll")
public class IndexController extends AbstractController {

	/*@Autowired
	private IndexService indexService;
*/
	@Autowired
	private PassportService passportService;

	@Autowired
	private RemberService remberService;

	@Autowired
    private LoginService loginService;
	
	@RequestMapping("/index")
	@Deprecated
	public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
		String token = request.getParameter("uuid");
		if (StringUtils.isEmpty(token)) {
			remberService.replaceKeys(request, response);
			model.addAttribute("pageName", "Sign In");
			return "passport/sign_in";
		}

		String token_value = AES.decrypt(token, AES.getKey(AES.KEY_LENGTH_128, ApplicationConstant.AES_128_KEY));
		com.vipkid.trpm.entity.User user = this.loginService.findUserByToken(token_value);
		if (user == null || !UserEnum.Dtype.TEACHER.toString().equals(user.getDtype())) {
			model.addAttribute("pageName", "Sign In");
			return "passport/sign_in";
		}

		Teacher teacher = this.loginService.findTeacherById(user.getId());
		if (teacher == null) {
			model.addAttribute("pageName", "Sign In");
			return "passport/sign_in";
		}

		/* 判断老师的LifeCycle，进行项目跳转 */
		if (TeacherLifeCycle.REGULAR.toString().equals(teacher.getLifeCycle())) {
			loginService.setLoginCooke(response, user);
			/* 设置老师能教的课程类型列表 */
			loginService.setCourseTypes(user.getId(), loginService.getCourseType(user.getId()));
			Cookie cookie = CookieUtils.getCookie(request, "from");
			if (cookie != null && "facebook".equals(cookie.getValue())) {
				return "redirect:/activity.shtml";
			}
			return "redirect:/bookings.shtml";
		} else {
			String recruitmentUrl = PropertyConfigurer.stringValue("recruitment.www");
			model.addAttribute("token", token);
			model.addAttribute("recruitmentUrl", recruitmentUrl);
			return "passport/index";
		}
	}

	@RequestMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
		loginService.removeLoginCooke(request, response);
		return "redirect:/";
	}

	@RequestMapping("/accessDenied")
	@Deprecated
	public String accessDenied(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "template/accessDenied";
	}

	@RequestMapping("/disableLayer")
	@PreAuthorize("fullyAuthenticated")
	public String disableLayer(HttpServletRequest request, HttpServletResponse response, Model model,
			TeacherPageLogin pageLogin) {
		Teacher teacher = loginService.getTeacher();
		pageLogin.setUserId(teacher.getId());
		loginService.doDisableLayer(pageLogin);
		return jsonView();
	}

}
