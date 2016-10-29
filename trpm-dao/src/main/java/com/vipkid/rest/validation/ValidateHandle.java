package com.vipkid.rest.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.rest.validation.annotation.Ignore;
import com.vipkid.rest.validation.annotation.Length;
import com.vipkid.rest.validation.annotation.NotNull;
import com.vipkid.rest.validation.annotation.Type;
import com.vipkid.rest.validation.tools.ReflectUtils;
import com.vipkid.rest.validation.tools.Result;

public class ValidateHandle {
    
    private static Logger logger = LoggerFactory.getLogger(ValidateHandle.class);
    
    
    /**
     * 检查指定的某些属性是否为空,不需要注解
     * @Author:ALong (ZengWeiLong)
     * @param bean
     * @param names 需要要检查的属性名
     * @return    
     * Result
     * @date 2016年10月22日
     */
    public static <T> Result checkForField(T bean,Field field){
        Ignore ignore = getAnnotation(Ignore.class,bean,field);
        if(ignore != null){
            logger.info("属性"+field.getName()+",有Ignore注解，优先");
            return Result.bulider();
        }
        Result result = checkNotNull(bean, field);
        if(!result.isResult()){
            result = checkLength(bean, field);
        }
        return result;
    }
    
    private static <T> Result checkNotNull(T bean,Field field){
        NotNull notNull = getAnnotation(NotNull.class,bean,field);
        if(notNull == null){
            return Result.bulider();
        }        
        Object value = ReflectUtils.getFieldValueByName(field.getName(), bean);
        //如果值为null 则验证不通过
        if(value == null){
            return Result.bulider(field.getName(),Type.NOT_NULL,notNull.message(),true);
        }
        // type
        if(String.class == field.getType()){
            if(StringUtils.isBlank((String)value)){
                return Result.bulider(field.getName(),Type.NOT_NULL,notNull.message(),true);
            }
        }else if(Integer.class == field.getType()){
            if((int)value == 0){
                return Result.bulider(field.getName(),Type.NOT_NULL,notNull.message(),true);
            }
        }else if(Long.class == field.getType()){
            if((long)value == 0){
                return Result.bulider(field.getName(),Type.NOT_NULL,notNull.message(),true);
            }
        }else if(Float.class == field.getType()){
            if((float)value == 0){
                return Result.bulider(field.getName(),Type.NOT_NULL,notNull.message(),true);
            }
        }else if(Double.class == field.getType()){
            if((double)value == 0){
                return Result.bulider(field.getName(),Type.NOT_NULL,notNull.message(),true);
            }
        }
        return Result.bulider();
    }
        
    private static <T> Result checkLength(T bean,Field field){
        Length length = getAnnotation(Length.class,bean,field);
        if(length == null){
            return Result.bulider();
        }
        Object value = ReflectUtils.getFieldValueByName(field.getName(), bean);
        //如果最大值为0 则验证通过，设置无效
        if(value == null || (length.maxLength() == 0 && length.minLength() == 0)){
            return Result.bulider();
        }
        if(String.class == field.getType()){
            if(StringUtils.length((String)value) > length.maxLength()){
                return Result.bulider(field.getName(),Type.MAXLENGTH,length.message(),true);
            }
            if(StringUtils.length((String)value) < length.minLength()){
                return Result.bulider(field.getName(),Type.MINLENGTH,length.message(),true);
            }
        }else if(Integer.class == field.getType()){
            if((int)value > length.maxLength()){
                return Result.bulider(field.getName(),Type.MAXLENGTH,length.message(),true);
            }
            if((int)value < length.minLength()){
                return Result.bulider(field.getName(),Type.MINLENGTH,length.message(),true);
            }
        }else if(Long.class == field.getType()){
            if((long)value > length.maxLength()){
                return Result.bulider(field.getName(),Type.MAXLENGTH,length.message(),true);
            }
            if((long)value < length.minLength()){
                return Result.bulider(field.getName(),Type.MINLENGTH,length.message(),true);
            }
        }else if(Float.class == field.getType()){
            if((float)value > length.maxLength()){
                return Result.bulider(field.getName(),Type.MAXLENGTH,length.message(),true);
            }
            if((float)value < length.minLength()){
                return Result.bulider(field.getName(),Type.MINLENGTH,length.message(),true);
            }
        }else if(Double.class == field.getType()){
            if((double)value > length.maxLength()){
                return Result.bulider(field.getName(),Type.MAXLENGTH,length.message(),true);
            }
            if((double)value < length.minLength()){
                return Result.bulider(field.getName(),Type.MINLENGTH,length.message(),true);
            }
        }else{
            if(StringUtils.length((String)value) > length.maxLength()){
                return Result.bulider(field.getName(),Type.MAXLENGTH,length.message(),true);
            }
            if(StringUtils.length((String)value) < length.minLength()){
                return Result.bulider(field.getName(),Type.MINLENGTH,length.message(),true);
            }
        }
        return Result.bulider();
    }
    
    
    public static <A extends Annotation> A getAnnotation(Class<A> annotationClass,Object bean,Field field){
        A a = field.getAnnotation(annotationClass);
        if(a == null){
            a = bean.getClass().getAnnotation(annotationClass);
        } 
        return a;
    }
}
