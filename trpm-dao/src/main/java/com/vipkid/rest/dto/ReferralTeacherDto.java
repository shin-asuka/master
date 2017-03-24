package com.vipkid.rest.dto;

import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

public class ReferralTeacherDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4810715305305540153L;

	private Long id;
	
	private String name = "";
	
	private String email = "";
	
	private Date applyDate;
	
	private String lifeCycle = "";
	
	private String status = "";
	
	private String result = "";
	
	private Date regularDate;
	
	private Date scheduledDateTime;

	private String nextStep;//下一步流程，进行中的需要显示

	private String applyDateFormatter;

	private String scheduledDateFormatter;

	private String regularDateFormatter;

	public static String YMD_ZN = "yyyy/MM/dd";

	public static String REGEX = "(\\w{1})(\\w+)(@\\w+)";

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		if (StringUtils.equalsIgnoreCase(TeacherEnum.LifeCycle.REGULAR.toString(), lifeCycle)
				&& email != null && email.contains("@")){
			return email.replaceAll(REGEX, "$1***$3");
		}
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getApplyDate() {
		return applyDate == null ? "" : DateFormatUtils.format(applyDate, YMD_ZN);
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public String getLifeCycle() {
		if (StringUtils.isNotBlank(lifeCycle)) {
			try {
				return TeacherEnum.LifeCycle.valueOf(lifeCycle).getVal();
			} catch (Exception e) {}
		} else {
			return "";
		}
		return lifeCycle;
	}

	public void setLifeCycle(String lifeCycle) {
		this.lifeCycle = lifeCycle;
	}

	public String getStatus() {
		if (StringUtils.isNotBlank(status)) {
			try {
				return TeacherApplicationEnum.Status.valueOf(status).getVal();
			} catch (Exception e) {}
		} else {
			return "";
		}
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResult() {
		if (StringUtils.isNotBlank(result)) {
			try {
				return TeacherApplicationEnum.Result.valueOf(result).getVal();
			} catch (Exception e) {}
		} else {
			return "";
		}
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getRegularDate() {
		return regularDate == null ? "" : DateFormatUtils.format(regularDate, YMD_ZN);
	}

	public void setRegularDate(Date regularDate) {
		this.regularDate = regularDate;
	}

	public String getScheduledDateTime() {
		return scheduledDateTime == null ? "" : DateFormatUtils.format(scheduledDateTime, YMD_ZN);
	}

	public void setScheduledDateTime(Date scheduledDateTime) {
		this.scheduledDateTime = scheduledDateTime;
	}

	public String getNextStep() {
		return StringUtils.isBlank(nextStep) ? "" : nextStep;
	}

	public void setNextStep(String nextStep) {
		this.nextStep = nextStep;
	}

	public String getApplyDateFormatter() {
		return applyDate == null ? "" : DateFormatUtils.format(applyDate, "MMM, dd yyyy", Locale.ENGLISH);
	}

	public String getScheduledDateFormatter() {
		return scheduledDateTime == null ? "" : DateFormatUtils.format(scheduledDateTime, "MMM, dd yyyy", Locale.ENGLISH);
	}

	public String getRegularDateFormatter() {
		return regularDate == null ? "" : DateFormatUtils.format(regularDate, "MMM, dd yyyy", Locale.ENGLISH);
	}
}
