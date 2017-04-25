package com.vipkid.recruitment.common.service;

import com.google.api.client.util.Maps;
import com.vipkid.http.service.HttpApiClient;
import com.vipkid.http.utils.JacksonUtils;
import com.vipkid.http.utils.WebUtils;
import com.vipkid.recruitment.common.dto.PushMessage;
import com.vipkid.recruitment.common.dto.PushMultiCastRequest;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by luojiaoxia on 17/4/13.
 */
@Service("auditPushMessageService")
public class AuditPushMessageService {

    private static final Logger logger = LoggerFactory.getLogger(AuditPushMessageService.class);

    private static final int RETRY_COUNT = 3;

    private static final String PUSH_URL = PropertyConfigurer.stringValue("api.push.url") + "/v1/biz/teacher/push/multicast";

    private static final String SAVE_PUSH_MESSAGE_URL = PropertyConfigurer.stringValue("tis.message.save.url");

    @Autowired
    private HttpApiClient apiClient;


    public void pushAndSaveMessage(Long teacherId){
        if(null == teacherId){
            logger.info("teacherId为空，终止发送pushMessage");
            return;
        }
        Map<String, String> requestHeader = Maps.newHashMap();
        requestHeader.put("X-Vipkid-Service", "TeacherREST");

        PushMultiCastRequest request = new PushMultiCastRequest();
        String title = "VIPKID Teach";
        String content = "Application status update";
        request.setTitle(title);
        request.setBody(content);
        request.setPriority("high");
        request.setJump_link("vipkid://native?target=message");
        request.setExpires_at(LocalDateTime.now().plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant()
                .getEpochSecond());
        List<Long> list = new ArrayList<Long>();
        list.add(teacherId);
        request.setTarget(list);
        logger.info("Invoke push api multicast params: {}", JacksonUtils.toJSONString(request));

        String resultJson = null;
        int retry = 0;
        String paramJson = JacksonUtils.toJSONString(request);
        while (null == resultJson && retry <= RETRY_COUNT) {
            retry++;
            resultJson =  apiClient.doPostJsonWithHeader(PUSH_URL, paramJson, requestHeader);
        }
        logger.info("Invoke push api multicast result: {}", resultJson);
    }
}
