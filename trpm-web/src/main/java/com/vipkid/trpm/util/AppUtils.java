package com.vipkid.trpm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.vipkid.trpm.entity.app.AppEnum;
import com.vipkid.trpm.entity.app.AppEnum.ClassStatus;
import com.vipkid.trpm.entity.app.AppEnum.CourseType;

public class AppUtils {

    private static Logger logger = LoggerFactory.getLogger(AppUtils.class);
    
    /**
     * 根据课程状态统计
     * @Author:ALong (ZengWeiLong)
     * @param classStatus
     * @return    String[]
     * @date 2016年6月8日
     */
    public static String[] coverClassStatus(String classStatus){
        if(StringUtils.isBlank(classStatus)) return null;
        String[] classStatuses = classStatus.split(",");
        for(int i = 0 ; i < classStatuses.length;i++){
            try{
                classStatuses[i] = AppEnum.valueOf(ClassStatus.class, Integer.valueOf(classStatuses[i])).toString();
            }catch(Exception e){
                logger.error("不存在的课程状态：" + classStatuses[i]);
                classStatuses[i] = null;
            }
        }
        return classStatuses;
    }
    
     /**
     * 根据课程类型统计
     * @Author:ALong (ZengWeiLong)
     * @param classStatus
     * @return    String[]
     * @date 2016年6月8日
     */
    public static String[] coverClassType(String classType){
        if(StringUtils.isBlank(classType)) return null;
        String[] classTypes = classType.split(",");
        for(int i = 0 ; i < classTypes.length;i++){
            try{
                classTypes[i] = AppEnum.valueOf(CourseType.class, Integer.valueOf(classTypes[i])).toString();
            }catch(Exception e){
                logger.error("不存在的课程类型定义：" + classTypes[i]);
                classTypes[i] = null;
            }
        }
        return classTypes;
    }
    
    /**
     * 判断一个枚举类是否包含某个名称 
     * @Author:ALong (ZengWeiLong)
     * @param clazz
     * @param name
     * @return    
     * boolean
     * @date 2016年10月22日
     */
   public static <E extends Enum<E>> boolean containsName(final Class<E> enumClass,String name){
       if(StringUtils.isBlank(name)) return false;
       try{
           return AppEnum.containsName(enumClass,name);
       }catch(Exception e){
           logger.error("不存在的课程类型定义：" + name);
       }
       return false;
   }
   
    
    /**
     * 将一个时间戳转化为指定时区的时间
     * @Author:ALong (ZengWeiLong)
     * @param timeMillis
     * @param timezone
     * @return    
     * String
     * @date 2016年6月8日
     */
    public static String converTimezone(long timeMillis,String timezone){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        return sdf.format(new Date(timeMillis))+" 00:00:00";
    }
    
    /**
     * 数据预备算法
     * @Author:ALong (ZengWeiLong)
     * @param keyStartTime
     * @param list
     * @return    
     * List<Map<String,Object>>
     * @throws java.text.ParseException
     * @date 2016年6月8日
     */
    public static List<Map<String, Object>> converPushList(String keyStartTime,String keyEndTIme,String timezone,List<Map<String, Object>> list) throws ParseException{
        long startcount = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        List<Map<String, Object>> retList = Lists.newArrayList();
        int count = daysBetween(keyStartTime, keyEndTIme);
        for(int i = 0 ; i < count; i++){
            Map<String, Object> tmpMap = Maps.newHashMap();
            long start = sdf.parse(keyStartTime).getTime() + i*3600*24*1000;
            long end = sdf.parse(keyStartTime).getTime() + (i+1)*3600*24*1000;
            tmpMap.put("startTime", start);
            tmpMap.put("endTime", end);
            tmpMap.put("hasClass",0);
            String key = sdf.format(new Date(start));
            if(list != null && !list.isEmpty()){
                for (int j = 0; j < list.size(); j++) {
                    Map<String, Object> map = list.get(j);
                    if(key.equals(map.get("days"))){
                        tmpMap.put("hasClass",1);
                        list.remove(j);
                        break;
                    }
                }
            }
            retList.add(tmpMap);
        }       
        long endcount = System.currentTimeMillis();
        logger.info(" converPushList 算法用时:" + (endcount - startcount) + " 毫秒");
        return retList;
    }
        
    /** 
    *字符串的日期格式的计算 
    */  
    public static int daysBetween(String smdate,String bdate) throws ParseException{  
       SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
       Calendar cal = Calendar.getInstance();    
       cal.setTime(sdf.parse(smdate));    
       long time1 = cal.getTimeInMillis();                 
       cal.setTime(sdf.parse(bdate));    
       long time2 = cal.getTimeInMillis();         
       long between_days=(time2-time1)/(1000*3600*24);  
       return Integer.parseInt(String.valueOf(between_days));     
    }
}
