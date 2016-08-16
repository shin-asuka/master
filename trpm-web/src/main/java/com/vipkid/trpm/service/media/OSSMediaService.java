package com.vipkid.trpm.service.media;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.community.lang.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.openservices.ClientException;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.OSSObject;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
import com.vipkid.trpm.constant.ApplicationConstant.MediaType;
import com.vipkid.trpm.constant.ApplicationConstant.OSS;
import com.vipkid.trpm.entity.media.UploadResult;
import com.vipkid.trpm.proxy.OSSProxy;

@Service("mediaService")
public class OSSMediaService extends AbstarctMediaService {

	private static Logger logger = LoggerFactory.getLogger(OSSMediaService.class);

	/**
	 * 处理多媒体文件上传
	 * 
	 * @param file
	 * @param fileType
	 * @param fileSize
	 * @param fullFileName
	 * @return
	 */
	@Override
	public UploadResult handleUpload(MultipartFile file, String fileType, String fileSize,
			String fullFileName) {
	    logger.info("file type: {} , full file name: {} ", fileType, fullFileName);
		return uploadToAliyunOSS(file, fileType, fileSize, fullFileName);
	}

	/**
	 * 上传到aliyun OSS的操作
	 * 
	 * @param file
	 * @param fileType
	 * @param fileSize
	 * @param fullFileName
	 *            指定要保存的文件名称
	 * @return UploadResult
	 */
	private UploadResult uploadToAliyunOSS(MultipartFile file, final String fileType,
			String fileSize, String fullFileName) {
		// 实例化OSS客户端
		OSSClient ossClient = new OSSClient(OSS.ENDPOINT, OSS.KEY_ID, OSS.KEY_SECRET);

		ObjectMetadata objectMetadata = new ObjectMetadata();
		if (!StringUtils.isEmpty(fileSize)) {
			objectMetadata.setContentLength(Long.parseLong((fileSize)));
		} else {
			objectMetadata.setContentLength(file.getSize());
		}
		
		String strMimeType = file.getContentType();
		logger.info("get mime type: {}", strMimeType);

		String realName = file.getOriginalFilename();
		if (!StringUtils.isEmpty(fullFileName)) {
			realName = fullFileName;
		}

		int index = realName.lastIndexOf(Symbol.PERIOD);
		String postFix = realName.substring(index);

		String ossUrlOrigin = getPrefix(fileType) + UUID.randomUUID().toString() + Symbol.SLASH ;
		String ossUrl = ossUrlOrigin + realName;
		logger.info("ossURL: {}", ossUrl);
		
		// 设置content type
		String strContentType = getContentType(postFix.replaceFirst(Symbol.PERIOD, ""));
		objectMetadata.setContentType(strContentType);

		final String url = ossUrl;
		PutObjectResult result = null;

		try {
			result = ossClient.putObject(OSS.BUCKET, url, file.getInputStream(), objectMetadata);
		} catch (OSSException e) {
			logger.error("OSSException,e={}", e);
		} catch (ClientException e) {
			logger.error("ClientException,e={}", e);
		} catch (IOException e) {
			logger.error("IOException,e={}", e);
		}

		logger.info("uploaded file = {} with type = {} to AliYun OSS: {}", file.getOriginalFilename(),
				fileType, url);

		/* Shrink Avatar */
		shrink(fileType, url);

		boolean succeed = false;
		UploadResult uploadedFileResult = new UploadResult();

		// 2016-06-01 mobile app需要对url进行编码
		String encodeURL = ossUrlOrigin;
		try {
            String encodeName = URLEncoder.encode(realName, "UTF-8");
            encodeURL +=   encodeName;
            logger.info("encode url:{}",encodeURL);
            
        } catch (UnsupportedEncodingException e) {
            logger.error("encode error: {}",e);
        }
		
		if (null == result) {
			succeed = false;
		} else {
			succeed = true;

			if (fileType == MediaType.FILE || fileType == MediaType.UNIT_TEST) {
				uploadedFileResult.setName(realName);
			} else {
				uploadedFileResult.setName(UUID.randomUUID().toString() + postFix);
			}

			// avatar 在数据库中不包括
			if (fileType == MediaType.AVATAR) {
			    logger.info("upload result encode url:{}", url);
				uploadedFileResult.setUrl(url);
				uploadedFileResult.setEncodeUrl(encodeURL);
			} else {
				uploadedFileResult.setUrl(OSS.URL_PREFFIX + Symbol.SLASH + url);
				uploadedFileResult.setEncodeUrl(OSS.URL_PREFFIX + Symbol.SLASH + encodeURL);
			}
		}

		uploadedFileResult.setResult(succeed);
		return uploadedFileResult;
	}

