package com.vipkid.trpm.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MapUtils;

import com.google.common.collect.Maps;

/**
 * bean 转化
 * 
 * @author Along(ZengWeiLong)
 * @ClassName: BatisBeanUtils
 * @date 2016年9月6日 下午5:08:45
 *
 */
public class Bean2Map {

    /**
     * Bean -- Map
     * @Author:ALong (ZengWeiLong)
     * @param bean
     * @return    
     * Map<String,? extends Object>
     * @date 2016年9月28日
     */
    public static Map<String,Object> toMap(Object bean) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            if (bean != null) {
                result = PropertyUtils.describe(bean);
                result.remove("class");
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Map -- Bean 反射实现
     * 
     * @Author:ALong (ZengWeiLong)
     * @param map
     * @param classs
     * @return T
     * @date 2016年9月18日
     */
    public static <T> T map2Bean(Map<String,? extends Object> map, Class<T> classs) {
        T bean = null;       
        try {
            bean = classs.newInstance();
            if (MapUtils.isNotEmpty(map)) {
                Field[] fields = classs.getDeclaredFields();
                for(Field field:fields){
                    if(map.containsKey(field.getName())){
                        String methodName = field.getName();
                        methodName = "set"+methodName.substring(0,1).toUpperCase()+methodName.substring(1);
                        Method m1 = classs.getDeclaredMethod(methodName,map.get(field.getName()).getClass());
                        m1.invoke(bean, new Object[]{map.get(field.getName())});
                    }
                }
            }            
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return bean;
    }
}
