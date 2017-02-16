package com.vipkid.common.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Created by liyang on 2017/2/15.
 */
public class AlertLogFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {

        if(event.getLevel().levelInt < Level.ERROR_INT){
            return FilterReply.DENY;
        }

        boolean notifyOn = true;
        if(!notifyOn){
            return FilterReply.DENY;
        }

        if (event.getLoggerName().equals("org.apache.commons.httpclient.HttpMethodBase") &&
                event.getMessage().contains("Using getResponseBodyAsStream instead is recommended.")) {
            return FilterReply.DENY;
        }



        return FilterReply.ACCEPT;
    }

}
