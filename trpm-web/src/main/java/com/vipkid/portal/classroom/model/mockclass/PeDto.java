package com.vipkid.portal.classroom.model.mockclass;

import java.util.List;

public class PeDto {

    private List<Integer> tagsList;

    private List<Integer> levelsList;

    private String thingsDidWell;

    private String areasImprovement;

    private Integer toCoordinator;

    private String toCoordinatorComment;

    private Integer teachTrailClass;

    private Integer applicationId;

    private String status;

    private String stateReason;

    public String getStateReason() {
        return stateReason;
    }

    public void setStateReason(String stateReason) {
        this.stateReason = stateReason;
    }

    public List<Integer> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<Integer> tagsList) {
        this.tagsList = tagsList;
    }

    public List<Integer> getLevelsList() {
        return levelsList;
    }

    public void setLevelsList(List<Integer> levelsList) {
        this.levelsList = levelsList;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

}
