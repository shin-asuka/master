package com.vipkid.background.dto.input;


import java.io.Serializable;

public class BackgroundCheckCaInputDto implements Serializable{
    private static final long serialVersionUID = 2932585128429730715L;

    private Long teacherId;

    private String ipicUrl;

    private String id2Url;


    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getIpicUrl() {
        return ipicUrl;
    }

    public void setIpicUrl(String ipicUrl) {
        this.ipicUrl = ipicUrl;
    }

    public String getId2Url() {
        return id2Url;
    }

    public void setId2Url(String id2Url) {
        this.id2Url = id2Url;
    }
}
