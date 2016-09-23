/**
 * 
 */
package com.vipkid.mq.message;

import java.io.Serializable;

/**
 * @author zouqinghua
 * @date 2016年5月6日  下午3:47:32
 *
 */
public class OnlineClassMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long scheduledDateTime;
	private Long createDateTime;
	private Long studentEnrollmentTime;
	private Long uaCreateDateTime; //ua报告创建时间
	private Long uaUpdateDateTime; //ua报告更新时间
	private Long uaUploadDateTime; //ua报告上传时间
	private Long tcUpdateDateTime; //tc评语更新时间
	private String finishType;
	private Boolean hasAssessmentReport;
	private Boolean hasComments;
	private Boolean paidForTrial;
	private Boolean isTrialOnly;
	
	private boolean shortNotice; //是否是紧急备用课程
	
	public OnlineClassMessage() {
		
	}
	
	public OnlineClassMessage(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getScheduledDateTime() {
		return scheduledDateTime;
	}

	public void setScheduledDateTime(Long scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
	}

	public Long getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Long createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Long getStudentEnrollmentTime() {
		return studentEnrollmentTime;
	}

	public void setStudentEnrollmentTime(Long studentEnrollmentTime) {
		this.studentEnrollmentTime = studentEnrollmentTime;
	}

	public String getFinishType() {
		return finishType;
	}

	public void setFinishType(String finishType) {
		this.finishType = finishType;
	}

	public Boolean getHasAssessmentReport() {
		return hasAssessmentReport;
	}

	public void setHasAssessmentReport(Boolean hasAssessmentReport) {
		this.hasAssessmentReport = hasAssessmentReport;
	}

	public Boolean getHasComments() {
		return hasComments;
	}

	public void setHasComments(Boolean hasComments) {
		this.hasComments = hasComments;
	}

	public Boolean getPaidForTrial() {
		return paidForTrial;
	}

	public void setPaidForTrial(Boolean paidForTrial) {
		this.paidForTrial = paidForTrial;
	}

	public Boolean getIsTrialOnly() {
		return isTrialOnly;
	}

	public void setIsTrialOnly(Boolean isTrialOnly) {
		this.isTrialOnly = isTrialOnly;
	}

	public Long getUaUpdateDateTime() {
		return uaUpdateDateTime;
	}

	public void setUaUpdateDateTime(Long uaUpdateDateTime) {
		this.uaUpdateDateTime = uaUpdateDateTime;
	}

	public Long getTcUpdateDateTime() {
		return tcUpdateDateTime;
	}

	public void setTcUpdateDateTime(Long tcUpdateDateTime) {
		this.tcUpdateDateTime = tcUpdateDateTime;
	}

	public Long getUaUploadDateTime() {
		return uaUploadDateTime;
	}

	public void setUaUploadDateTime(Long uaUploadDateTime) {
		this.uaUploadDateTime = uaUploadDateTime;
	}

	public boolean getShortNotice() {
		return shortNotice;
	}

	public void setShortNotice(boolean shortNotice) {
		this.shortNotice = shortNotice;
	}

    public Long getUaCreateDateTime() {
        return uaCreateDateTime;
    }

    public void setUaCreateDateTime(Long uaCreateDateTime) {
        this.uaCreateDateTime = uaCreateDateTime;
    }
}
