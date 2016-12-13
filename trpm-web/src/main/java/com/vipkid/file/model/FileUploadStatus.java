package com.vipkid.file.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * TIS 返回的 file upload 结果
 */
public class FileUploadStatus implements Serializable {

    private static final long serialVersionUID = -265263423308348563L;

    private Long id;
    private Integer status;
    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}