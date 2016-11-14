package com.vipkid.utils;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 * @author John
 *
 */
public class StringUtil {
	
	private static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}
	/**
	 * check whether the input string is constructed with only Chinese Charactor
	 * @param chinese
	 */
	public static boolean isChinese(String chinese) {
		for (char c : chinese.toCharArray()) {
			if (!isChinese(c)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param english
	 * @return
	 */
	public static boolean isEnglish(String english) {
		return english.matches("^[a-zA-Z]*$");
	}
	
	/**
	 * check whether the string is a correct email address. 
	 * @param email
	 */
	public static boolean isEmailAddress(String email) {
		return email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	}
	
	/**
	 * check whether the string is a correct mobile number
	 */
	public static boolean isMobileNumber(String mobile) {
		if (mobile == null || mobile.length() != 11) return false;
		//Pattern pattern = Pattern.compile("(?<!\\d)(?:(?:1[358]\\d{9})|(?:861[358]\\d{9}))(?!\\d)");
		if (mobile.matches("^(?<!\\d)(?:(?:1[358]\\d{9})|(?:861[358]\\d{9})|(?:17[06]\\d{8})|(?:8617[06]\\d{8}))(?!\\d)$")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 根据给定正则表达式的匹配拆分此字符串到一个字符串List列表
	 * 
	 * @param str
	 *            字符串参数
	 * @param regex
	 *            定界正则表达式
	 * @return 字符串List列表
	 */
	public static List<String> splitToList(String str, String regex) {
		String[] strArry = str.split(regex);

		List<String> strList = new ArrayList<String>();
		for (String s : strArry) {
			strList.add(s);
		}

		return strList;
	}

	/**
	 * 根据给定正则表达式的匹配拆分此字符串到一个字符串Map集合
	 * 
	 * @param str
	 *            字符串参数
	 * @param regex
	 *            定界正则表达式
	 * @return 字符串Map集合
	 */
	public static Map<String, String> splitToMap(String str, String regex) {
		String[] strarry = str.split(regex, 2);

		Map<String, String> strmap = new HashMap<String, String>();
		if (null != strarry && strarry.length > 1) {
			strmap.put(strarry[0], strarry[1]);
		}

		return strmap;
	}

	/**
	 * 根据给定的正则表达式匹配字符串一次
	 * 
	 * @param input
	 *            待匹配的字符串
	 * @param regex
	 *            正则表达式
	 * @param flags
	 *            正则表达式的flags
	 * @param groupCount
	 *            指定捕获组
	 * @return 匹配的子字符串
	 */
	public static String matchString(String input, String regex, int flags, int groupCount) {
		Matcher matcher = Pattern.compile(regex, flags).matcher(input);
		while (matcher.find()) {
			if (groupCount <= matcher.groupCount()) {
				return matcher.group(groupCount);
			}
		}

		return null;
	}

	/**
	 * 根据给定的正则表达式匹配字符串多次
	 * 
	 * @param input
	 *            待匹配的字符串
	 * @param regex
	 *            正则表达式
	 * @param flags
	 *            正则表达式的flags
	 * @param groupCount
	 *            指定捕获组
	 * @return 匹配的子字符串List
	 */
	public static List<String> matchStrings(String input, String regex, int flags, int groupCount) {
		List<String> matchs = new ArrayList<String>();

		Matcher matcher = Pattern.compile(regex, flags).matcher(input);
		while (matcher.find()) {
			if (groupCount <= matcher.groupCount()) {
				matchs.add(matcher.group(groupCount));
			}
		}

		return matchs;
	}

	/**
	 * 根据给定的正则表达式匹配字符串多次，不区分大小写
	 * 
	 * @param input
	 *            待匹配的字符串
	 * @param regex
	 *            正则表达式
	 * @return 匹配的子字符串List
	 */
	public static List<String> matchStrings(String input, String regex) {
		return matchStrings(input, regex, Pattern.CASE_INSENSITIVE, 0);
	}

	/**
	 * 根据给定的正则表达式匹配字符串一次，不区分大小写
	 * 
	 * @param input
	 *            待匹配的字符串
	 * @param regex
	 *            正则表达式
	 * @return 匹配的子字符串
	 */
	public static String matchString(String input, String regex) {
		return matchString(input, regex, Pattern.CASE_INSENSITIVE, 0);
	}

	/**
	 * 匹配指定的JSONP格式字符串中的JSON格式子字符串
	 * 
	 * @param jsonp
	 *            JSONP字符串
	 * @return JSON子字符串
	 */
	public static String matchJsonString(String jsonp) {
		return matchString(jsonp, "\\{[\\s\\S]*(?=\\))");
	}

	public static boolean isNull(String input) {
		return (null == input) ? true : false;
	}

	public static boolean isEmpty(String input) {
		Matcher matcher = Pattern.compile("^\\s*$").matcher(input);
		return matcher.matches();
	}

	public static boolean isNullOrEmpty(String input) {
		return isNull(input) || isEmpty(input);
	}

	public static boolean isInt(String input) {
		Matcher matcher = Pattern.compile("^-?\\d+$").matcher(input);
		return matcher.matches();
	}

	public static boolean isFloat(String input) {
		Matcher matcher = Pattern.compile("^(-?\\d+)(\\.\\d+)?$").matcher(input);
		return matcher.matches();
	}

	/**
	 * 字符串首字母大写
	 * 
	 * @param str
	 *            字符串参数
	 * @return 首字母大写的字符串
	 */
	public static String firstLetterUpper(String str) {
		char[] chars = str.toCharArray();

		StringBuffer newstr = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			if (0 == i) {
				newstr.append(String.valueOf(chars[i]).toUpperCase());
			} else {
				newstr.append(String.valueOf(chars[i]));
			}
		}

		return newstr.toString();
	}
	
	public static String firstUpper(String str) {
		char[] chars = str.toCharArray();

		StringBuffer newstr = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			if (0 == i) {
				newstr.append(String.valueOf(chars[i]).toUpperCase());
			} else {
				newstr.append(String.valueOf(chars[i]).toLowerCase());
			}
		}
		return newstr.toString();
	}

	public static String toLowerCase(String str){
		return str.toLowerCase();
	}
	
	/**
	 * 字符串首字母小写
	 * 
	 * @param str
	 *            字符串参数
	 * @return 首字母小写的字符串
	 */
	public static String firstLetterLower(String str) {
		char[] chars = str.toCharArray();

		StringBuffer newstr = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			if (0 == i) {
				newstr.append(String.valueOf(chars[i]).toLowerCase());
			} else {
				newstr.append(String.valueOf(chars[i]));
			}
		}

		return newstr.toString();
	}

	/**
	 * 连接多个字符串
	 * 
	 * @param strings
	 *            字符串参数列表
	 * @return 连接后的字符串
	 */
	public static String concat(String... strings) {
		StringBuffer content = new StringBuffer();
		for (String str : strings) {
			content.append(str);
		}

		return content.toString();
	}

	/**
	 * 使字符串在一行显示
	 * 
	 * @param str
	 *            待处理字符串
	 * @return 在一行显示的字符串
	 */
	public static String toInline(String str) {
		if (isNullOrEmpty(str)) {
			return str;
		}

		return str.replaceAll("\\s*[\r|\n]\\s*", "");
	}

	/**
	 * 生成字符串的哈希码
	 * 
	 * @param str
	 * @return 哈希码
	 */
	public static int hashCode(String... str) {
		return new HashCodeBuilder(11, 31).append(str).toHashCode();
	}

	/**
	 * 读取输入流到字符串
	 * 
	 * @param inputStream
	 * @param charSet
	 * @return
	 */
	public static String readContent(InputStream inputStream, String charSet) {
		StringBuffer content = new StringBuffer();
		BufferedReader bufferedReader = null;

		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charSet));
			String line = bufferedReader.readLine();

			while (null != line) {
				content.append(line);
				line = bufferedReader.readLine();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (null != bufferedReader) {
					bufferedReader.close();
				}
				if (null != inputStream) {
					inputStream.close();
				}
			} catch (Exception ex) {
			}
		}

		return content.toString();
	}
	
	/**
	 * @param parentZipcode
	 * @return
	 */
	public static boolean isZipcode(String parentZipcode) {
		if (parentZipcode == null) return false;
		
		return parentZipcode.matches("^[1-9][0-9]{5}$");
	}

}
