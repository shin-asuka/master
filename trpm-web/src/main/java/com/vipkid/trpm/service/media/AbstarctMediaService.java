package com.vipkid.trpm.service.media;

import org.springframework.web.multipart.MultipartFile;

import com.vipkid.trpm.entity.media.UploadResult;

public abstract class AbstarctMediaService {

	public abstract UploadResult handleUpload(MultipartFile file, String fileType, String fileSize,
			String fullFileName);

}
