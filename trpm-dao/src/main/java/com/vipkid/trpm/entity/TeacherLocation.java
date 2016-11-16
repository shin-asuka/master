package com.vipkid.trpm.entity;

import java.io.Serializable;

import org.community.dao.support.Entity;

public class TeacherLocation extends Entity implements Serializable {

	private static final long serialVersionUID = -7569795341004821768L;
	/* 主键 */
	private int id;
	/* 父ID */
	private int parentId = -1;
	/* 名称 */
	private String name;
	/* 描述 */
	private String description;
	/* 排序 */
	private int sort;
	/* 级别,1 国家 2 省市/州 3 城市 */
	private int level;
	/* 备注说明 */
	private String comment;
	
	private String timezone;

	public int getId() {
		return this.id;
	}

	public TeacherLocation setId(int id) {
		this.id = id;
		return this;
	}

	public int getParentId() {
		return this.parentId;
	}

	public TeacherLocation setParentId(int parentId) {
		this.parentId = parentId;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public TeacherLocation setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return this.description;
	}

	public TeacherLocation setDescription(String description) {
		this.description = description;
		return this;
	}

	public int getSort() {
		return this.sort;
	}

	public TeacherLocation setSort(int sort) {
		this.sort = sort;
		return this;
	}

	public int getLevel() {
		return this.level;
	}

	public TeacherLocation setLevel(int level) {
		this.level = level;
		return this;
	}

	public String getComment() {
		return this.comment;
	}

	public TeacherLocation setComment(String comment) {
		this.comment = comment;
		return this;
	}

    public String getTimezone() {
        return timezone;
    }

    public TeacherLocation setTimezone(String timezone) {
        this.timezone = timezone;
        return this;
    }

}