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
public class TeacherMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String serialNumber;
	private String realName;
	private String email;
	private String contractType;
	private Integer basePay;
	
	public TeacherMessage() {
		
	}
	
	public TeacherMessage(Long id) {
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

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public Integer getBasePay() {
		return basePay;
	}

	public void setBasePay(Integer basePay) {
		this.basePay = basePay;
	}

}
