package com.vipkid.trpm.entity;

import org.community.dao.support.Entity;

import java.io.Serializable;

public final class TeacherTags extends Entity implements Serializable {

    private static final long serialVersionUID = 6446541773668144303L;
    /*  */
    private Integer id;
    /*  */
    private Integer tagId;
    /*  */
    private Integer teacherId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

}