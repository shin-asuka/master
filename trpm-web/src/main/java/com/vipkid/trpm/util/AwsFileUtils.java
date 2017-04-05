package com.vipkid.trpm.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.file.utils.Encodes;
import com.vipkid.file.utils.FileUtils;
import com.vipkid.file.utils.StringUtils;

import com.vipkid.recruitment.entity.TeacherApplication;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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
	public static final String IDENTIFICATION = "identification";
	public static final String DIPLOMA = "diploma"; // W9 form
	public static final String CERTIFICATES = "certificates"; // W9 form
	public static final String DEGREES = "degrees"; // W9 form
	public static final String CONTRACT = "contract"; // W9 form

	public static final String TEACHER_AVATAR = "avatar";
	public static final String TEACHER_LIFE_PICTURE = "picture";
	public static final String TEACHER_SHORT_VIDEO = "video";

	public static final String CANADA_BACKGROUND_CHECK_CPIC_FORM = "cacpic";

	public static final String CANADA_BACKGROUND_CHECK_ID2 = "caid2";

	public static final String US_BACKGROUND_CHECK = "usbgcheck";

	public static final Long BACKGROUND_CHECK_FILE_MAX_SIZE = 10*1024*1024L; //10M

	public static final Long TAPXPAYER_FILE_MAX_SIZE = 20*1024*1024L; //20M
	public static final String TAPXPAYER_FILE_TYPE = "pdf,jpg,png,jpeg,bmp";

	public static final String CONTRACT_FILE_TYPE = "doc,docx,pdf,jpg,jpeg,png,bmp";
	public static final Long CONTRACT_FILE_MAX_SIZE = 20*1024*1024L; //20M

	public static final String IDENTIFICATION_FILE_TYPE = "doc,docx,pdf,jpg,jpeg,png,bmp";
	public static final Long IDENTIFICATION_FILE_MAX_SIZE = 20*1024*1024L; //20M

	public static final String DIPLOMA_FILE_TYPE = "doc,docx,pdf,jpg,jpeg,png,bmp";
	public static final Long DIPLOMA_FILE_MAX_SIZE = 20*1024*1024L; //20M

	public static final String CERTIFICATES_FILE_TYPE = "doc,docx,pdf,jpg,jpeg,png,bmp";
	public static final Long CERTIFICATES_FILE_MAX_SIZE = 20*1024*1024L; //20M

	public static final String DEGREES_FILE_TYPE = "doc,docx,pdf,jpg,jpeg,png,bmp";
	public static final Long DEGREES_FILE_MAX_SIZE = 20*1024*1024L; //20M

	public static final Long AVATAR_MAX_SIZE = 20*1024*1024L; //20M
	public static final String AVATAR_FILE_TYPE = "pdf,jpg,jpeg,png,bmp";

	public static final Long LIFE_PICTURE_MAX_SIZE = 20*1024*1024L; //20M
	public static final String LIFE_PICTURE_FILE_TYPE = "pdf,jpg,jpeg,png,bmp";

	public static final Long SHORT_VIDEO_MIN_SIZE = 0*1024*1024L; //0M
	public static final Long SHORT_VIDEO_MAX_SIZE = 100*1024*1024L; //100M
	public static final String SHORT_VIDEO_FILE_TYPE = "mp4,mov,avi,rmvb";

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
	public static String getContractkey(Long teacherId, String fileName){
		String key = buildS3FileKey(CONTRACT, fileName, teacherId);
		return key;
	}
	public static String getIdentificationkey(Long teacherId, String fileName){
		String key = buildS3FileKey(IDENTIFICATION, fileName, teacherId);
		return key;
	}
	public static String getDiplomakey(Long teacherId, String fileName){
		String key = buildS3FileKey(DIPLOMA, fileName, teacherId);
		return key;
	}
	public static String getDegreeskey(Long teacherId, String fileName){
		String key = buildS3FileKey(DEGREES, fileName, teacherId);
		return key;
	}
	public static String getCertificateskey(Long teacherId, String fileName){
		String key = buildS3FileKey(CERTIFICATES, fileName, teacherId);
		return key;
	}

	public static String getCanadaBackgroundCheckCpicFormKey(Long teacherId, String fileName){
		String key = buildS3FileKey(CANADA_BACKGROUND_CHECK_CPIC_FORM, fileName, teacherId);
		return key;
	}

	public static String getCanadaBackgroundCheckId2Key(Long teacherId, String fileName){
		String key = buildS3FileKey(CANADA_BACKGROUND_CHECK_ID2, fileName, teacherId);
		return key;
	}

	public static String getUsBackgroundCheckKey(Long teacherId, String fileName){
		String key = buildS3FileKey(US_BACKGROUND_CHECK, fileName, teacherId);
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

	public static Boolean checkContractFileType(String fileName){
		Boolean flag = checkUploadFileType(CONTRACT_FILE_TYPE, fileName);
		return flag;
	}

	public static Boolean checkContractFileSize(Long fileSize){
		Boolean flag = checkUploadFileSize(null, CONTRACT_FILE_MAX_SIZE, fileSize);
		return flag;
	}

	public static Boolean checkIdentificationFileType(String fileName){
		Boolean flag = checkUploadFileType(IDENTIFICATION_FILE_TYPE, fileName);
		return flag;
	}

	public static Boolean checkIdentificationFileSize(Long fileSize){
		Boolean flag = checkUploadFileSize(null, IDENTIFICATION_FILE_MAX_SIZE, fileSize);
		return flag;
	}

	public static Boolean checkDiplomaFileType(String fileName){
		Boolean flag = checkUploadFileType(DIPLOMA_FILE_TYPE, fileName);
		return flag;
	}

	public static Boolean checkDiplomaFileSize(Long fileSize){
		Boolean flag = checkUploadFileSize(null, DIPLOMA_FILE_MAX_SIZE, fileSize);
		return flag;
	}


	public static Boolean checkCertificatesFileType(String fileName){
		Boolean flag = checkUploadFileType(CERTIFICATES_FILE_TYPE, fileName);
		return flag;
	}

	public static Boolean checkCertificatesFileSize(Long fileSize){
		Boolean flag = checkUploadFileSize(null, CERTIFICATES_FILE_MAX_SIZE, fileSize);
		return flag;
	}


	public static Boolean checkDegreesFileType(String fileName){
		Boolean flag = checkUploadFileType(DEGREES_FILE_TYPE, fileName);
		return flag;
	}

	public static Boolean checkDegreesFileSize(Long fileSize){
		Boolean flag = checkUploadFileSize(null, DEGREES_FILE_MAX_SIZE, fileSize);
		return flag;
	}

	public static Boolean checkTaxPayerFileType(String fileName){
		Boolean flag = checkUploadFileType(TAPXPAYER_FILE_TYPE, fileName);
		return flag;
	}


	public static Boolean checkTaxPayerFileSize(Long fileSize){
		Boolean flag = checkUploadFileSize(null, TAPXPAYER_FILE_MAX_SIZE, fileSize);
		return flag;
	}

	public static Boolean checkAvatarFileType(String fileName){
		Boolean flag = checkUploadFileType(AVATAR_FILE_TYPE, fileName);
		return flag;
	}

	public static Boolean checkAvatarFileSize(Long fileSize){
		Boolean flag = checkUploadFileSize(null, AVATAR_MAX_SIZE, fileSize);
		return flag;
	}

	public static Boolean checkLifePicFileType(String fileName){
		Boolean flag = checkUploadFileType(LIFE_PICTURE_FILE_TYPE, fileName);
		return flag;
	}

	public static Boolean checkLifePicFileSize(Long fileSize){
		Boolean flag = checkUploadFileSize(null, LIFE_PICTURE_MAX_SIZE, fileSize);
		return flag;
	}

	public static Boolean checkShortVideoFileType(String fileName){
		Boolean flag = checkUploadFileType(SHORT_VIDEO_FILE_TYPE, fileName);
		return flag;
	}

	public static Boolean checkShortVideoFileSize(Long fileSize){
		Boolean flag = checkUploadFileSize(SHORT_VIDEO_MIN_SIZE, SHORT_VIDEO_MAX_SIZE, fileSize);
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

	public static Boolean checkUploadFileSize(Long minSizeLimit, Long maxSizeLimit, Long fileSize){
		if(fileSize == null) {
			return false;
		}

		if(minSizeLimit != null && fileSize <= minSizeLimit){
			return false;
		}

		if(maxSizeLimit != null && fileSize >= maxSizeLimit) {
			return false;
		}

		return true;
	}


	public static String reNewFileName(String fileName){
		String name = fileName;
		if(StringUtils.isNotBlank(fileName)){
			String encodeName = Encodes.urlEncode(fileName);
			String fileType = FileUtils.getFileType(fileName);
			if(!fileName.equals(encodeName)){
				name = DateUtils.formatDate(new Date(), "yyyyMMdd-HHmmss")+"."+fileType;
			}
		}
		return name;
	}
	
	public static String getFileName(String url){
		String name = "";
		if(StringUtils.isNotBlank(url)){
			Integer index = url.lastIndexOf("/");
			name = url.substring(index+1);
			if(name.contains("-")){
				name = name.substring(name.indexOf("-")+1);
			}
		}
		return name;
	}
}
