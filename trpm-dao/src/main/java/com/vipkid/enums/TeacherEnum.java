package com.vipkid.enums;

public class TeacherEnum {

	public enum Currency {
		US_DOLLAR, // 美元
		CANADIAN_DOLAR, // 加元
		CNY // 人民币
	}

	public enum Certificate {
		TESOL, // 英语教育认证
		TEFL // 作为外语的英语教学认证
	}

	public enum Type {
		FULL_TIME, // 全职
		PART_TIME, // 兼职
		TEST // test
	}

	public enum LifeCycle {
		SIGNUP, // 注册 + 收集基本信息
		BASIC_INFO, // 2015-08-08 添加basic-info 状态，从signup分离
		INTERVIEW, // 面试
		SIGN_CONTRACT, // 签合同
		TRAINING, // 教师培训
		PRACTICUM, // 试讲
		REGULAR, // 成为正式老师Ø
		QUIT, // 离职
		FAIL // 被剔除的老师，永不录用的那种

	}

	public enum RecruitmentChannel {
		CHEGG, // Chegg渠道
		STAFF_REFERAL, // 员工推荐
		TEACHER_REFERAL, // 老师推荐
		SELF_REFERAL, // 自荐
		PARTNER_JON, // Partner：Jon
		PARTNER_RAYMOND, // Partner：Raymond
		PARTNER_JOY_XU, // Partner：Joy Xu
		PARTNER_RYAN_TAN, // Partner：Ryan Tan
		PARTNER_HELEN, // Partner：Helen
		PARTNER_NEISSA, // Partner：Neissa
		OTHER // 其他
	}

	public enum Hide {
		ALL, SCHEDULE, TRIAL, NONE
	}

	public enum PageEnum {
		SIGNUP,INFOMATION,RESULT
	}
}
