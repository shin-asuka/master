package com.vipkid.email.templete;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;

/**
 * Email 模板内容读取
 * 含  readTemplete 接口
 * @author ALong
 *
 */
public class TempleteUtils {

    public final static String NOTE_TEMPLETE = "<tr><td>{{time}}</td><td>{{ename}}</td><td>{{lessonson}}</td></tr>";
    
    /**
     * 
     *  模板读取<br/>
     * 
     * @Author:ALong (ZengWeiLong)
     * @param contentTemplete 内容模板路径
     * @param map 模板中,以{{name}}标识的参数名称和值
     * @param titleTemplete 标题模板路径
     * @return Map<String,String> 返回读取的内容title content
     * @date 2016年4月23日
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> readTemplete(String contentTemplete, Map<String, String> map, String titleTemplete) {
        Map<String,String> cacheMap = TempleteChche.getMe().get(contentTemplete,Map.class);
        String content = "",title = "";
        if(cacheMap == null || cacheMap.size() == 0){
            cacheMap = Maps.newHashMap();
            cacheMap.put("content", readTemplete(contentTemplete).toString());
            cacheMap.put("title", readTemplete(titleTemplete).toString());
            TempleteChche.getMe().set(contentTemplete,cacheMap);
        }
        content = cacheMap.get("content");
        title = cacheMap.get("title");
        if(map != null && map.size() > 0){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                content = content.replace("{{" + entry.getKey().trim() + "}}", entry.getValue());
                title = title.replace("{{" + entry.getKey().trim() + "}}", entry.getValue());
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
     * @param templeteName 文件名称
     * @return 2015年11月5日
     */
    private static StringBuilder readTemplete(String templeteName) {
        InputStream is = TempleteUtils.class.getResourceAsStream(templeteName);
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
