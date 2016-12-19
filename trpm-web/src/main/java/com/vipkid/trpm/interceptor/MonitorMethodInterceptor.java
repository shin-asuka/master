package com.vipkid.trpm.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.velocity.runtime.parser.node.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorMethodInterceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MonitorMethodInterceptor.class);

    /*
     * @param invocation
     * 
     * @return
     * 
     * @throws Throwable
     * 
     * @see
     * org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation
     * )
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long beginTime = System.nanoTime();
        try {
            Object result = invocation.proceed();
            return result;
        } catch (Exception e) {
            logger.error("MonitorMethodInterceptor error !",e);
            throw new RuntimeException(e);
        } finally {
            long endTime = System.nanoTime();
            logger.info("Method: [" + invocation.getMethod().getDeclaringClass().getName() + "."
                            + invocation.getMethod().getName() + "()], Invocation Time: ["
                            + MathUtils.divide((endTime - beginTime), (1000 * 1000)) + "] Millis");
        }
    }

}
