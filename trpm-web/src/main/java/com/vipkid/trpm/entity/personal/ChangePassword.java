package com.vipkid.trpm.entity.personal;

import java.util.Base64;

public class ChangePassword {

	private String originalPassword;

	private String userpassword;

	private String repassword;

	public String getOriginalPassword() {
		return originalPassword;
	}

	public void setOriginalPassword(String originalPassword) {
		this.originalPassword = originalPassword;
	}

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = new String(Base64.getDecoder().decode(userpassword.getBytes()));
    }

    public String getRepassword() {
        return repassword;
    }

    public void setRepassword(String repassword) {
        this.repassword = new String(Base64.getDecoder().decode(repassword.getBytes()));
    }
}
