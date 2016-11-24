package com.vipkid.dataSource;

import com.vipkid.dataSource.annotation.Master;
import com.vipkid.dataSource.annotation.Slave;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by liuguanqing on 16/5/10.
 */
public class TypedReadWriteDataSourceInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();

        Annotation annotation = method.getAnnotation(Master.class);
        if (annotation == null) {
            annotation = method.getAnnotation(Slave.class);
        }
        if (annotation != null) {
            TypedReadWriteDataSourceRouter.DataSourceHolder.set(annotation);
        }
        try {
            return invocation.proceed();
        } finally {
            TypedReadWriteDataSourceRouter.DataSourceHolder.clear();
        }
    }

}
