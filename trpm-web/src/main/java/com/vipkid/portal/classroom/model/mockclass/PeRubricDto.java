package com.vipkid.portal.classroom.model.mockclass;

import java.io.Serializable;
import java.util.List;

public class PeRubricDto implements Serializable {

    private static final long serialVersionUID = -4283203693283004056L;
    /*  */
    private Integer id;
    /*  */
    private String name;
    /*  */
    private Integer seq;

    private List<PeSectionDto> sectionList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<PeSectionDto> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<PeSectionDto> sectionList) {
        this.sectionList = sectionList;
    }

}
