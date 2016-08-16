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
public class StudentMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private Long createDateTime;
	
	public StudentMessage() {
		
	}
	
	public StudentMessage(Long id) {
		super();
		this.id = id;
	}

	public StudentMessage(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Long createDateTime) {
		this.createDateTime = createDateTime;
	}

}
