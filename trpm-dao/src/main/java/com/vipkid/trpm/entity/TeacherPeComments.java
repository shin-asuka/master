package com.vipkid.trpm.entity;

import com.vipkid.rest.validation.annotation.Length;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.community.dao.support.Entity;

import java.io.Serializable;

public final class TeacherPeComments extends Entity implements Serializable {

    private static final long serialVersionUID = 6611553613271690756L;
    /*  */
    private Integer id;
    /*  */
    private Integer applicationId;
    /*  */
    private String thingsDidWell;
    /*  */
    private String areasImprovement;
    /*  */
    private Integer totalScore;
    /*  */
    private String status;

    private Integer toCoordinator;
    /*  */
    private String toCoordinatorComment;
    /*  */
    private Integer teachTrailClass;
    /*  */
    private String stateReason;
    /*  */
    private Integer templateId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
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

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getStateReason() {
        return stateReason;
    }

    public void setStateReason(String stateReason) {
        this.stateReason = stateReason;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
