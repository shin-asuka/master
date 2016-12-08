/**
 * 
 */
package com.vipkid.http.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * 教师生活照
 * @author zouqinghua
 * @date 2016年11月10日  上午11:03:45
 *
 */
public class AppVideo implements Serializable{

	private static final long serialVersionUID = 599275727559781725L;

	private Long id;  //视频ID
	private String url; //视频url
	private Integer status; //视频处理状态
	private String previewImageUrl; //预览图片url
	
	public AppVideo() {
	}

	public AppVideo(Long id) {
		super();
		this.id = id;
	}

	public AppVideo(Long id, String url) {
		this.id = id;
		this.url = url;
	}
	
	@JsonIgnore
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPreviewImageUrl() {
		return previewImageUrl;
	}

	public void setPreviewImageUrl(String previewImageUrl) {
		this.previewImageUrl = previewImageUrl;
	}
	
}
