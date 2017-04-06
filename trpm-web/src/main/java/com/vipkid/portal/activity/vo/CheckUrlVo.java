package com.vipkid.portal.activity.vo;

import com.alibaba.fastjson.JSONObject;

public class CheckUrlVo {

	private String examVersion;
	
	private JSONObject pageContent;
	
	private Long level;

	public String getExamVersion() {
		return examVersion;
	}

	public JSONObject getPageContent() {
		return pageContent;
	}

	public void setExamVersion(String examVersion) {
		this.examVersion = examVersion;
	}

	public void setPageContent(JSONObject pageContent) {
		this.pageContent = pageContent;
	}

	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
	}
	
}
