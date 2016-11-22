package com.vipkid.dataSource.annotation;

import java.lang.annotation.*;

/**
 * Created by liuguanqing on 16/5/10.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Master {
}
