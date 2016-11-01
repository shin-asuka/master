package com.vipkid.trpm.weixin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.Student;
import com.vipkid.trpm.entity.User;

public class MessageTools {


    private static Logger logger = LoggerFactory.getLogger(MessageTools.class);
    
    /**
     * 异步发送消息
     * 发送fackbook到家长（发送：宝贝收到一条老师的反馈）的模板消息 openIds id1,id2,id3<br>
     * token 认证，参数openIds和from组合加密后的串<br>
     */
    public void sendFeedbackAsync(String openIds,Student student,User teacherUser,String serialNumber,long onlineClassId) {
       /* if(StringUtils.isNotBlank(openIds)){
            Map<String,String> pram = Maps.newHashMap();
            pram.put("teacherName",teacherUser.getName());
            pram.put("studentName",student.getEnglishName());
            pram.put("lessonName",serialNumber);
            pram.put("mobileUrl",PropertyConfigurer.stringValue("mobile.url")+"teacherfeedback?teacherId="+teacherUser.getId()+"&onlineClassId="+onlineClassId+"&sign=1&type=MAJOR");
            String content = this.readTemplete("weichat.html",pram);
            
            Map<String, String> requestParam = Maps.newHashMap();
            requestParam.put("openIds", openIds);
            requestParam.put("message",content);
            requestParam.put("from", MessageConfig.ENCRYPT_KEY_SUFFIX);
            requestParam.put("token",DigestUtils.md5Hex(MessageConfig.ENCRYPT_KEY_PREFIX + openIds + MessageConfig.ENCRYPT_KEY_SUFFIX));
            logger.info("异步,预备参数:" + requestParam.toString());
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(()->{
                new MessageHandle().sendMessage(requestParam);
            });
        }else{
            logger.info("没有获取到家长微信关注公众号信息，不能发送信息给家长微信");
        }*/
    }
    
    /**
     * 同步发送消息
     * 发送fackbook到家长（发送：宝贝收到一条老师的反馈）的模板消息 openIds id1,id2,id3<br>
     * token 认证，参数openIds和from组合加密后的串<br>
     */
    public void sendFeedbackSync(String openIds,Student student,User teacherUser,String serialNumber,long onlineClassId) {
        /*if(StringUtils.isNotBlank(openIds)){
            Map<String,String> pram = Maps.newHashMap();
            pram.put("teacherName",teacherUser.getName());
            pram.put("studentName",student.getEnglishName());
            pram.put("lessonName",serialNumber);
            pram.put("mobileUrl",PropertyConfigurer.stringValue("mobile.url")+"teacherfeedback?teacherId="+teacherUser.getId()+"&onlineClassId="+onlineClassId+"&sign=1&type=MAJOR");
            String content = this.readTemplete("weichat.html",pram);
            
            Map<String, String> requestParam = Maps.newHashMap();
            requestParam.put("openIds", openIds);
            requestParam.put("message",content);
            requestParam.put("from", MessageConfig.ENCRYPT_KEY_SUFFIX);
            requestParam.put("token",DigestUtils.md5Hex(MessageConfig.ENCRYPT_KEY_PREFIX + openIds + MessageConfig.ENCRYPT_KEY_SUFFIX));
            logger.info("同步,预备参数:" + requestParam.toString());
            MessageHandle mh = new MessageHandle();
            mh.sendMessage(requestParam);
        }else{
            logger.info("没有获取到家长微信关注公众号信息，不能发送信息给家长微信");
        }*/
    }
    
    /**
     * 读取模板内容
     * @Author:ALong
     * @param templeteName 文件名称
     * @return 2015年11月5日
     */
    private String readTemplete(String templeteName,Map<String,String> pram){
        InputStream is = this.getClass().getResourceAsStream(templeteName);
        StringBuilder result = new StringBuilder("");
        try{
            BufferedReader br =new BufferedReader(new InputStreamReader(is,Charsets.UTF_8));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(s+"\n");
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        String resultStr = result.toString();
        if(pram != null && !pram.isEmpty()){
            for (String key:pram.keySet()) {
                resultStr = resultStr.replace("{{"+key+"}}", pram.get(key)); 
            }
        }
        return resultStr;
   }
}
