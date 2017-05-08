package com.vipkid.portal.classroom.model.mockclass;

import java.io.Serializable;
import java.util.List;

public class PeSectionDto implements Serializable {

    private static final long serialVersionUID = -4848521248780132565L;
    /*  */
    private Integer id;
    /*  */
    private String name;
    /*  */
    private Integer seq;

    private List<PeCriteriaDto> criteriaList;

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

    public List<PeCriteriaDto> getCriteriaList() {
        return criteriaList;
    }

    public void setCriteriaList(List<PeCriteriaDto> criteriaList) {
        this.criteriaList = criteriaList;
    }

}
