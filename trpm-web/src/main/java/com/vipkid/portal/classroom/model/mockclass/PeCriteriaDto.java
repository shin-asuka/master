package com.vipkid.portal.classroom.model.mockclass;


import java.io.Serializable;
import java.util.List;

public class PeCriteriaDto implements Serializable {

    private static final long serialVersionUID = 2954254633202906431L;
    /*  */
    private Integer id;
    /*  */
    private String title;
    /* 取值: input|radio|checkbox|select */
    private String type;
    /*  */
    private Integer points;
    /*  */
    private Integer seq;

    private List<PeOptionDto> optionList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public List<PeOptionDto> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<PeOptionDto> optionList) {
        this.optionList = optionList;
    }

}
