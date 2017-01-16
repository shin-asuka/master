package com.vipkid.trpm.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.community.dao.support.Entity;

import java.io.Serializable;

public final class Tags extends Entity implements Serializable {

    private static final long serialVersionUID = 6416186142106776065L;
    /*  */
    private int id;
    /*  */
    private String name;
    /*  */
    private String chineseDescribe;
    /* 1:tag 2:group */
    private int type;
    /*  */
    private String described;
    /* 0 是根，顶层 */
    private int parentId;
    /*  */
    private int createrId;
    /*  */
    private java.sql.Timestamp createTime;
    /*  */
    private java.sql.Timestamp updateTime;
    /*  */
    private int updateId;
    /*  */
    private long version;

    public int getId() {
        return this.id;
    }

    public Tags setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Tags setName(String name) {
        this.name = name;
        return this;
    }

    public String getChineseDescribe() {
        return this.chineseDescribe;
    }

    public Tags setChineseDescribe(String chineseDescribe) {
        this.chineseDescribe = chineseDescribe;
        return this;
    }

    public int getType() {
        return this.type;
    }

    public Tags setType(int type) {
        this.type = type;
        return this;
    }

    public String getDescribed() {
        return this.described;
    }

    public Tags setDescribed(String described) {
        this.described = described;
        return this;
    }

    public int getParentId() {
        return this.parentId;
    }

    public Tags setParentId(int parentId) {
        this.parentId = parentId;
        return this;
    }

    public int getCreaterId() {
        return this.createrId;
    }

    public Tags setCreaterId(int createrId) {
        this.createrId = createrId;
        return this;
    }

    public java.sql.Timestamp getCreateTime() {
        return this.createTime;
    }

    public Tags setCreateTime(java.sql.Timestamp createTime) {
        this.createTime = createTime;
        return this;
    }

    public java.sql.Timestamp getUpdateTime() {
        return this.updateTime;
    }

    public Tags setUpdateTime(java.sql.Timestamp updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public int getUpdateId() {
        return this.updateId;
    }

    public Tags setUpdateId(int updateId) {
        this.updateId = updateId;
        return this;
    }

    public long getVersion() {
        return this.version;
    }

    public Tags setVersion(long version) {
        this.version = version;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
