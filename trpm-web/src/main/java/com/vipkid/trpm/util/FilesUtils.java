package com.vipkid.trpm.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesUtils {

	private static Logger logger = LoggerFactory.getLogger(FilesUtils.class);

	/**
	 * 读取文件内容
	 * 
	 * @Author:ALong
	 * @param templeteName
	 *            文件名称
	 * @return 2015年11月5日
	 */
	public static String readContent(InputStream inputStream, Charset charSet) {
		StringBuilder result = new StringBuilder();
		BufferedReader bufferedReader = null;

		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charSet));// 构造一个BufferedReader类来读取文件

			String line = null;
			while ((line = bufferedReader.readLine()) != null) {// 使用readLine方法，一次读一行
				result.append(line);
			}

			bufferedReader.close();
			inputStream.close();
		} catch (Exception e) {
			logger.error("Read file stream error.", e);
		} finally {
			try {
				if (null != bufferedReader) {
					bufferedReader.close();
				}
				if (null != inputStream) {
					inputStream.close();
				}
			} catch (Exception ex) {
				logger.error("Close file stream error.", ex);
			}
		}

		return result.toString();
	}

	/**
	 * 日志模板操作
	 * 
	 * @Author:ALong
	 * @param name
	 *            模板
	 * @param parmMap
	 *            参数集合 key value
	 * @date 2015年12月22日
	 */
	public static String readLogTemplete(String name, Map<String, Object> parmMap) {
		String content = PropertyConfigurer.stringValue(name);
		checkArgument(null != content, "The content is null.");

		for (String key : parmMap.keySet()) {
			content = content.replace("%" + key + "%", String.valueOf(parmMap.get(key)));
		}

		return content;
	}

}
