package com.vipkid.trpm.entity.media;

import java.io.Serializable;

public class UploadResult implements Serializable {

	private static final long serialVersionUID = 1L;
	// 文件名 ，如：filename.png
	private String name;
	// 相对url: /file/filename.png
	private String url;
	// 信息提示
	private String msg;
	/* 上传结果： 成功、失败 */
	private boolean result;
	
	/* url编码后的url*/
	private String encodeUrl;
	
	

	public String getEncodeUrl() {
        return encodeUrl;
    }

    public void setEncodeUrl(String encodeUrl) {
        this.encodeUrl = encodeUrl;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

}
