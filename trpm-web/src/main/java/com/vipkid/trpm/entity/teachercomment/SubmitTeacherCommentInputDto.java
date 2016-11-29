package com.vipkid.trpm.entity.teachercomment;

import java.io.Serializable;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2016/11/17 下午2:32
 */
public class SubmitTeacherCommentInputDto implements Serializable{

    private static final long serialVersionUID = -1L;

    private String teacherCommentId;
    private String teacherFeedback;
    private String tipsForOtherTeachers;

    //Major特有字段
    private String levelOfdifficulty;
    private boolean suggestAdjustment;

    //trial特有字段
    private String trialLevelResult;

    public String getTeacherCommentId() {
        return teacherCommentId;
    }

    public void setTeacherCommentId(String teacherCommentId) {
        this.teacherCommentId = teacherCommentId;
    }

    public String getTeacherFeedback() {
        return teacherFeedback;
    }

    public void setTeacherFeedback(String teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
    }

    public String getTipsForOtherTeachers() {
        return tipsForOtherTeachers;
    }

    public void setTipsForOtherTeachers(String tipsForOtherTeachers) {
        this.tipsForOtherTeachers = tipsForOtherTeachers;
    }

    public String getLevelOfdifficulty() {
        return levelOfdifficulty;
    }

    public void setLevelOfdifficulty(String levelOfdifficulty) {
        this.levelOfdifficulty = levelOfdifficulty;
    }

    public boolean isSuggestAdjustment() {
        return suggestAdjustment;
    }

    public void setSuggestAdjustment(boolean suggestAdjustment) {
        this.suggestAdjustment = suggestAdjustment;
    }

    public String getTrialLevelResult() {
        return trialLevelResult;
    }

    public void setTrialLevelResult(String trialLevelResult) {
        this.trialLevelResult = trialLevelResult;
    }
}
