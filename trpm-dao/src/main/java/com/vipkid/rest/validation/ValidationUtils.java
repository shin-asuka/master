package com.vipkid.rest.validation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.vipkid.rest.validation.annotation.Ignore;
import com.vipkid.rest.validation.annotation.Verify;
import com.vipkid.rest.validation.tools.ReflectUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.rest.validation.tools.ValidationEnum;
import com.vipkid.rest.validation.tools.ValidationCore;

public class ValidationUtils {
    
    private static Logger logger = LoggerFactory.getLogger(ValidationUtils.class);
    
    /**
     * 可选择验证结果类型 多个和一个  返回List
     * @Author:ALong (ZengWeiLong)
     * @param bean
     * @param isAll true,返回所有验证不通过的属性List，false:返回第一个验证不通过的List.get(0)
     * @return    
     * List<Result>
     * @date 2016年10月21日
     */
    public static <T> List<Result> checkForClass(T bean,boolean isAll){
        List<Result>  list = Lists.newArrayList();
        Verify verify = bean.getClass().getAnnotation(Verify.class);
        Field[] fields = bean.getClass().getDeclaredFields(); 
        for(Field field : fields){
            Ignore ignore = field.getAnnotation(Ignore.class);
            if(ignore == null){
                Verify _verify = field.getAnnotation(Verify.class);
                if(_verify != null) {
                    verify = _verify;
                    logger.info("属性{},有局部注解，优先",field.getName());
                }
                if(verify != null){
                    Result result = checkHandle(verify, bean, field);
                    if(result.isResult()){
                        list.add(result);
                        if(!isAll){
                            return list;
                        }
                    }
                }
            }else{
                logger.info("属性{},有忽略注解，优先",field.getName());
            }
        }
        return list;
    }
    
    /**
     * 单独验证结果 返回Result
     * @Author:ALong (ZengWeiLong)
     * @param bean
     * @return    
     * Result
     * @date 2016年10月22日
     */
    public static <T> Result checkForField(T bean){
        List<Result> list = checkForClass(bean,false);
        if(CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
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
    public static <T> Result checkForField(T bean,List<String> names){
        Field[] fields = bean.getClass().getDeclaredFields();
        for(Field field : fields){
            if(names.contains(field.getName())){
                Object value = ReflectUtils.getFieldValueByName(field.getName(), bean);
                Result result = ValidationCore.isNull(field.getName(), field.getType(), value);
                if(result.isResult()){
                    return result;
                }
            }
        }
        return null;
    }
    
    private static <T> Result checkHandle(Verify verify,T bean,Field field){
        //null
        Result result = Result.bulider();
        if(Arrays.asList(verify.type()).contains(ValidationEnum.Type.NOT_NULL)){
            Object value = ReflectUtils.getFieldValueByName(field.getName(), bean);
            result = ValidationCore.isNull(field.getName(), field.getType(), value);
        }
        //length
        if(!result.isResult() && Arrays.asList(verify.type()).contains(ValidationEnum.Type.MAX_LENGTH)){
            Object value = ReflectUtils.getFieldValueByName(field.getName(), bean);
            result = ValidationCore.maxLength(field.getName(), field.getType(), value,verify.maxLength());
        }
        return result;
    }
     
}
