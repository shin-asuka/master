package com.vipkid.trpm.controller.passport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vipkid.enums.UserEnum;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.controller.AbstractController;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.passport.EmailService;
import com.vipkid.trpm.service.passport.IndexService;
import com.vipkid.trpm.service.passport.PassportService;
import com.vipkid.trpm.util.IpUtils;

@Controller
public class EmailController extends AbstractController {
    
    private Logger log = LoggerFactory.getLogger(EmailController.class);

	@Autowired
	private PassportService passportService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private IndexService indexService;

	@Deprecated
	@RequestMapping("/sendemail")
	public String inPage(HttpServletRequest request, Integer userId) {
		if (userId != null && indexService.getUser(request).getId() == userId) {
		    log.info("ID为："+userId + "的老师从IP为:【"+ IpUtils.getRemoteIP()+"】的客户端进入邮件发送页面!");
			return "passport/email";
		}
		return "passport/sign_in";
	}

	@Deprecated
	@RequestMapping("/searchEmail")
	public String searchEmail(HttpServletRequest request, HttpServletResponse response, Model model, String email,
			String type) {
		// 根据email，检查是否有此账号。
		User user = this.passportService.findUserByUsername(email);
		if (null == user) {
			model.addAttribute("info", ApplicationConstant.AjaxCode.ERROR_CODE);
			return jsonView(response, model.asMap());
		}
		// 检查用户类型
		if (!UserEnum.Dtype.TEACHER.toString().equals(user.getDtype())) {
			model.addAttribute("info", ApplicationConstant.AjaxCode.TYPE_CODE);
			return jsonView(response, model.asMap());
		}

		Teacher teacher = this.passportService.findTeacherById(user.getId());
		if (teacher == null) {
			model.addAttribute("info", ApplicationConstant.AjaxCode.ERROR_CODE);
			return jsonView(response, model.asMap());
		} else {
		    log.info("用户名为："+indexService.getUser(request).getUsername() + "的老师从IP为:【"+ IpUtils.getRemoteIP()+"】的客户端，给老师【"+user.getUsername()+"】发送类型是:【"+type+"】的邮件！");
			model.addAllAttributes(this.emailService.senEmail(user, teacher, type));
			return jsonView(response, model.asMap());
		}
	}

}