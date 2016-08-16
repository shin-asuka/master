package com.vipkid.trpm.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SHA256PasswordEncoder implements PasswordEncoder {

	private static final Logger logger = LoggerFactory.getLogger(SHA256PasswordEncoder.class);

	private static final String SHA256 = "SHA-256";

	@Override
	public String encode(CharSequence rawPassword) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(SHA256);
			byte[] bytes = messageDigest.digest(rawPassword.toString().getBytes());
			String hexString = Hex.encodeHexString(bytes);
			return Base64.encodeBase64URLSafeString(hexString.getBytes());
		} catch (NoSuchAlgorithmException e) {
			logger.error("SHA256PasswordEncoder encode err: {}", e.getMessage());
			return (null == rawPassword) ? null : rawPassword.toString();
		}
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String rawPasswordEncoded = this.encode(rawPassword);

		logger.info("SHA256PasswordEncoder matches rawPasswordEncoded: {},encodedPassword: {}",
				rawPasswordEncoded, encodedPassword);

		return encodedPassword.equals(rawPasswordEncoded);
	}

}
