package com.vipkid.trpm.util;

import com.vipkid.background.api.sterling.dto.SterlingCallBack;

/**
 * Created by liyang on 2017/3/16.
 */
public interface Function<K, V> {
    K apply(V input);
}
