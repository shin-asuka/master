package com.vipkid.portal.activity.vo;

import com.alibaba.fastjson.JSONObject;

public class CheckUrlVo {

	private String examVersion;
	
	private JSONObject pageContent;

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
	
	
}
