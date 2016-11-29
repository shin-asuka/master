package com.vipkid.trpm.entity.teachercomment;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2016/11/28 下午7:06
 */
public class StudentAbilityUnitRule {

    /* 主键 */
    private Integer unitId;
    /* 名称 */
    private String unitName;
    /* 阅读能力 */
    private String reading;
    /* 听说能力 */
    private String speakingListening;

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
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
}
