package com.vipkid.enums;

public class TeacherApplicationEnum {
    
	public enum Status {
		SIGNUP, // 新申请
		BASIC_INFO,	// 2015-08-08 添加basic-info 状态，从signup分离
		INTERVIEW, //面试
		SIGN_CONTRACT, //签合同
		TRAINING, // 教师培训
		PRACTICUM,//试讲
		PUBLICITY_INFO, //新增
		CONTRACT, //新增
		CANCELED, //已取消
		FINISHED // 已结束
	}
	
	public enum Result {
        PASS, // 通过
        FAIL, // 失败
        REAPPLY, //重新申请,继续上PRACTICUM1（由于客观原因没能完成面试）
        PRACTICUM2, //第一次面试没通过，上PRACTICUM2
        TBD_FAIL,
        TBD,
        CANCEL
	}
	
    public enum AuditStatus {
        ToAudit, // 待审核
        ToSubmit, // 待提交
        goToClass, // 待上课
        hasTimeOut //过期
        
    }
}
