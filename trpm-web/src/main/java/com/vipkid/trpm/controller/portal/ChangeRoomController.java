package com.vipkid.trpm.controller.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.community.config.PropertyConfigurer;
import org.community.http.client.HttpClientProxy;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

@Controller
public class ChangeRoomController extends AbstractPortalController{
    
    private Logger logger = LoggerFactory.getLogger(ChangeRoomController.class);

    /**
     * 教师变更检查
     * @Author:ALong (ZengWeiLong)
     * @param request
     * @param response
     * @return
     * String
     * @date 2016年5月16日
     */
    @RequestMapping("/chagngeroom")
    public String chagngeroom(HttpServletRequest request,HttpServletResponse response,String onlineClassId){
        Map<String,Object> resultMap = Maps.newHashMap();
        String requestUrl = PropertyConfigurer.stringValue("microservice.url") + "/classroom/onlineClassSupplierCode";
        Map<String, String> pram = Maps.newHashMap();
        pram.put("onlineClassId", onlineClassId);
        String resultBody = HttpClientProxy.get(requestUrl, pram,  Maps.newHashMap());
        logger.info("请求URL:"+requestUrl+",参数onlineClassId:" + onlineClassId+",请求结果result:"+resultBody);
        if(resultBody != null){
           JsonNode jnode = JsonTools.readValue(resultBody);
           if(jnode != null && jnode.get("supplierCode") != null){ 
               try{
                   int supplierCode  = jnode.get("supplierCode").asInt();
                   resultMap.put("supplierCode", supplierCode);
               }catch(Exception e){
                   logger.error("supplierCode:返回参数不是整型,valule="+jnode.get("supplierCode")+",Error-info"+e.getMessage(),e);
               }
           }else{
               logger.error("返回内容为空,resultBody=" + resultBody);
           }
        }else{
            logger.error("返回内容为null,resultBody=" + resultBody);
        }
        return jsonView(response,resultMap);
    }
}
