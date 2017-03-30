package com.vipkid.trpm.util;

import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by liyang on 2017/3/16.
 */
public class MapUtils {

    public static <K, V> Map<K, V> transformListToMap(List<V> list, Function<K, V> function) {
        Map<K, V> map = Maps.newHashMap();
        if (CollectionUtils.isEmpty(list)){
            return map;
        }
        for (V v : list) {
            K key = function.apply(v);
            map.put(key, v);
        }
        return map;
    }


}
