package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

/**
 * @author zouqinghua
 * @date 2016年9月22日  下午8:34:57
 *
 */
public class Staff extends Entity implements Serializable{

	private static final long serialVersionUID = 5695165745733040096L;
	private Long id;
	private String email;		// email
	private String englishName;		// english_name
	private String mobile;		// mobile
	private Long salesTeamId;		// sales_team_id
	
	public Staff() {
	}

	
	public Staff(Long id) {
		super();
		this.id = id;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Long getSalesTeamId() {
		return salesTeamId;
	}

	public void setSalesTeamId(Long salesTeamId) {
		this.salesTeamId = salesTeamId;
	}
	
	

}
