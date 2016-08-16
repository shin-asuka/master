/**
 * 
 */
package com.vipkid.mq.message;

import java.io.Serializable;

/**
 * @author zouqinghua
 * @date 2016年5月6日  下午3:47:32
 *
 */
public class CourseMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String serialNumber;
	private String name;
	private String type;
	
	public CourseMessage() {
		
	}
	
	public CourseMessage(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	

}
