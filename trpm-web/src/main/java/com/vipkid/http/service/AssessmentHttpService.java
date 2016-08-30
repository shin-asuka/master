/**
 * 
 */
package com.vipkid.http.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.utils.WebUtils;
import com.vipkid.http.vo.Announcement;
import com.vipkid.http.vo.OnlineClassVo;
import com.vipkid.http.vo.StudentUnitAssessment;

/**
 * 
 * @author zouqinghua
 * @date 2016年7月15日  下午8:50:17
 *
 */
public class AssessmentHttpService extends HttpBaseService {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentHttpService.class);

    public OnlineClassVo findUnSubmitonlineClassVo(OnlineClassVo onlineClassVo ) {

        String url = new StringBuilder(super.serverAddress)
                .append("/education/findSubmitedListByOnlineClassIds").toString();
        logger.info("httpGet findSubmitedListByOnlineClassIds , url= {} ,onlineClassVo = {}", url,JsonUtils.toJSONString(onlineClassVo));
        OnlineClassVo unSubmitSnlineClassVo = null;
        try {
        	String data = WebUtils.postNameValuePair(url, onlineClassVo);
            if (data!=null) {
            	unSubmitSnlineClassVo = JsonUtils.toBean(data, OnlineClassVo.class);
            }
		} catch (Exception e) {
			logger.error("获取未提交课程的UA报告失败！",e);
			e.printStackTrace();
		}
       
        return unSubmitSnlineClassVo;
    }
   
    public List<StudentUnitAssessment> findOnlineClassVo(OnlineClassVo onlineClassVo ) {

        String url = new StringBuilder(super.serverAddress)
                .append("/education/findUAListByOnlineClassIds").toString();
        logger.info("httpGet findUAListByOnlineClassIds , url= {} ,onlineClassVo = {}", url,JsonUtils.toJSONString(onlineClassVo));
        List<StudentUnitAssessment> list = null;
        try {
        	String data = WebUtils.postNameValuePair(url, onlineClassVo);
            if (data!=null) {
            	list = JsonUtils.toBeanList(data, StudentUnitAssessment.class);
            }
		} catch (Exception e) {
			logger.error("获取UA报告失败！",e);
			e.printStackTrace();
		}
       
        return list;
    }
    
    public StudentUnitAssessment findStudentUnitAssessmentByOnlineClassId(Long onlineClassId){
    	StudentUnitAssessment studentUnitAssessment = null;
    	
    	OnlineClassVo onlineClassVo = new OnlineClassVo();
    	onlineClassVo.getIdList().add(onlineClassId);
		List<StudentUnitAssessment> list = findOnlineClassVo(onlineClassVo );
		if(CollectionUtils.isNotEmpty(list)){
			studentUnitAssessment = list.get(0);
		}
		return studentUnitAssessment;
    }
}
