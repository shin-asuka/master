package com.vipkid.portal.classroom.model.mockclass;

import java.util.List;

public class PeReviewOutputDto extends PeDto {

    private List<PeRubricDto> rubricList;

    private Integer totalScore;

    public List<PeRubricDto> getRubricList() {
        return rubricList;
    }

    public void setRubricList(List<PeRubricDto> rubricList) {
        this.rubricList = rubricList;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

}
