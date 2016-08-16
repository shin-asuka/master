package com.vipkid.trpm.util;

import org.junit.Test;
import org.springframework.web.util.HtmlUtils;

public class HtmlFilter {

    @Test
    public void converHtml(){
        String value = "<a>a</a><b>v<c>";
        System.out.println("源字符:"+value);
        value = HtmlUtils.htmlEscape(value);
        System.out.println("转化后:"+value);
        System.out.println("最终展示:" + value.replace("&gt;", ">").replace("&lt;", "<"));
    }
    
}
