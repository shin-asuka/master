package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class AssessmentReport extends Entity implements Serializable {

    private static final long serialVersionUID = 6259249294914061255L;
    private long id;
    private java.sql.Timestamp createDateTime;
    private java.sql.Timestamp updateDateTime; // 更新时间
    private java.sql.Timestamp uploadDateTime; // 上传时间
    private String name;
    private int readed;
    private String url;
    private long studentId;
    private int score;
    private long onlineClassId;
    
    //扩展字段
    private Boolean hasUnitAssessment; //是否上传ua报告并审核是通过
        
    public long getId() {
        return this.id;
    }

    public AssessmentReport setId(long id) {
        this.id = id;
        return this;
    }
        
    public java.sql.Timestamp getCreateDateTime() {
        return this.createDateTime;
    }

    public AssessmentReport setCreateDateTime(java.sql.Timestamp createDateTime) {
        this.createDateTime = createDateTime;
        return this;
    }
        
    public java.sql.Timestamp getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(java.sql.Timestamp updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

	public String getName() {
        return this.name;
    }

    public AssessmentReport setName(String name) {
        this.name = name;
        return this;
    }
        
    public int getReaded() {
        return this.readed;
    }

    public AssessmentReport setReaded(int readed) {
        this.readed = readed;
        return this;
    }
        
    public String getUrl() {
        return this.url;
    }

    public AssessmentReport setUrl(String url) {
        this.url = url;
        return this;
    }
        
    public long getStudentId() {
        return this.studentId;
    }

    public AssessmentReport setStudentId(long studentId) {
        this.studentId = studentId;
        return this;
    }

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public long getOnlineClassId() {
		return onlineClassId;
	}

	public AssessmentReport setOnlineClassId(long onlineClassId) {
		this.onlineClassId = onlineClassId;
		return this;
	}

	public Boolean getHasUnitAssessment() {
		return hasUnitAssessment;
	}

	public void setHasUnitAssessment(Boolean hasUnitAssessment) {
		this.hasUnitAssessment = hasUnitAssessment;
	}

	public java.sql.Timestamp getUploadDateTime() {
		return uploadDateTime;
	}

	public void setUploadDateTime(java.sql.Timestamp uploadDateTime) {
		this.uploadDateTime = uploadDateTime;
	}
	
	
}
