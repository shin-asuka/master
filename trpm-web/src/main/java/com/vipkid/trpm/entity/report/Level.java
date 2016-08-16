package com.vipkid.trpm.entity.report;

import java.util.List;

public class Level {

	private String name;

	private List<OptionRadio> options;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<OptionRadio> getOptions() {
		return options;
	}

	public void setOptions(List<OptionRadio> options) {
		this.options = options;
	}

}
