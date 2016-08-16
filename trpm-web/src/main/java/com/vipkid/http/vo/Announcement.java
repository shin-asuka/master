package com.vipkid.http.vo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 教师公告对象
 * 
 * @author zouqinghua
 * @date 2016年7月15日  下午2:42:42
 *
 */
public class Announcement implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public enum Status{
		INVALID ,//已失效
		AVAILABLE//可用的
	}
	
	private Long id;
	private String content;		// 内容
	private String links;		// 链接
	private Timestamp publishTime;		// 提交时间
	private Integer validDate;		// 有效天数
	private Integer status;		// 状态{1 有效, 0 无效 }
	private Timestamp inValidDate; //失效日期
	
	public Announcement() {
	}

	public Announcement(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLinks() {
		return links;
	}

	public void setLinks(String links) {
		this.links = links;
	}

	public Timestamp getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Timestamp publishTime) {
		this.publishTime = publishTime;
	}

	public Integer getValidDate() {
		return validDate;
	}

	public void setValidDate(Integer validDate) {
		this.validDate = validDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Timestamp getInValidDate() {
		return inValidDate;
	}

	public void setInValidDate(Timestamp inValidDate) {
		this.inValidDate = inValidDate;
	}

	@Override
	public String toString() {
		return id==null?"":String.valueOf(id);
	}
}
