package com.vipkid.portal.activity.dto;

import com.vipkid.teacher.tools.utils.validation.annotaion.Ignore;
import com.vipkid.teacher.tools.utils.validation.annotaion.NotNull;

@NotNull
public class DrawUserDto {

	private Integer page;
	
	@Ignore
	private Integer pageSize = 20;

	public Integer getPage() {
		return page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

}
