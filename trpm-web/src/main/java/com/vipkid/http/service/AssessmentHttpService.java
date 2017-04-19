/**
 * 
 */
package com.vipkid.http.service;

import java.text.SimpleDateFormat;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vipkid.file.utils.StringUtils;
import com.vipkid.http.utils.JacksonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.utils.WebUtils;
import com.vipkid.http.vo.Announcement;
import com.vipkid.http.vo.OnlineClassVo;
import com.vipkid.http.vo.StudentUnitAssessment;
import org.springframework.stereotype.Service;

/**
 * 
 * @author zouqinghua
 * @date 2016年7月15日  下午8:50:17
 *
 */
@Service
public class AssessmentHttpService extends HttpBaseService {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentHttpService.class);


    public OnlineClassVo findUnSubmitonlineClassVo(OnlineClassVo onlineClassVo ) {

        String url = new StringBuilder(super.serverAddress)
                .append("/education/findUnSubmitedListByOnlineClassIds").toString();
        OnlineClassVo unSubmitSnlineClassVo = null;
        try {
            int i = 0;
            String data = null;
            //retry 拿不到idListStr的请求
            while(data == null && i<3) {
                try {
                    i++;
                    data = WebUtils.postNameValuePair(url, onlineClassVo);
                }catch (Exception e){
                    logger.info("查询未提交UA失败",e);
                }
                Thread.sleep(500);
            }
            if (data!=null) {
            	unSubmitSnlineClassVo = JsonUtils.readJson(data, new TypeReference<OnlineClassVo>() {},JacksonUtils.HHMMSS_MAPPER);
                if(unSubmitSnlineClassVo.getIdListStr().equals(""));
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
        List<StudentUnitAssessment> list = null;
        try {
        	String data = WebUtils.postNameValuePair(url, onlineClassVo);
            if (data!=null) {
            	list = JsonUtils.readJson(data, new TypeReference<List<StudentUnitAssessment>>() {}, JacksonUtils.HHMMSS_MAPPER);
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
