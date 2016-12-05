package com.vipkid.trpm.util;

import java.util.UUID;

import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.vipkid.file.utils.FileUtils;
import com.vipkid.file.utils.StringUtils;

/**
 * 
 * aws s3 文件工具类
 * 
 * @author zouqinghua
 * @date 2016年10月17日  上午2:35:12
 *
 */
public class AwsFileUtils {

	public static Logger logger = LoggerFactory.getLogger(AwsFileUtils.class);
	public static final String TAXPAYER_FORM = "taxpayer";
	
	public static final long TAPXPAYER_FILE_MAX_SIZE = 20*1024*1024L; //20M
	public static final String TAPXPAYER_FILE_TYPE = "pdf,jpg,png,jpeg";
	
	
	/**
	 * key = /${aws.teacer.dir}/TAXPAYER_FORM/${teacherId}/uuid/fileName.xxx
	 * @param teacherId
	 * @param fileName
	 * @return
	 */
	public static String getTaxpayerkey(Long teacherId,String fileName){
		String key = null;
		try {
			String rootDir = PropertyConfigurer.stringValue("aws.teacer.dir");
			String uuid = UUID.randomUUID().toString().replace("-", "");
			key = rootDir+"/"+TAXPAYER_FORM;
			if(teacherId!=null){ //加入教师Id方便查询
				key+="/"+teacherId;
			}
			key+="/"+uuid+"/"+fileName;
			key = key.replaceAll("//", "/");
			if(key.startsWith("/")){
				key = key.substring(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
	}
	
	/*public static String getTaxpayerkey(String fileName){
		String key = null;
		try {
			String rootDir = PropertyConfigurer.stringValue("aws.teacer.dir");
			String uuid = UUID.randomUUID().toString().replace("-", "");
			key = rootDir+"/"+TAXPAYER_FORM+"/"+uuid+"/"+fileName;
			key = key.replaceAll("//", "/");
			if(key.startsWith("/")){
				key = key.substring(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
	}*/
	
	public static Boolean checkFileType(String fileName){
		Boolean flag = false;
		String fileType = FileUtils.getFileType(fileName);
		if(StringUtils.isNotBlank(fileType)){
			String allTypes = ","+TAPXPAYER_FILE_TYPE+",";
			if(allTypes.contains(","+fileType+",")){
				flag = true;
			}
		}
		
		return flag;
	}
	
	public static Boolean checkFileSize(Long size){
		Boolean flag = false;
		if(size!=null && size<= TAPXPAYER_FILE_MAX_SIZE){
			flag = true;
		}
		return flag;
	}
	
}
