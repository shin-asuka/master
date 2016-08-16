package com.vipkid.trpm.entity.report;

import java.util.List;

public class Report {

	private String reportName;

	private List<ReportOption> options;

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public List<ReportOption> getOptions() {
		return options;
	}

	public void setOptions(List<ReportOption> options) {
		this.options = options;
	}

}
