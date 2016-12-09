package com.vipkid.email.template;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Email 模板内容读取
 * 含  readTemplate 接口
 * @author ALong
 *
 */
public class TemplateUtils {

    private static Logger logger = LoggerFactory.getLogger(TemplateUtils.class);

    public final static String NOTE_TEMPLATE = "<tr><td>{{time}}</td><td>{{ename}}</td><td>{{lessonNo}}</td></tr>";
    
    /**
     * 
     *  模板读取<br/>
     * 
     * @Author:ALong (ZengWeiLong)
     * @param contentTemplate 内容模板路径
     * @param map 模板中,以{{name}}标识的参数名称和值
     * @param titleTemplate 标题模板路径
     * @return Map<String,String> 返回读取的内容title content
     * @date 2016年4月23日
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> readTemplate(String contentTemplate, Map<String, String> map, String titleTemplate) {
        Map<String,String> cacheMap = TemplateCache.getMe().get(contentTemplate,Map.class);
        String content = "",title = "";
        if(cacheMap == null || cacheMap.size() == 0){
            cacheMap = Maps.newHashMap();
            cacheMap.put("content", readTemplate(contentTemplate).toString());
            cacheMap.put("title", readTemplate(titleTemplate).toString());
            TemplateCache.getMe().set(contentTemplate,cacheMap);
        }
        content = cacheMap.get("content");
        title = cacheMap.get("title");
        if(map != null && map.size() > 0){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry != null && entry.getValue() != null) {
                    content = content.replace("{{" + entry.getKey().trim() + "}}", entry.getValue());
                    title = title.replace("{{" + entry.getKey().trim() + "}}", entry.getValue());
                }
            }
        }
        Map<String, String> resultMap = Maps.newHashMap();
        resultMap.put("title", title);
        resultMap.put("content", content);
        return resultMap;
    }

    /**
     * 读取模板内容
     * 
     * @Author:ALong
     * @param templateName 文件名称
     * @return 2015年11月5日
     */
    public static StringBuilder readTemplate(String templateName) {
        InputStream is = TemplateUtils.class.getClass().getClassLoader().getResourceAsStream("template" + File.separator + templateName);
        return streamToString(is);
    }

    /**
     * 读取模板内容
     *
     * @Author:ALong
     * @param templateName 文件名称
     * @return 2015年11月5日
     */
    public static StringBuilder readTemplatePath(String templateName) {
        InputStream is = TemplateUtils.class.getClass().getClassLoader().getResourceAsStream(templateName);
        return streamToString(is);
    }

    private static StringBuilder streamToString(InputStream is){
        StringBuilder result = new StringBuilder("");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is,Charsets.UTF_8));// 构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
                result.append(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
