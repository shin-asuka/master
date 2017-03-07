package com.vipkid.trpm.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.community.dao.support.Entity;

import java.io.Serializable;

public final class TeacherPeCriteria extends Entity implements Serializable {

    private static final long serialVersionUID = 2954254633202906431L;
    /*  */
    private Integer id;
    /*  */
    private Integer sectionId;
    /*  */
    private String title;
    /* 取值: input|radio|checkbox|select */
    private String type;
    /*  */
    private Integer points;
    /* 取值: 0不参与计算|1参与计算 */
    private Integer calculated;
    /*  */
    private Integer seq;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getCalculated() {
        return calculated;
    }

    public void setCalculated(Integer calculated) {
        this.calculated = calculated;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
