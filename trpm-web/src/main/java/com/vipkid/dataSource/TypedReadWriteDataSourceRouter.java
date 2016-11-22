package com.vipkid.dataSource;

import com.vipkid.dataSource.annotation.Master;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.annotation.Annotation;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by liuguanqing on 16/5/10.
 * 基于@Master，@Slave注释类型的读写分离
 * 只有注释的接口才会读写分离，其他的默认仍然是master
 */
public class TypedReadWriteDataSourceRouter extends AbstractRoutingDataSource {

    private Integer slaves;//slaves的个数

    public void setSlaves(Integer slaves) {
        this.slaves = slaves;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        Annotation annotation = DataSourceHolder.get();
        //默认还是master；等测试成熟以后，可以全量RW分离
        if(annotation == null || !isReadOnly) {
            return "WRITE";
        }

        if(annotation.annotationType() == Master.class) {
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
