package com.vipkid.rest.validation;

import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.vipkid.rest.validation.tools.Result;

public class ValidateUtils {
    
    private static Logger logger = LoggerFactory.getLogger(ValidateUtils.class);
    
    /**
     * 可选择验证结果类型 多个和一个  返回List
     * @Author:ALong (ZengWeiLong)
     * @param bean
     * @param isAll true,返回所有验证不通过的属性List，false:返回第一个验证不通过的List.get(0)
     * @return    
     * List<Result>
     * @date 2016年10月21日
     */
    public static <T> List<Result> checkBean(T bean,boolean isAll){
        List<Result>  list = Lists.newArrayList();
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field:fields) {
            Result result = ValidateHandle.checkForField(bean, field);
            if(result.isResult()){
                logger.info("result:"+result.getName());
                list.add(result);
                if(!isAll){
                    return list;
                }
            }
        }
        return list;
    }

    /**
     * 检查指定的某些属性是否为空,不需要注解
     * @Author:ALong (ZengWeiLong)
     * @param bean
     * @param names 需要要检查的属性名
     * @return    
     * Result
     * @date 2016年10月22日
     */
    public static <T> List<Result> checkForField(T bean,List<String> names,boolean isAll){
        List<Result>  list = Lists.newArrayList();
        Field[] fields = bean.getClass().getDeclaredFields();
        for(Field field:fields){
            if(names.contains(field.getName())){
                Result result = ValidateHandle.checkForField(bean, field);
                if(result.isResult()){
                    list.add(result);
                    if(!isAll){
                        return list;
                    }
                }
            }
        }
        return list;
    }
}
