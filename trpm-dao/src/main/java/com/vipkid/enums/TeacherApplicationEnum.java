package com.vipkid.enums;

public class TeacherApplicationEnum {
    
	public enum Status {

		SIGNUP("Sign Up"), // 新申请

		BASIC_INFO("Basic Info"), // 2015-08-08 添加basic-info 状态，从signup分离

		INTERVIEW("Interview"), //面试

		TRAINING("Teaching Prep"), // 教师培训

		SIGN_CONTRACT("Sign Contract"), //签合同

		PRACTICUM("Mock Class"),//试讲

		CONTRACT_INFO("Contract Info"), //新增

		CANCELED("Canceled"), //已取消

		FINISHED("Finished"); // 已结束

		private String value;

		Status(String value) {
			this.value = value;
		}

		public String getVal() {
			return this.value;
		}
	}
	
	public enum Result {
		PASS("Pass"),

		FAIL("Fail"),

		REAPPLY("Reapply"),//重新申请,继续上PRACTICUM1（由于客观原因没能完成面试）

		PRACTICUM("Mock Class"),

		PRACTICUM2("Mock Class 2"),//第一次面试没通过，上PRACTICUM2

		TBD("TBD"),

		TBD_FAIL("TBD Fail"),

		CANCEL("Cancel");

		private String value;

		Result(String value) {
			this.value = value;
		}

		public String getVal() {
			return this.value;
		}
	}
	
    public enum AuditStatus {
        TO_AUDIT, // 待审核
        TO_SUBMIT, // 待提交
        TO_CLASS, // 待上课
        HAS_TIMEOUT //过期
    }

	/**
	 * //教师证书的类型
	 * 1-other_degrees
	 * 2-certificationFiles
	 * 3-Identification
	 * 4-Diploma
	 * 5-Contract
	 * 6-Passport
	 * 7-Driver's license
	 * 8-W9
	 * 9-US background check
	 * 10-CANADA CPIC form background check
	 * 11-CANADA ID2 background check
	 */
 	public enum ContractFileType {
		OTHER_DEGREES(1),
	    CERTIFICATIONFILES(2),
	    IDENTIFICATION(3),
	    DIPLOMA(4),
	    CONTRACT(5),
	    PASSPORT(6),
	    DRIVER(7),
	    CONTRACT_W9(8),
		US_BACKGROUND_CHECK(9),
		CANADA_BACKGROUND_CHECK_CPIC_FORM(10),
		CANADA_BACKGROUND_CHECK_ID2(11);

	private Integer val;

	private ContractFileType(Integer val) {
		this.val = val;
	}
	public Integer val() {
		return val;
	}
	}
}
