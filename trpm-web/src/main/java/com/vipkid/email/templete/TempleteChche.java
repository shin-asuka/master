package com.vipkid.email.templete;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模板内存缓存类<br/>
 *  
 *  仅仅在内存中保存模板数据，一旦机器重启将清空缓存
 *  
 * @author Along(ZengWeiLong)
 * @ClassName: TempleteChche
 * @date 2016年4月23日 下午7:10:14
 *
 */
public class TempleteChche {

    private static TempleteChche tc = null;

    private Map<String, Object> cacheMap = new ConcurrentHashMap<String, Object>();

    private TempleteChche() {

    }

    public static TempleteChche getMe() {
        if (tc == null) {
            tc = new TempleteChche();
        }
        return tc;
    }

    public boolean set(String key, Object value) {
        cacheMap.put(key, value);
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object object = cacheMap.get(key);
        return object == null ? null : (T) object;
    }

    public boolean del(String key) {
        cacheMap.remove(key);
        return true;
    }

    public boolean delAll() {
        cacheMap.clear();
        return true;
    }
}
