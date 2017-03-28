package com.vipkid.trpm.util;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.vipkid.email.template.TemplateUtils;

public class FileTest {

	public static void main(String[] args) {
		InputStream is = TemplateUtils.class.getClassLoader().getResourceAsStream("data"+File.separator +"share"+File.separator +"exam-version.json");
		//InputStream inputStream = FileTest.class.getResourceAsStream("data"+File.separator +"share"+File.separator +"exam-version.json");
		String contentJson = FilesUtils.readContent(is, Charset.defaultCharset());
		System.out.println("读取到：" + contentJson);
		String json = TemplateUtils.readTemplatePath("data"+File.separator +"share"+File.separator +"exam-version.json").toString();
    	System.out.println("读取到：" + json);
	}
}
