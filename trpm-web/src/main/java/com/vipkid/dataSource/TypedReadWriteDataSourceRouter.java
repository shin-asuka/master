package com.vipkid.dataSource;

import com.vipkid.dataSource.annotation.Master;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.lang.annotation.Annotation;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by liuguanqing on 16/5/10. 基于@Master，@Slave注释类型的读写分离 只有注释的接口才会读写分离，其他的默认仍然是master
 */
public class TypedReadWriteDataSourceRouter extends AbstractRoutingDataSource {

    private static Logger logger = LoggerFactory.getLogger(TypedReadWriteDataSourceRouter.class);

    private Integer slaves;// slaves的个数

    public void setSlaves(Integer slaves) {
        this.slaves = slaves;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        Annotation annotation = DataSourceHolder.get();
        if (annotation == null || annotation.annotationType() == Master.class) {
            return "WRITE";
        }
        logger.info("TypedReadWriteDataSourceRouter set datasource [salve]");
        
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return "READ_" + random.nextInt(slaves);
    }

    static class DataSourceHolder {
        private static final ThreadLocal<Annotation> holder = new ThreadLocal<Annotation>();

        public static Annotation get() {
            return holder.get();
        }

        public static void set(Annotation context) {
            holder.set(context);
        }

        public static void clear() {
            holder.remove();
        }
    }
}
