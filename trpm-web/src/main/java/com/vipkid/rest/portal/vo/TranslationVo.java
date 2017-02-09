package com.vipkid.rest.portal.vo;

/**
 * Created by LP-813 on 2017/2/9.
 */
public class TranslationVo {

    private Long id;
    private String text;
    private String time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
