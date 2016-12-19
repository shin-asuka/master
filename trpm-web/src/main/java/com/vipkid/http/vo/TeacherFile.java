package com.vipkid.http.vo;

import java.io.Serializable;
import java.util.List;

import com.vipkid.file.model.AppLifePicture;

/**
 * 教师文件信息
 * 
 * @author zouqinghua
 * @date 2016年12月7日  下午8:01:28
 *
 */
public class TeacherFile implements Serializable{

	private static final long serialVersionUID = 4470553818191937227L;
	
	private Long teacherId;
	private String avatar;
	private List<AppLifePicture> lifePictures;
	private AppVideo shortVideo;
	
	public TeacherFile() {
	}
	public TeacherFile(Long teacherId) {
		this.teacherId = teacherId;
	}
	
	public Long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}

	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public List<AppLifePicture> getLifePictures() {
		return lifePictures;
	}

	public void setLifePictures(List<AppLifePicture> lifePictures) {
		this.lifePictures = lifePictures;
	}

	public AppVideo getShortVideo() {
		return shortVideo;
	}

	public void setShortVideo(AppVideo shortVideo) {
		this.shortVideo = shortVideo;
	}

	
}
