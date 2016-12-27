package com.vipkid.file.service;
import com.google.api.client.util.Maps;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.controller.portal.OnlineClassController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Created by ninglu on 2016/12/15.
 */
public class QNService {
    private static Logger logger = LoggerFactory.getLogger(OnlineClassController.class);

/*    public static final String IMG_BUCKET = "vipkid-img";
    public static final String VIDEO_BUCKET = "vipkid-video";
    public static final String VIDEO_URL_FIX = "http://video.vipkid.com.cn/previp/unitsong";
    public static final String IMG_URL_FIX = "http://image.vipkid.com.cn/previp/unitsong";
    public static final String FILE_URL_FIX = "http://file.vipkid.com.cn/previp/unitsong";
    public static final String PRECLASS = "preclass/";
    public static final String PREVIP_SONG = "/previp/unitsong";

    public static final String ACCESS_KEY = "TszxltNAuOVlM2jFhdkPl6_qfXt6YT6TkeBJH8TL";
    public static final String SECRET_KEY = "1Bv8qVhp0q_Uw9BphceKOHfJUfhL0kbEkG0yoawL";*/

    Auth auth = Auth.create(ApplicationConstant.QiNiu.ACCESS_KEY,ApplicationConstant.QiNiu.SECRET_KEY);

    String URL = "http://file.vipkid.com.cn/previp/unitsong/MC-L1-U1.pdf";
    //上传文件的路径
    String filePath = "E://file/PreVIP";
    UploadManager uploadManager = new UploadManager();
    //上传到七牛云保存的文件名
    String Key = "Unit-song-lyrics.pdf";

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

    public String getDoewloaddUrl(String seriaNum,String fileType){
        String URL = getPrefix(fileType);
        String type = fileType.toString().toLowerCase();
        String LyricsUrl= URL + seriaNum;
        StringBuffer stringBuffer = new StringBuffer(URL);
        int pos = stringBuffer.indexOf("?");
        if (pos > 0) {
            stringBuffer.append("&attname=");
        } else {
            stringBuffer.append("?attname=");
        }
        URL = download(stringBuffer.toString());
        return URL;
    }

    public Map<String,String> getShowUrl(String seriaNum,String fileType){
        String URL = getPrefix(fileType);
        String type = fileType.toString().toLowerCase();
        Map<String,String> showUrl = Maps.newHashMap();
        String LyricsUrl= URL + seriaNum + ".pdf";
        String VideoUrl = URL + seriaNum + ".mp4";
        showUrl.put("LyricsUrl",LyricsUrl);
        showUrl.put("VideoUrl",VideoUrl);
        return showUrl;
    }
    //3600为token失效的时间
    public String download(String URL){
        String downloadRUL = auth.privateDownloadUrl(URL, 3600);
        return downloadRUL;
    }


    public static void main(String[] args) {
/*        String URL = "http://file.vipkid.com.cn/previp/unitsong/Unit-song-lyrics.pdf";
        StringBuffer stringBuffer = new StringBuffer(URL);
        int pos = stringBuffer.indexOf("?");
        if (pos > 0) {
            stringBuffer.append("&attname=");
        } else {
            stringBuffer.append("?attname=hahahahaa");
        }*/
        QNService qnService = new QNService();
        String DownURL = qnService.getDoewloaddUrl("Unit-song-lyrics.pdf",ApplicationConstant.MediaType.FILE);
        String ShowUrl = qnService.getShowUrl("Unit-song-lyrics.pdf",ApplicationConstant.MediaType.FILE);
        System.out.println(DownURL);
        System.out.println(ShowUrl);
    }
}
