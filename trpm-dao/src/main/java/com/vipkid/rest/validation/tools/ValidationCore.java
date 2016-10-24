package com.vipkid.rest.validation.tools;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipkid.rest.validation.ValidationUtils;

public class ValidationCore {

    private static Logger logger = LoggerFactory.getLogger(ValidationUtils.class);
    
    /**
     * @Author:ALong (ZengWeiLong)
     * @param type
     * @param o
     * @return    
     * Result
     * @date 2016年10月21日
     */
    public static Result isNull(String name,Class<?> type,Object value){
        logger.info("name:{},type:{},value:{}",name,type.toString(),value);
        //如果值为null 则验证不通过
        if(value == null){
            return Result.bulider(name,ValidationEnum.Message.NOT_NULL, true);
        }
        // type
        if(String.class == type){
            if(StringUtils.isBlank((String)value)){
                return Result.bulider(name,ValidationEnum.Message.NOT_NULL, true);
            }
        }else if(Integer.class == type){
            if((int)value == 0){
                return Result.bulider(name,ValidationEnum.Message.NOT_NULL, true);
            }
        }else if(Long.class == type){
            if((long)value == 0){
                return Result.bulider(name,ValidationEnum.Message.NOT_NULL, true);
            }
        }else if(Float.class == type){
            if((float)value == 0){
                return Result.bulider(name,ValidationEnum.Message.NOT_NULL, true);
            }
        }else if(Double.class == type){
            if((double)value == 0){
                return Result.bulider(name,ValidationEnum.Message.NOT_NULL, true);
            }
        }
        return Result.bulider();
    }
    
    public static Result maxLength(String name,Class<?> type,Object value,int maxLength){
        logger.info("name:{},type:{},value:{},maxLength:{}",name,type.toString(),value,maxLength);
        //如果最大值为0 则验证通过，设置无效
        if(maxLength <= 0){
            return Result.bulider();
        }
        if(String.class == type){
            if(StringUtils.length((String)value) > maxLength){
                return Result.bulider(name,ValidationEnum.Message.MAX_LENGTH, true);
            }
        }else if(Integer.class == type){
            if((int)value > maxLength){
                return Result.bulider(name,ValidationEnum.Message.MAX_LENGTH, true);
            }
        }else if(Long.class == type){
            if((long)value > maxLength){
                return Result.bulider(name,ValidationEnum.Message.MAX_LENGTH, true);
            }
        }else if(Float.class == type){
            if((float)value > maxLength){
                return Result.bulider(name,ValidationEnum.Message.MAX_LENGTH, true);
            }
        }else if(Double.class == type){
            if((double)value > maxLength){
                return Result.bulider(name,ValidationEnum.Message.MAX_LENGTH, true);
            }
        }else{
            if(StringUtils.length((String)value) > maxLength){
                return Result.bulider(name,ValidationEnum.Message.MAX_LENGTH, true);
            }
        }
        return Result.bulider();
    }
}
