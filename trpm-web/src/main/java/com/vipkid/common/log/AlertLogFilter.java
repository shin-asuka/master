package com.vipkid.common.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.google.api.client.util.Lists;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liyang on 2017/2/15.
 */
public class AlertLogFilter extends Filter<ILoggingEvent> {






    @Override
    public FilterReply decide(ILoggingEvent event) {

        String mailSwitch = PropertyConfigurer.stringValue("log.mail.alert.switch");

        if(StringUtils.equals(mailSwitch,"off")){
            return FilterReply.DENY;
        }

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

        String filterLogger = PropertyConfigurer.stringValue("logback.filter.logger");
        if(StringUtils.isNotBlank(filterLogger)){
            Iterable<String> loggerIterable = Splitter.on(";").split(filterLogger);
            for(String filterLoggerItem : loggerIterable){
                if(event.getLoggerName().contains(filterLoggerItem)){
                    return FilterReply.DENY;
                }
            }
        }





        return FilterReply.ACCEPT;
    }

}
