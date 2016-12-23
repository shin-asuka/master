package com.vipkid.rest.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.rest.validation.annotation.EnumList.Annotaions;
import com.vipkid.rest.validation.annotation.EnumList.Type;
import com.vipkid.rest.validation.annotation.Ignore;
import com.vipkid.rest.validation.annotation.Length;
import com.vipkid.rest.validation.annotation.NotNull;
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
        Result result = Result.bulider();
        if("serialVersionUID".equalsIgnoreCase(field.getName())){
            return result;
        }
        Ignore ignore = getAnnotation(Ignore.class,bean,field);
        if(ignore != null){
            
            Arrays.asList(ignore.type()).stream().forEach(_bean->{logger.info("属性"+field.getName()+",有Ignore注解 type:"+_bean.name());});
            
            if(Arrays.asList(ignore.type()).contains(Annotaions.ALL)){
                return result;
            }
            if(Arrays.asList(ignore.type()).contains(Annotaions.LENGTH)){
                if(!result.isResult()){
                    result = checkNotNull(bean, field);
                }
            }
            if(Arrays.asList(ignore.type()).contains(Annotaions.NOT_NULL)){
                if(!result.isResult()){
                    result = checkLength(bean, field);
                }
            }
        }else{
            if(!result.isResult()){
                result = checkNotNull(bean, field);
            }
            if(!result.isResult()){
                result = checkLength(bean, field);
            }
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
        if(ClassUtils.isString(field.getType())){
            if(StringUtils.isBlank((String)value)){
                return Result.bulider(field.getName(),Type.NOT_NULL,notNull.message(),true);
            }
        }else if(ClassUtils.isInteger(field.getType())){
            if((int)value < 0){
                return Result.bulider(field.getName(),Type.NOT_NULL,notNull.message(),true);
            }
        }else if(ClassUtils.isLong(field.getType())){
            if((long)value < 0){
                return Result.bulider(field.getName(),Type.NOT_NULL,notNull.message(),true);
            }
        }else if(ClassUtils.isFloat(field.getType())){
            if((float)value < 0){
                return Result.bulider(field.getName(),Type.NOT_NULL,notNull.message(),true);
            }
        }else if(ClassUtils.isDouble(field.getType())){
            if((double)value < 0){
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
        if(ClassUtils.isString(field.getType())){
            if(length.maxLength() != 0 && StringUtils.length((String)value) > length.maxLength()){
                return Result.bulider(field.getName(),Type.MAXLENGTH,length.message(),true);
            }
            if(length.minLength() != 0 && StringUtils.length((String)value) < length.minLength()){
                return Result.bulider(field.getName(),Type.MINLENGTH,length.message(),true);
            }
        }else if(ClassUtils.isInteger(field.getType())){
            if(length.maxLength() != 0 && (int)value > length.maxLength()){
                return Result.bulider(field.getName(),Type.MAXLENGTH,length.message(),true);
            }
            if(length.minLength() != 0 && (int)value < length.minLength()){
                return Result.bulider(field.getName(),Type.MINLENGTH,length.message(),true);
            }
        }else if(ClassUtils.isLong(field.getType())){
            if(length.maxLength() != 0 && (long)value > length.maxLength()){
                return Result.bulider(field.getName(),Type.MAXLENGTH,length.message(),true);
            }
            if(length.minLength() != 0 && (long)value < length.minLength()){
                return Result.bulider(field.getName(),Type.MINLENGTH,length.message(),true);
            }
        }else if(ClassUtils.isFloat(field.getType())){
            if(length.maxLength() != 0 && (float)value > length.maxLength()){
                return Result.bulider(field.getName(),Type.MAXLENGTH,length.message(),true);
            }
            if(length.minLength() != 0 && (float)value < length.minLength()){
                return Result.bulider(field.getName(),Type.MINLENGTH,length.message(),true);
            }
        }else if(ClassUtils.isDouble(field.getType())){
            if(length.maxLength() != 0 && (double)value > length.maxLength()){
                return Result.bulider(field.getName(),Type.MAXLENGTH,length.message(),true);
            }
            if(length.minLength() != 0 && (double)value < length.minLength()){
                return Result.bulider(field.getName(),Type.MINLENGTH,length.message(),true);
            }
        }else{
            if(length.maxLength() != 0 && StringUtils.length((String)value) > length.maxLength()){
                return Result.bulider(field.getName(),Type.MAXLENGTH,length.message(),true);
            }
            if(length.minLength() != 0 && StringUtils.length((String)value) < length.minLength()){
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
    
   static class ClassUtils{
        
        static boolean isString(Class<?> classes){
            if(String.class == classes){
                return true;
            }
            return false;
        }
        
        static boolean isInteger(Class<?> classes){
            if(Integer.class == classes || int.class == classes){
                return true;
            }
            return false;
        }
        
        static boolean isDouble(Class<?> classes){
            if(Double.class == classes || double.class == classes){
                return true;
            }
            return false;
        }
        
        static boolean isFloat(Class<?> classes){
            if(Float.class == classes || float.class == classes){
                return true;
            }
            return false;
        }
        
        static boolean isLong(Class<?> classes){
            if(Long.class == classes || long.class == classes){
                return true;
            }
            return false;
        }
        
        static boolean isBoolean(Class<?> classes){
            if(Boolean.class == classes || boolean.class == classes){
                return true;
            }
            return false;
        }
        
        static boolean isByte(Class<?> classes){
            if(Byte.class == classes || byte.class == classes){
                return true;
            }
            return false;
        }
        
        static boolean isChar(Class<?> classes){
            if(Character.class == classes || char.class == classes){
                return true;
            }
            return false;
        }
        
        static boolean isDate(Class<?> classes){
            if(Date.class == classes){
                return true;
            }
            return false;
        }
    }
}
