package com.vipkid.common.log;

import ch.qos.logback.classic.html.HTMLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;

/**
 * Created by liyang on 2017/2/15.
 */
public class HTMLWrapperLayout extends HTMLLayout {


    /*
     * 使用策略模式封装wrapper的策略方式
     */
    private interface WrapperHtmlStrategy{
        public StringBuilder wrap(String content,ILoggingEvent event);
    }

    private abstract class BaseStrategy implements WrapperHtmlStrategy{

        /* 这里无需使用缓存，因为内部实现已经帮你考虑到了！他缓存的是cachedLocalHost */
        protected String getHostName(){

            try {
                return InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ignored) {}

            return "unknown-host";
        }

        protected String getHostAddress(){

            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ignored) {}

            return "unknown-host";
        }

        protected StringBuilder decorateColorFont(StringBuilder content,String color,int fontSize){
            StringBuilder builder = new StringBuilder();
            builder.append("<font size=\"")
                    .append(fontSize)
                    .append("\" color=\"")
                    .append(color)
                    .append("\">")
                    .append(content)
                    .append("</font>");
            return builder;
        }

        protected StringBuilder decorateStrong(StringBuilder content){
            StringBuilder builder = new StringBuilder();
            builder.append("<strong>")
                    .append(content)
                    .append("</strong>");
            return builder;
        }
    }


    private class DefaultStrategy extends BaseStrategy{

        @Override
        public StringBuilder wrap(String content,ILoggingEvent event) {
            StringBuilder builder = new StringBuilder();

            builder.append(this.getHostName())
                    .append(" @ [ ")
                    .append(this.getHostAddress())
                    .append(" ] ");
            if(event.getThreadName() != null){
                builder.append("[").append(event.getThreadName()).append("]");
            }



            StackTraceElement[] stackTraceElements = event.getCallerData();

            int stackTraceCount = 3;
            boolean stackTraceQunarOnly = true;

            if(stackTraceElements != null && stackTraceElements.length > 0){
                builder.append("<br><br>");
                int stackLength = stackTraceElements.length > stackTraceCount?stackTraceCount:stackTraceElements.length;
                for(int i=0;i<stackLength;i++){
                    if(stackTraceQunarOnly){
                        if(stackTraceElements[i].getClassName().contains("vipkid")){
                            String sourcePath = stackTraceElements[i].toString();
                            builder.append("<br>").append(sourcePath);
                        }
                    }else{
                        String sourcePath = stackTraceElements[i].toString();
                        builder.append("<br>").append(sourcePath);
                    }
                }
            }
            return decorateColorFont(builder, "red", 3)
                    .append("<br><br>")
                    .append(content);
        }
    }

    private WrapperHtmlStrategy wrapper = new DefaultStrategy();

    /*
     * 覆盖doLayout方法，对父类生成的html模板进行wrap从而生成期望的html日志内容
     */
    @Override
    public String doLayout(ILoggingEvent event) {
        event.getThreadName();
        StringBuilder builder = wrapper.wrap(super.doLayout(event),event);
        return builder.toString();
    }


}
