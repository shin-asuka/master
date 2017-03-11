package com.vipkid.background.api.sterling.dto;

import java.io.Serializable;

/**
 * Created by liyang on 2017/3/11.
 */
public class SterlingReportLink implements Serializable {


    private static final long serialVersionUID = 7710524083167010070L;
    private String html;
    private String pdf;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }
}
