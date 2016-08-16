package com.vipkid.trpm.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.vipkid.trpm.entity.personal.ChangePassword;

@Component
public class ChangePasswordValidator implements Validator {

	public static final int USERNAME_MIN_SIZE = 6;
	public static final String USERNAME_PATTERN = "^\\w+$";

	public boolean supports(Class<?> clazz) {
		return ChangePassword.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		ChangePassword changePassword = (ChangePassword) target;
		
		// 验证密码格式
		String newPassword = changePassword.getUserpassword();
		if (null == newPassword || newPassword.length() < USERNAME_MIN_SIZE
				|| !newPassword.matches(USERNAME_PATTERN)) {
			errors.rejectValue("newPassword", "newPassword.pattern");
		}
	}

}
