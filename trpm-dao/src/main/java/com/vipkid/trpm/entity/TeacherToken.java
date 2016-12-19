package com.vipkid.trpm.entity;

import org.community.dao.support.Entity;

import java.io.Serializable;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2016/12/13 下午12:33
 */
public class TeacherToken extends Entity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /*  */
    private Long teacherId;
    /*  */
    private String appToken;


    public TeacherToken() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }
}
