package com.vipkid.trpm.entity.teachercomment;

import java.util.List;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2016/11/28 下午7:06
 */
public class StudentAbilityLevelRule {

    /* 主键 */
    private Integer levelId;
    /* 名称 */
    private String levelName;
    /* 阅读能力 */
    private String reading;
    /* 听说能力 */
    private String speakingListening;

    private List<StudentAbilityUnitRule> units;

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public String getSpeakingListening() {
        return speakingListening;
    }

    public void setSpeakingListening(String speakingListening) {
        this.speakingListening = speakingListening;
    }

    public List<StudentAbilityUnitRule> getUnits() {
        return units;
    }

    public void setUnits(List<StudentAbilityUnitRule> units) {
        this.units = units;
    }
}
