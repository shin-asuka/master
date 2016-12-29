package com.vipkid.file.service;
import com.google.api.client.util.Maps;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.controller.portal.OnlineClassController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Created by ninglu on 2016/12/15.
 */
@Service
public class QNService {
    private static Logger logger = LoggerFactory.getLogger(OnlineClassController.class);

    Auth auth = Auth.create(ApplicationConstant.QiNiu.ACCESS_KEY,ApplicationConstant.QiNiu.SECRET_KEY);
    
    private String getPrefix(String fileType) {
        String dir = null;
        switch (fileType) {
            case ApplicationConstant.MediaType.IMAGE:
                dir = ApplicationConstant.QiNiu.IMG_URL_FIX + ApplicationConstant.QiNiu.PREVIP_SONG + "/";
                break;
            case ApplicationConstant.MediaType.VIDEO:
                dir = ApplicationConstant.QiNiu.VIDEO_URL_FIX + ApplicationConstant.QiNiu.PREVIP_SONG + "/";
                break;
            case ApplicationConstant.MediaType.FILE:
                dir = ApplicationConstant.QiNiu.FILE_URL_FIX + ApplicationConstant.QiNiu.PREVIP_SONG + "/";
            default:
        }
        return dir;
    }

    public Map<String,Object> getDownloadUrl(String seriaNum){
        String lyricsURL = getPrefix(ApplicationConstant.MediaType.FILE);
        String videoURL = getPrefix(ApplicationConstant.MediaType.VIDEO);
        Map<String,Object> downloadUrl = Maps.newHashMap();
        Map<String,Object> showUrl = getShowUrl(seriaNum);
        String lyricsDownUrl = showUrl.get("lyricsShowUrl") + "?attname=";
        String videoDownUrl = showUrl.get("videoShoewUrl") + "?attname=";
        lyricsDownUrl = download(lyricsDownUrl);
        videoDownUrl = download(videoDownUrl);
        downloadUrl.put("lyricsDownUrl",lyricsDownUrl);
        downloadUrl.put("videoDownUrl",videoDownUrl);


        return downloadUrl;
    }

    public Map<String,Object> getShowUrl(String seriaNum){
        String lyricsURL = getPrefix(ApplicationConstant.MediaType.FILE);
        String videoURL = getPrefix(ApplicationConstant.MediaType.VIDEO);
        Map<String,Object> showUrl = Maps.newHashMap();
        String lyricsUrl= lyricsURL + seriaNum + ".pdf";
        String videoUrl = videoURL + seriaNum + ".mp4";
        System.out.println(lyricsUrl + "  " + videoUrl);
        showUrl.put("lyricsShowUrl",lyricsUrl);
        showUrl.put("videoShowUrl",videoUrl);
        return showUrl;
    }
    //3600为token失效的时间
    public String download(String URL){
        String downloadRUL = auth.privateDownloadUrl(URL, 3600);
        return downloadRUL;
    }

}
