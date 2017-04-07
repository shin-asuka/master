package com.vipkid.background.dto.input;


import java.io.Serializable;

public class BackgroundCheckCaInputDto implements Serializable{

    private static final long serialVersionUID = 9201707707558364386L;
    private Long teacherId;

    private String cpicUrl;

    private String id2Url;


    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getCpicUrl() {
        return cpicUrl;
    }

    public void setCpicUrl(String cpicUrl) {
        this.cpicUrl = cpicUrl;
    }

    public String getId2Url() {
        return id2Url;
    }

    public void setId2Url(String id2Url) {
        this.id2Url = id2Url;
    }
}
