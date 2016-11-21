package com.vipkid.enums;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

public class AppEnum {

    /**
     * 根据index获取枚举
     * @Author:ALong (ZengWeiLong)
     * @param clazz
     * @param index
     * @return    
     * E
     * @date 2016年10月24日
     */
    public static <E extends Enum<E>> E getByIndex(final Class<E> enumClass, int index) {
        return (E)enumClass.getEnumConstants()[index];  
    } 
    
    /**
     * 根据名称获取枚举 
     * @Author:ALong (ZengWeiLong)
     * @param enumClass
     * @param name
     * @return    
     * E
     * @date 2016年10月24日
     */
    public static <E extends Enum<E>> E getByName(final Class<E> enumClass, String name) {  
        return EnumUtils.getEnum(enumClass, name);
    } 
    
    /**
     * 判断枚举类是否包含某个枚举
     * @Author:ALong (ZengWeiLong)
     * @param enumClass
     * @param name
     * @return    
     * boolean
     * @date 2016年10月24日
     */
    public static <E extends Enum<E>> boolean containsName(final Class<E> enumClass, String name) {
        return EnumUtils.isValidEnum(enumClass, name);
    } 
    @Deprecated
    public static <T> boolean containsNameold(final Class<T> enumClass, String name) {
        T[] enums = enumClass.getEnumConstants();
        for(T obj:enums){
            if(StringUtils.equals(obj.toString(), name)){
                return true;
            }
        }
        return false;
    }
}
