package com.vipkid.enums;

public class TeacherLockLogEnum {
    
	public enum Reason {
		RESCHEDULE, // RESCHEDULE次数过多
		// 邮件：
		NO_BOOK,
		NO_RESCHEDULE,
		NO_FINISH_REGISTER,
		NO_UPLOAD
	}
}