	/**
	 * Shrink Avatar 图片
	 * 
	 * @param fileType
	 * @param url
	 */
	private void shrink(String fileType, String url) {
	    
	    String strShrink = new String(url);
		if (MediaType.AVATAR == fileType) {
			new Thread(() -> {
				try {
					String avatarLargeStyle = "avatar-large";
//					String strShrinkURL = OSS.SHRINK_URl + Symbol.SLASH + url;
					String strShrinkURL = OSS.SHRINK_URl + strShrink;
					logger.info("now shrink {} with style:{}",strShrinkURL, avatarLargeStyle);
                    if (!OSSProxy.shrink(strShrinkURL, avatarLargeStyle)) {
						logger.error("Failed to process the image {} with style {} to AliYun.",
						        strShrink, avatarLargeStyle);
					}
				} catch (Exception e) {
					logger.error("ImageProcessAPI has error:" + e.getMessage());
				}
			}).start();
		}
	}

	/**
	 * 获取文件类型前缀
	 * 
	 * @param fileType
	 * @return String
	 */
	private String getPrefix(String fileType) {
		return fileType.toLowerCase() + Symbol.SLASH;
	}

	/**
	 * 根据文件后缀名，获取ContentType
	 * 
	 * @param strFileType
	 * @return String
	 */
	private static String getContentType(String fileSuffix) {
		String contentType = "text/plain";
		if (null == fileSuffix) {
			return contentType;
		}

		switch (fileSuffix.toLowerCase()) {
		// image
		case "gif":
			contentType = "image/gif";
			break;
		case "png":
			contentType = "image/png";
			break;
		case "jpg":
			contentType = "image/jpg";
			break;
		case "jpeg":
			contentType = "image/jpeg";
			break;
		case "bmp":
			contentType = "image/bmp";
			break;
		case "rar":
			contentType = "application/octet-stream";
			break;
		case "zip":
			contentType = "application/zip";
			break;
		// doc
		case "doc":
			contentType = "application/msword";
			break;
		case "docx":
			contentType = "application/msword";
			break;
		case "rtf":
			contentType = "application/rtf";
			break;
		case "pdf":
			contentType = "application/pdf";
			break;
		case "txt":
			contentType = "text/plain";
			break;
		case "xml":
			contentType = "text/xml";
			break;
		// audio
		case "aiff":
			contentType = "audio/aiff";
			break;
		case "mp3":
			contentType = "audio/mpeg";
			break;
		case "wav":
			contentType = "audio/x-wav";
			break;
		// video
		case "mp4":
			contentType = "video/mp4";
			break;
		case "avi":
			contentType = "video/x-msvideo";
			break;
		case "mov":
			contentType = "video/quicktime";
			break;
		default:
			break;
		}

		return contentType;
	}

	/**
     * 对avatar做压缩裁剪处理
     * @param url -- /file/#########/filename.ext
     */
    public static void compactAvatarInAliyun(String url) {
        int nLen = url.length();
        int nIndex = url.lastIndexOf(".");
        
        String fileExt = url.substring(nIndex+1);
        if (null == fileExt) {
            return;
        }
    
        compactImageWithAliyunAPI(url,fileExt);
        
    }
    
    public static void compactImageWithAliyunAPI(String url, String fileType) {
        OSSClient ossClient = new OSSClient(OSS.SHRINK_URl, OSS.KEY_ID, OSS.KEY_SECRET);
        String ossObject = url;
        if (url.startsWith("/")) {
            // 
            ossObject = url.substring(1);
        }
        
        String suffixURL = "@1e_320w_240h_1c_0i_1o_90Q_1x.jpg";
        suffixURL = getImageShrinkSuffix(fileType);
        String compactURL = ossObject+suffixURL;
        
        OSSObject os = ossClient.getObject(OSS.BUCKET, compactURL);
        logger.info(""+os.getObjectMetadata().getContentLength());

        ossClient = new OSSClient(OSS.ENDPOINT, OSS.KEY_ID, OSS.KEY_SECRET);
        PutObjectResult result = ossClient.putObject(OSS.BUCKET, ossObject, os.getObjectContent(),
                os.getObjectMetadata());
        logger.info(""+result);
    }
    
    
    private static HashMap<String, String> SYNC_IMAGE_SHRINK_SUFFIX = new HashMap<String, String>() {
        private static final long serialVersionUID = 12;
        {
            put(null,"@1e_320w_240h_1c_0i_1o_90Q_1x.jpg");
            put("jpg", "@1e_320w_240h_1c_0i_1o_90Q_1x.jpg");
            
            put("png", "@1e_250w_250h_1c_0i_1o_1x.png");
            
        }
    };
    
    /**
     * 使用aliyun的image压缩处理的类型suffix
     * @param fileType
     * @return
     */
    private static String getImageShrinkSuffix(String fileType) {
        String defaultSuffix = "@1e_320w_240h_1c_0i_1o_90Q_1x.jpg";
        String suffix = defaultSuffix;
        
        suffix = SYNC_IMAGE_SHRINK_SUFFIX.get(fileType);
        if (null != suffix) {
            return suffix;
        }
        
        // 其他的处理
        String defaultSuffixNoType = "@1e_320w_240h_1c_0i_1o_90Q_1x";
        return defaultSuffixNoType+"."+fileType;
    }
    
}
