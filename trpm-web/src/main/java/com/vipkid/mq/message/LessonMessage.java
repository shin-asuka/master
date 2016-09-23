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
public class LessonMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String serialNumber;
	private String name;
	private String number;
    /**
     * 是否生成UA报告
     */
    private Integer isUnitAssessment;

    public LessonMessage() {
		
	}
	
	public LessonMessage(Long id) {
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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

    public Integer getIsUnitAssessment() {
        return isUnitAssessment;
    }

    public void setIsUnitAssessment(Integer isUnitAssessment) {
        this.isUnitAssessment = isUnitAssessment;
    }
}
