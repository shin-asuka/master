package com.vipkid.trpm.util;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.vipkid.file.utils.Encodes;
import com.vipkid.neo.utils.DateTimeUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.security.access.method.P;

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

	public static final String TAXPAYER_FORM = "tpinfo"; // W9 form
	public static final String TEACHER_AVATAR = "avatar";
	public static final String TEACHER_LIFE_PICTURE = "picture";
	public static final String TEACHER_SHORT_VIDEO = "video";
	
	public static final Long TAPXPAYER_FILE_MAX_SIZE = 20*1024*1024L; //20M
	public static final String TAPXPAYER_FILE_TYPE = "pdf,jpg,png,jpeg";

	public static final Long AVATAR_MAX_SIZE = 20*1024*1024L; //20M
	public static final String AVATAR_FILE_TYPE = "jpg,png,jpeg";

	public static final Long LIFE_PICTURE_MAX_SIZE = 20*1024*1024L; //20M
	public static final String LIFE_PICTURE_FILE_TYPE = "jpg,png,jpeg";

	public static final Long SHORT_VIDEO_MAX_SIZE = 20*1024*1024L; //20M
	public static final String SHORT_VIDEO_FILE_TYPE = "mp4,mov,avi";

	public static final String TEACHER_S3_ROOT = PropertyConfigurer.stringValue("aws.teacher.dir");

	private static String getAwsAccessKey() {
		String accessKey = PropertyConfigurer.stringValue("aws.accessKey");
		return accessKey;
	}

	private static String getAwsSecretKey() {
		String secretKey = PropertyConfigurer.stringValue("aws.secretKey");
		return secretKey;
	}

	public static String getAwsBucketName(){
		String bucketName = PropertyConfigurer.stringValue("aws.bucketName");
		return bucketName;
	}

	public static AWSCredentials getAWSCredentials() {
		String accessKey = AwsFileUtils.getAwsAccessKey();
		String secretKey = AwsFileUtils.getAwsSecretKey();
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		return credentials;
	}

	public static String getTaxpayerkey(Long teacherId, String fileName){
		String key = buildS3FileKey(TAXPAYER_FORM, fileName, teacherId);
		return key;
	}

	public static String getAvatarKey(String fileName){
		String key = buildS3FileKey(TEACHER_AVATAR, fileName, null);
		return key;
	}

	public static String getLifePictureKey(String fileName){
		String key = buildS3FileKey(TEACHER_LIFE_PICTURE, fileName, null);
		return key;
	}

	public static String getShortVideoKey(String fileName){
		String key = buildS3FileKey(TEACHER_SHORT_VIDEO, fileName, null);
		return key;
	}

	public static String buildS3FileKey(String subDir, String fileName, Long teacherId){
		String key = null;
		try {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			if(teacherId!=null) { //加入教师Id方便查询
				key = Joiner.on("/").join(TEACHER_S3_ROOT, subDir, teacherId, uuid, fileName);
			} else {
				key = Joiner.on("/").join(TEACHER_S3_ROOT, subDir, uuid, fileName);
			}
			key = key.replaceAll("//", "/");
			if(key.startsWith("/")){
				key = key.substring(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
	}
	
	public static Boolean checkTaxPayerFileType(String fileName){
		Boolean flag = checkUploadFileType(TAPXPAYER_FILE_TYPE, fileName);
		return flag;
	}

	public static Boolean checkTaxPayerFileSize(Long fileSize){
		Boolean flag = checkUploadFileSize(TAPXPAYER_FILE_MAX_SIZE, fileSize);
		return flag;
	}

	public static Boolean checkAvatarFileType(String fileName){
		Boolean flag = checkUploadFileType(AVATAR_FILE_TYPE, fileName);
		return flag;
	}

	public static Boolean checkAvatarFileSize(Long fileSize){
		Boolean flag = checkUploadFileSize(AVATAR_MAX_SIZE, fileSize);
		return flag;
	}

	public static Boolean checkLifePicFileType(String fileName){
		Boolean flag = checkUploadFileType(LIFE_PICTURE_FILE_TYPE, fileName);
		return flag;
	}

	public static Boolean checkLifePicFileSize(Long fileSize){
		Boolean flag = checkUploadFileSize(LIFE_PICTURE_MAX_SIZE, fileSize);
		return flag;
	}

	public static Boolean checkShortVideoFileType(String fileName){
		Boolean flag = checkUploadFileType(SHORT_VIDEO_FILE_TYPE, fileName);
		return flag;
	}

	public static Boolean checkShortVideoFileSize(Long fileSize){
		Boolean flag = checkUploadFileSize(SHORT_VIDEO_MAX_SIZE, fileSize);
		return flag;
	}

	public static Boolean checkUploadFileType(String acceptedTypes, String fileName){
		Boolean flag = false;
		String fileType = FileUtils.getFileType(fileName);
		if(StringUtils.isNotBlank(fileType) && StringUtils.isNotBlank(acceptedTypes)){
			List<String> types = Splitter.on(",").omitEmptyStrings().splitToList(acceptedTypes);
			if(types.contains(fileType)){
				flag = true;
			}
		}

		return flag;
	}

	public static Boolean checkUploadFileSize(Long sizeLimit, Long fileSize){
		Boolean flag = false;
		if(fileSize != null && fileSize != null && fileSize <= sizeLimit){
			flag = true;
		}
		return flag;
	}
	public static String reNewFileName(String fileName){
		String name = fileName;
		if(StringUtils.isNotBlank(fileName)){
			String encodeName = Encodes.urlEncode(fileName);
			String fileType = FileUtils.getFileType(fileName);
			if(!fileName.equals(encodeName)){
				name = DateTimeUtils.formatDate(new Date(), "yyyyMMdd-HHmmss")+"."+fileType;
			}
		}
		return name;
	}
}
