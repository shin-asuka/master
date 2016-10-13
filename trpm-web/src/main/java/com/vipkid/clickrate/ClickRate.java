package com.vipkid.clickrate;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class ClickRate extends Entity implements Serializable {
	
	private long id;
	
	private String name;
	
	private String ip;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
