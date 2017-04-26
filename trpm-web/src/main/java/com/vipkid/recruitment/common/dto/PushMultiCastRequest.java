package com.vipkid.recruitment.common.dto;

import java.util.List;

public class PushMultiCastRequest {

    private List<Long> target;

    private String title;

    private String body;

    private String jump_link;

    private String priority;

    private long expires_at;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getJump_link() {
        return jump_link;
    }

    public void setJump_link(String jump_link) {
        this.jump_link = jump_link;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public long getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(long expires_at) {
        this.expires_at = expires_at;
    }

    public List<Long> getTarget() {
        return target;
    }

    public void setTarget(List<Long> target) {
        this.target = target;
    }

}
