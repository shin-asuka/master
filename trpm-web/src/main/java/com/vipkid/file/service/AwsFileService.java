package com.vipkid.file.service;

import java.io.IOException;
import java.io.InputStream;

import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.vipkid.file.model.FileVo;
import com.vipkid.file.utils.FileUtils;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.trpm.util.AwsFileUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zouqinghua
 * @date 2016年10月17日  上午2:38:23
 *
 */
@Service
public class AwsFileService {

public Logger logger = LoggerFactory.getLogger(AwsFileService.class);
	
	private AmazonS3 amazonS3;
	
	public AwsFileService() {
	}
	
	public AmazonS3 getAmazonS3(){
		if(amazonS3 == null){
			try {
				AWSCredentials credentials = AwsFileUtils.getAWSCredentials();
				ClientConfiguration clientConfig = new ClientConfiguration();
				amazonS3 = new AmazonS3Client(credentials, clientConfig);
				Region cn = Region.getRegion(Regions.CN_NORTH_1);
				amazonS3.setRegion(cn);
			} catch (Exception e) {
				logger.error("连接aws 服务异常",e);
			}
		}
		return amazonS3;
	}

	public FileVo getFile(String bucketName,String key){
		FileVo fileVo = null;
		AmazonS3 client = getAmazonS3();
        try {
        	logger.info("获取文件信息 bucketName = {}, key = {}",bucketName,key);
        	if(client.doesObjectExist(bucketName, key)){
        		ObjectMetadata objectMetadata = client.getObjectMetadata(bucketName, key);
            	fileVo = FileVo.ObjectMetadataToFileVo(objectMetadata, key);
            	String url = "https://"+bucketName+"/" + fileVo.getPath();
                url = FileUtils.EncodeURLFileName(url);
                fileVo.setUrl(url);
        	}
            logger.info("获取文件信息 : {}",JsonUtils.toJSONString(fileVo));
        } catch (Exception e) {
            logger.error("获取文件信息出错：" , e);
        }
        return fileVo;
	}
	
	public FileVo down(String bucketName,String key){
		FileVo fileVo = null;
		AmazonS3 client = getAmazonS3();
        try {
        	logger.info("下载文件 bucketName = {}, key = {}",bucketName,key);
        	if(client.doesObjectExist(bucketName, key)){
        		S3Object s3jObect = client.getObject(bucketName, key);
            	fileVo = FileVo.S3ObjectToFileVo(s3jObect);
            	String url = "https://"+bucketName+"/" + fileVo.getPath();
                url = FileUtils.EncodeURLFileName(url);
                fileVo.setUrl(url);
        	}
            logger.info("下载文件 : {}",JsonUtils.toJSONString(fileVo));
        } catch (Exception e) {
            logger.error("下载文件出错：" , e);
        }
        return fileVo;
	}
	
	public FileVo upload(String bucketName, String key, InputStream inputStream, Long size){
		String contentType = Mimetypes.getInstance().getMimetype(key);
		AmazonS3 client = getAmazonS3();
		
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(size);
		metadata.setContentType(contentType);
		
		logger.info("========= 开始上传文件 bucketName = {},key = {},size = {}",bucketName,key,size);
		PutObjectResult result = client.putObject(bucketName, key, inputStream,metadata);
		
		if(inputStream!=null){
            try {
            	inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		logger.info("========= 上传结束 key = {}",key);
		FileVo fileVo = FileVo.ObjectMetadataToFileVo(result.getMetadata(), key);
		if(fileVo!=null){
        	fileVo.setBucketName(bucketName);
        }
		return fileVo;
	}

	/**
	 * 文件上传功能
	 */
	public FileVo awsUpload(MultipartFile file, Long teacherId, String fileName, String key) {
		logger.info("teacher id = {} ", teacherId);
		FileVo fileVo = null;
		if (file != null) {
			String bucketName = PropertyConfigurer.stringValue("aws.bucketName");
			try {
				logger.info("文件:{}上传", fileName);
				fileVo = upload(bucketName, key, file.getInputStream(), file.getSize());
			} catch (IOException e) {
				logger.error("awsUpload exception", e);
			}
			if (fileVo != null) {
				String url = "https://" + bucketName + "/" + key;
				fileVo.setUrl(url);
			}
		}
		return fileVo;
	}
	
	
}
