package com.vipkid.portal.classroom.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @desc: 反射工具
 */
public class BeanUtils {

	private static Logger logger = LoggerFactory.getLogger(BeanUtils.class);
	
    /**
     * 相同属性值拷贝，不相同的属性保留原值
     * @author: bingye
     * @createTime: 2014-11-8 上午10:33:59
     * @param <S>
     * @param <D>
     * @param s 源对象
     * @param d 目标对象
     * @return D 目标对象
     */
    @SuppressWarnings({ "unchecked", "rawtypes"})
    public static <S, D> D copyPropertys(S s, D d) {
        if(s == null || d == null){
            return d;
        }
        Field[] sfields = s.getClass().getDeclaredFields();
        Field[] dfields = d.getClass().getDeclaredFields();
        Class scls = s.getClass();
        Class dcls = d.getClass();
        try {
            for (Field sfield : sfields) {
                String sName = sfield.getName();
                Class sType = sfield.getType();
                if(sName.equals("serialVersionUID")){
                	continue;
                }
                String sfieldName = sName.substring(0, 1).toUpperCase() + sName.substring(1);
                Method sGetMethod = scls.getMethod("get" + sfieldName);
                Object value = sGetMethod.invoke(s);
                for (Field dfield : dfields) {
                    String dName = dfield.getName();
                    Class dType = dfield.getType();
                    if (dName.equals(sName)) {
                        Method dSetMethod = dcls.getMethod("set" + sfieldName,dType);
                        try{
                        	dSetMethod.invoke(d, value);
                        }catch(Exception e){
                        	logger.error("Property:"+sType.toString() + ",type:" + dType.toString()+",error:"+e.getMessage(),e);
                        }
                        break;
                    }
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        }
        return d;
    }
}