package com.vipkid.portal.classroom.model.mockclass;

import java.util.List;

public class PeViewOutputDto {

    private List<PeRubricDto> rubricList;

    private String thingsDidWell;

    private String areasImprovement;

    private Integer toCoordinator;

    private String toCoordinatorComment;

    private Integer teachTrailClass;

    private Integer totalScore;

    private List<PeTagsDto> tagsList;

    private List<PeLevelsDto> levelsList;

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PeRubricDto> getRubricList() {
        return rubricList;
    }

    public void setRubricList(List<PeRubricDto> rubricList) {
        this.rubricList = rubricList;
    }

    public String getThingsDidWell() {
        return thingsDidWell;
    }

    public void setThingsDidWell(String thingsDidWell) {
        this.thingsDidWell = thingsDidWell;
    }

    public String getAreasImprovement() {
        return areasImprovement;
    }

    public void setAreasImprovement(String areasImprovement) {
        this.areasImprovement = areasImprovement;
    }

    public Integer getToCoordinator() {
        return toCoordinator;
    }

    public void setToCoordinator(Integer toCoordinator) {
        this.toCoordinator = toCoordinator;
    }

    public String getToCoordinatorComment() {
        return toCoordinatorComment;
    }

    public void setToCoordinatorComment(String toCoordinatorComment) {
        this.toCoordinatorComment = toCoordinatorComment;
    }

    public Integer getTeachTrailClass() {
        return teachTrailClass;
    }

    public void setTeachTrailClass(Integer teachTrailClass) {
        this.teachTrailClass = teachTrailClass;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public List<PeTagsDto> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<PeTagsDto> tagsList) {
        this.tagsList = tagsList;
    }

    public List<PeLevelsDto> getLevelsList() {
        return levelsList;
    }

    public void setLevelsList(List<PeLevelsDto> levelsList) {
        this.levelsList = levelsList;
    }

}
