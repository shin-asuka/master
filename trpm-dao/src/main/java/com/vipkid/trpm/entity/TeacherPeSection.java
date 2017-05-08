package com.vipkid.trpm.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.community.dao.support.Entity;

import java.io.Serializable;

public final class TeacherPeSection extends Entity implements Serializable {

    private static final long serialVersionUID = -4848521248780132565L;
    /*  */
    private Integer id;
    /*  */
    private Integer rubricId;
    /*  */
    private String name;
    /*  */
    private Integer seq;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRubricId() {
        return rubricId;
    }

    public void setRubricId(Integer rubricId) {
        this.rubricId = rubricId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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