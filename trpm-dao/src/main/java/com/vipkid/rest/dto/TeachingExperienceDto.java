package com.vipkid.rest.dto;

import com.vipkid.rest.validation.annotation.Ignore;
import com.vipkid.rest.validation.annotation.Verify;
import com.vipkid.rest.validation.tools.ValidationEnum.Type;

@Verify
public class TeachingExperienceDto{
    
    @Ignore
    private Long id;
    
    private String organisationName;
    
    private String jobTitle;
    
    private Long timePeriodStart;
    
    private Long timePeriodEnd;
    
    private Float hoursWeek;
    
    @Verify(type={Type.NOT_NULL,Type.MAX_LENGTH},maxLength=2000)
    private String jobDescription;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;

    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;

    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;

    }

    public Long getTimePeriodStart() {
        return timePeriodStart;
    }

    public void setTimePeriodStart(Long timePeriodStart) {
        this.timePeriodStart = timePeriodStart;

    }

    public Long getTimePeriodEnd() {
        return timePeriodEnd;
    }

    public void setTimePeriodEnd(Long timePeriodEnd) {
        this.timePeriodEnd = timePeriodEnd;

    }

    public Float getHoursWeek() {
        return hoursWeek;
    }

    public void setHoursWeek(Float hoursWeek) {
        this.hoursWeek = hoursWeek;

    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;

    }
  
}
