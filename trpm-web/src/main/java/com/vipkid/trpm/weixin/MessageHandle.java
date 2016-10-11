package com.vipkid.trpm.weixin;

import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.community.http.client.HttpClientProxy;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 微信消息发送 
 * @author Along(ZengWeiLong)
 * @ClassName: MessageSend 
 * @date 2016年5月9日 下午12:44:10 
 *
 */
public class MessageHandle {

    private static Logger logger = LoggerFactory.getLogger(MessageHandle.class);
    
    /**
     * 同步发送消息到家长端,
     * @Author:ALong (ZengWeiLong)
     * @param requestParam 请求参数
     * @return  Boolean 返回值
     * @throws InterruptedException 
     * @date 2016年5月9日
     */
    public Boolean sendMessage(Map<String, String> requestParam){
        try{
            boolean result = false;
            int i = 0;
            while( i < MessageConfig.MAX_COUNT){
                i++;
                requestParam.put("token",DigestUtils.md5Hex(MessageConfig.ENCRYPT_KEY_PREFIX + requestParam.get("openIds") + MessageConfig.ENCRYPT_KEY_SUFFIX));
                logger.info("第"+i+"次请求参数"+requestParam);
                String responseBody = HttpClientProxy.post(MessageConfig.REQUEST_URL, requestParam);
                if (null == responseBody) {
                    logger.error("第{}次请求结果: Request error,检查请求地址是否存在:{}", i, MessageConfig.REQUEST_URL);
                    Thread.sleep(MessageConfig.MILLISECOND);
                    continue;
                }
                JsonNode resultString = JsonTools.readValue(responseBody);
                if(!"0".equals(resultString.get("errcode").asText())){
                    logger.error("第{}次请求结果: Request fail  结果:{}", i,JsonTools.getJson(resultString));
                    JsonNode subresult = resultString.get("data");
                    if(subresult != null){
                        String failId = "";
                        for (int j = 0; j < subresult.size(); j++) {
                            if(!"0".equals(subresult.get(j).get("errcode").asText())){
                                failId += subresult.get(j).get("openId").asText()+",";
                            }
                        }
                        if(failId.indexOf(",") > 0) failId =failId.substring(0,failId.length() - 1);
                        requestParam.put("openIds",failId);
                    }
                    Thread.sleep(MessageConfig.MILLISECOND);
                    continue;
                }else{
                    logger.info("Request ok result:{}", JsonTools.getJson(result));
                    result = true;
                    break;
                }
            }
            return result;
        }catch(Exception e){
            logger.error("同步发送失败:"+e.getMessage()+";参数:"+requestParam, e);
        }
        return null;
    }
    

}
