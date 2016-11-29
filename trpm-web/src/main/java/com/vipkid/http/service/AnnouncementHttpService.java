/**
 * 
 */
package com.vipkid.http.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.utils.WebUtils;
import com.vipkid.http.vo.Announcement;

/**
 * 
 * @author zouqinghua
 * @date 2016年7月15日  下午8:50:17
 *
 */
public class AnnouncementHttpService extends HttpBaseService {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementHttpService.class);

    //tianwenqi
    public List<Announcement> test() {
        return null;
    }
    public List<Announcement> findAnnouncementList() {

        String url = new StringBuilder(super.serverAddress)
                .append("/api/am/teacherAnnouncement/findTeacherPortShowList").toString();
        logger.info("httpGet findTeacherPortShowList , url= "+ url);
        List<Announcement> list = null;
        try {
        	String data = WebUtils.simpleGet(url);
            if (data!=null) {
            	list = JsonUtils.toBeanList(data, Announcement.class);
            }
		} catch (Exception e) {
			logger.error("获取教师公告失败！"+e);
			e.printStackTrace();
		}
        
        if(list == null){
        	list = Lists.newArrayList();
        }
        return list;
    }
   
}
