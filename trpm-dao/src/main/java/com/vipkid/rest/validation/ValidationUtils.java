package com.vipkid.rest.validation;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.vipkid.rest.validation.annotation.Ignore;
import com.vipkid.rest.validation.annotation.Verify;
import com.vipkid.rest.validation.tools.ReflectUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.rest.validation.tools.ValidationEnum;

public class ValidationUtils {
    
    private static Logger logger = LoggerFactory.getLogger(ValidationUtils.class);
    
    /**
     * 获取属性及验证结果 
     * @Author:ALong (ZengWeiLong)
     * @param bean
     * @param all
     * @return    
     * List<Result>
     * @date 2016年10月21日
     */
    public static <T> List<Result> checkoutDtoClass(T bean,boolean all){
        List<Result>  list = Lists.newArrayList();
        Verify verify = bean.getClass().getAnnotation(Verify.class);
        if(verify != null){
            Field[] fields = bean.getClass().getDeclaredFields(); 
            for(Field field : fields){
                Ignore ignore = field.getAnnotation(Ignore.class);
                if(ignore == null){
                    if(verify.type() == ValidationEnum.Type.NOT_NULL){
                        Object value = ReflectUtils.getFieldValueByName(field.getName(), bean);
                        Result result = isNotNull(field.getName(), field.getType(), value);
                        if(result.isResult()){
                            list.add(result);
                            if(!all){
                                return list;
                            }
                        }
                    }
                }
            }
        }
        return list;
    }
    
    /**
     * 单独验证结果 
     * @Author:ALong (ZengWeiLong)
     * @param bean
     * @return    
     * Result
     * @date 2016年10月22日
     */
    public static <T> Result checkoutDto(T bean){
        Verify verify = bean.getClass().getAnnotation(Verify.class);
        if(verify == null){
            Field[] fields = bean.getClass().getDeclaredFields(); 
            for(Field field : fields){
                Ignore ignore = field.getAnnotation(Ignore.class);
                if(ignore == null){
                    verify = field.getAnnotation(Verify.class);
                    if(verify.type() == ValidationEnum.Type.NOT_NULL){
                        Object value = ReflectUtils.getFieldValueByName(field.getName(), bean);
                        Result result = isNotNull(field.getName(), field.getType(), value);
                        if(result.isResult()){
                            return result;
                        }
                    }
                }
            }
        }else{
            List<Result> list = checkoutDtoClass(bean,false);
            if(CollectionUtils.isNotEmpty(list)){
                return list.get(0);
            }
        }
        return null;
    }
    
    /**
     * @Author:ALong (ZengWeiLong)
     * @param type
     * @param o
     * @return    
     * Result
     * @date 2016年10月21日
     */
    private static Result isNotNull(String name,Class<?> type,Object value){
        logger.info("name:{},type:{},value:{}",name,type.toString(),value);
        if(String.class == type){
            if(StringUtils.isBlank((String)value)){
                return Result.bulider(name,ValidationEnum.Message.ERROR, true);
            }
        }else if(Integer.class == type){
            if(value == null || (int)value == 0){
                return Result.bulider(name,ValidationEnum.Message.ERROR, true);
            }
        }else if(Long.class == type){
            if(value == null || (long)value == 0){
                return Result.bulider(name,ValidationEnum.Message.ERROR, true);
            }
        }else if(Float.class == type){
            if(value == null || (float)value == 0){
                return Result.bulider(name,ValidationEnum.Message.ERROR, true);
            }
        }else if(Double.class == type){
            if(value == null || (double)value == 0){
                return Result.bulider(name,ValidationEnum.Message.ERROR, true);
            }
        }
        return Result.bulider();
    }
     
}
