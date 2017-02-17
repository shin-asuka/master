package com.vipkid.common.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.google.api.client.util.Lists;
import org.community.config.PropertyConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liyang on 2017/2/15.
 */
public class AlertLogFilter extends Filter<ILoggingEvent> {

    private static final List<String> nonFilterEnvNames = new ArrayList<>(Arrays.asList("preonline", "production"));




    @Override
    public FilterReply decide(ILoggingEvent event) {

        String envName = PropertyConfigurer.stringValue("environment.name");

        if(!nonFilterEnvNames.contains(envName)){
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



        return FilterReply.ACCEPT;
    }

}
