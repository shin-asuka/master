package com.vipkid.trpm.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES工具 
 * @ClassName: AES 
 * @date 2016年3月18日 下午1:39:42 
 *
 */
public class AES {

	/**
	 * 算法
	 */
	private static final String ALGORITHM = "AES";

	/**
	 * 加密解密算法/工作模式/填充方式JAVA6 支持PKCS5PADDING填充方式 Bouncy castle支持PKCS7Padding填充方式
	 */
	private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

	/**
	 * 字符集编码
	 */
	private static final String CHARSET = "UTF-8";

	/**
	 * 128加密KEY长度
	 */
	public static final int KEY_LENGTH_128 = 128;
	
	/**
	 * 192加密KEY长度
	 */
	public static final int KEY_LENGTH_192 = 192;
	
	/**
	 * 256加密KEY长度
	 */
	public static final int KEY_LENGTH_256 = 256;

	/**
	 * 生成密钥，java6只支持56位密钥，Bouncy castle支持64位密钥
	 * 
	 * @param keyLength
	 *            AES密钥长度
	 * @return byte[] 二进制密钥
	 */
	public static byte[] getKey(int keyLength) {
		try {
			// 实例化密钥生成器
			KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);

			// 初始化密钥生成器，AES要求密钥长度为128位、192位、256位
			keyGenerator.init(keyLength);

			// 生成密钥
			SecretKey secretKey = keyGenerator.generateKey();

			// 获取二进制密钥编码形式
			return secretKey.getEncoded();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 生成自定义密钥
	 * 
	 * @param keyLength
	 *            AES密钥长度
	 * @param key
	 *            自定义KEY字符串
	 * @return byte[] 二进制密钥
	 */
	public static byte[] getKey(int keyLength, String key) {
		try {
			
			// 实例化密钥生成器
			KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(key.getBytes(CHARSET));

			// 初始化密钥生成器
			keyGenerator.init(keyLength, secureRandom);

			// 生成密钥
			SecretKey secretKey = keyGenerator.generateKey();

			// 获取二进制密钥编码形式
			return secretKey.getEncoded();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 转换密钥
	 * 
	 * @param keyBytes
	 *            二进制密钥
	 * @return SecretKey 密钥
	 */
	private static SecretKey toSecretKey(byte[] keyBytes) {
		return new SecretKeySpec(keyBytes, ALGORITHM);
	}

	/**
	 * 加密数据
	 * 
	 * @param data
	 *            待加密数据
	 * @param keyBytes
	 *            密钥字节数组
	 * @return String 加密后的数据
	 */
	public static String encrypt(String data, byte[] keyBytes) {
		try {
			
			// 实例化 使用 PKCS7PADDING 填充方式，按如下方式实现,就是调用Bouncy
			// castle组件实现Cipher.getInstance(CIPHER_ALGORITHM, "BC")
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

			// 初始化，设置为加密模式
			cipher.init(Cipher.ENCRYPT_MODE, toSecretKey(keyBytes));

			// 执行操作
			byte[] dataBytes = cipher.doFinal(data.getBytes(CHARSET));
			// return Base64.encodeBase64String(dataBytes);
			return toHex(dataBytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 解密数据
	 * 
	 * @param data
	 *            待解密数据
	 * @param keyBytes
	 *            密钥字节数组
	 * @return String 解密后的数据
	 */
	public static String decrypt(String data, byte[] keyBytes) {
		try {
			
			// 实例化 使用 PKCS7PADDING 填充方式，按如下方式实现,就是调用Bouncy
			// castle组件实现Cipher.getInstance(CIPHER_ALGORITHM, "BC")
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

			// 初始化，设置为解密模式
			cipher.init(Cipher.DECRYPT_MODE, toSecretKey(keyBytes));

			// 执行操作
			// byte[] dataBytes = Base64.decodeBase64(data);
			byte[] dataBytes = fromHex(data);
			return new String(cipher.doFinal(dataBytes));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 二进制字节转化为十六进制
	 * 
	 * @param bytes
	 * @return String
	 */
	public static String toHex(byte[] bytes) {
		StringBuffer sbufHex = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				sbufHex.append("0");
			}

			sbufHex.append(Long.toString((int) bytes[i] & 0xff, 16));
		}

		return sbufHex.toString();
	}

	/**
	 * 十六进制转为二制进字节
	 * 
	 * @param hexString
	 * @return byte[]
	 */
	public static byte[] fromHex(String hexString) {
		if (hexString.length() < 1) {
			return null;
		}

		byte[] bytes = new byte[hexString.length() / 2];
		for (int i = 0; i < hexString.length() / 2; i++) {
			int high = Integer.parseInt(hexString.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexString.substring(i * 2 + 1, i * 2 + 2), 16);

			bytes[i] = (byte) (high * 16 + low);
		}

		return bytes;
	}
	
}