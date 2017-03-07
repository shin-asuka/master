package com.vipkid.portal.classroom.model.mockclass;

import java.io.Serializable;

public class PeOptionDto implements Serializable {

    private static final long serialVersionUID = 1907140565287436165L;
    /*  */
    private Integer id;
    /*  */
    private String description;
    /*  */
    private Integer points;
    /*  */
    private Integer seq;

    private Boolean checked;

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

}
