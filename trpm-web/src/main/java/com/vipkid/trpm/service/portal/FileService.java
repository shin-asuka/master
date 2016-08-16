package com.vipkid.trpm.service.portal;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.vipkid.trpm.constant.ApplicationConstant.MediaType;
import com.vipkid.trpm.entity.media.UploadResult;
import com.vipkid.trpm.service.media.AbstarctMediaService;


@Service
public class FileService {
    
    private Logger logger = LoggerFactory.getLogger(FileService.class);
    
    @Autowired
    private AbstarctMediaService mediaService;
    
    public Map<String, Object> uploadNormalFile(MultipartFile file) {
        logger.info("uploadNormalFile now...");
        
        //result
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("result", false);
        
        ////
        String fileType = MediaType.FILE;
        
        long fs = file.getSize();
        String fileSize = String.valueOf(fs);
        
        String fullFileName = file.getOriginalFilename();
        
        UploadResult upload = mediaService.handleUpload(file, fileType, fileSize, fullFileName);
        if (!upload.isResult()) {
            resultMap.put("message", upload.getMsg());
            
            return resultMap;
        }
        
        resultMap.put("result", true);
        resultMap.put("name", fullFileName);
//        resultMap.put("url", upload.getUrl());
        resultMap.put("url", upload.getEncodeUrl());
        return resultMap;
    }

    
}
