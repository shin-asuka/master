package com.vipkid.enums;

public class TeacherApplicationEnum {
    
	public enum Status {
		SIGNUP, // 新申请
		BASIC_INFO,	// 2015-08-08 添加basic-info 状态，从signup分离
		INTERVIEW, //面试
		SIGN_CONTRACT, //签合同
		TRAINING, // 教师培训
		PRACTICUM,//试讲
		CONTRACT_INFO, //新增
		CANCELED, //已取消
		FINISHED // 已结束
	}
	
	public enum Result {
        PASS, // 通过
        FAIL, // 失败
        REAPPLY, //重新申请,继续上PRACTICUM1（由于客观原因没能完成面试）
        PRACTICUM2, //第一次面试没通过，上PRACTICUM2
        TBD_FAIL,
        CANCEL
	}
	
    public enum AuditStatus {
        TO_AUDIT, // 待审核
        TO_SUBMIT, // 待提交
        TO_CLASS, // 待上课
        HAS_TIMEOUT //过期
    }
//1-other_degrees  2-certificationFiles   3-Identification  4-Diploma 5-Contract  6-Passport   7-Driver's license
	public enum ContractFileType {
		OTHER_DEGREES(1),
	    CERTIFICATIONFILES(2),
	    IDENTIFICATION(3),
	    DIPLOMA(4),
	    CONTRACT(5),
	    PASSPORT(6),
	    DRIVER(7),
	    CONTRACT_W9(8);

	private Integer val;

	private ContractFileType(Integer val) {
		this.val = val;
	}
	public Integer val() {
		return val;
	}
	}
}
