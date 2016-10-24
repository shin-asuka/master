package com.vipkid.rest.validation.tools;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.ValidateEnum.Type;

public class ValidateCore {

    private static Logger logger = LoggerFactory.getLogger(ValidateUtils.class);
    
    /**
     * @Author:ALong (ZengWeiLong)
     * @param classes
     * @param o
     * @return    
     * Result
     * @date 2016年10月21日
     */
    public static Result isNull(String name,Class<?> classes,Object value){
        logger.info("name:{},type:{},value:{}",name,classes.toString(),value);
        Type type = Type.NOT_NULL;
        //如果值为null 则验证不通过
        if(value == null){
            return Result.bulider(name,type, true);
        }
        // type
        if(String.class == classes){
            if(StringUtils.isBlank((String)value)){
                return Result.bulider(name,type, true);
            }
        }else if(Integer.class == classes){
            if((int)value == 0){
                return Result.bulider(name,type, true);
            }
        }else if(Long.class == classes){
            if((long)value == 0){
                return Result.bulider(name,type, true);
            }
        }else if(Float.class == classes){
            if((float)value == 0){
                return Result.bulider(name,type, true);
            }
        }else if(Double.class == classes){
            if((double)value == 0){
                return Result.bulider(name,type, true);
            }
        }
        return Result.bulider();
    }
    
    public static Result maxLength(String name,Class<?> classes,Object value,int maxLength){
        logger.info("name:{},type:{},value:{},maxLength:{}",name,classes.toString(),value,maxLength);
        Type type = Type.MAX_LENGTH;
        //如果最大值为0 则验证通过，设置无效
        if(maxLength <= 0 || value == null){
            return Result.bulider();
        }
        if(String.class == classes){
            if(StringUtils.length((String)value) > maxLength){
                return Result.bulider(name,type, true);
            }
        }else if(Integer.class == classes){
            if((int)value > maxLength){
                return Result.bulider(name,type, true);
            }
        }else if(Long.class == classes){
            if((long)value > maxLength){
                return Result.bulider(name,type, true);
            }
        }else if(Float.class == classes){
            if((float)value > maxLength){
                return Result.bulider(name,type, true);
            }
        }else if(Double.class == classes){
            if((double)value > maxLength){
                return Result.bulider(name,type, true);
            }
        }else{
            if(StringUtils.length((String)value) > maxLength){
                return Result.bulider(name,type, true);
            }
        }
        return Result.bulider();
    }
    
    
    public static Result minLength(String name,Class<?> classes,Object value,int minLength){
        logger.info("name:{},type:{},value:{},minLength:{}",name,classes.toString(),value,minLength);
        Type type = Type.MIN_LENGTH;
        //如果最大值为0 则验证通过，设置无效
        if(minLength <= 0 || value == null){
            return Result.bulider();
        }
        if(String.class == classes){
            if(StringUtils.length((String)value) < minLength){
                return Result.bulider(name,type, true);
            }
        }else if(Integer.class == classes){
            if((int)value < minLength){
                return Result.bulider(name,type, true);
            }
        }else if(Long.class == classes){
            if((long)value < minLength){
                return Result.bulider(name,type, true);
            }
        }else if(Float.class == classes){
            if((float)value < minLength){
                return Result.bulider(name,type, true);
            }
        }else if(Double.class == classes){
            if((double)value < minLength){
                return Result.bulider(name,type, true);
            }
        }else{
            if(StringUtils.length((String)value) < minLength){
                return Result.bulider(name,type, true);
            }
        }
        return Result.bulider();
    }
    
}
