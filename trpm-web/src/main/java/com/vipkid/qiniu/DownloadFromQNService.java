package com.vipkid.qiniu;
import com.qiniu.util.Auth;

/**
 * Created by ninglu on 2016/12/15.
 */
public class DownloadFromQNService {

    public static final String IMG_BUCKET = "vipkid-img";
    public static final String VIDEO_BUCKET = "vipkid-video";
    public static final String VIDEO_URL_FIX = "http://video.vipkid.com.cn";
    public static final String IMG_URL_FIX = "http://image.vipkid.com.cn";
    public static final String PRECLASS = "preclass/";

    public static final String ACCESS_KEY = "TszxltNAuOVlM2jFhdkPl6_qfXt6YT6TkeBJH8TL";
    public static final String SECRET_KEY = "1Bv8qVhp0q_Uw9BphceKOHfJUfhL0kbEkG0yoawL";

    Auth auth = Auth.create(ACCESS_KEY,SECRET_KEY);

    String filename = "L1-U1-LC1-1";

    String URL = "http://video.vipkid.com.cn";

    String attname = filename;
    //3600为token失效的时间
    public String download(){
        String downloadRUL = auth.privateDownloadUrl(URL, 3600);
        return downloadRUL;
    }




}
