package com.vipkid.trpm.util;

import org.junit.Test;
import org.springframework.web.util.HtmlUtils;

import com.vipkid.recruitment.utils.ReturnMapUtils;

public class HtmlFilter {

    //@Test
    public void converHtml(){
        String value = "<div>hello world</div><p>&nbsp;</p>";
        System.out.println("源字符:"+value);
        System.out.println("转化后:"+HtmlUtils.htmlEscape(value));
        System.out.println("还原字符:"+HtmlUtils.htmlUnescape(HtmlUtils.htmlEscape(value)));
        System.out.println("还原字符:"+HtmlUtils.htmlUnescape(value));
    }

    @Test
    public void name() {
        ReturnMapUtils.returnFail("ERROR");
    }
    
}
