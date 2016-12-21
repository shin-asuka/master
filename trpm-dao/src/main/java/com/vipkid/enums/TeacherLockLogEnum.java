package com.vipkid.enums;

public class TeacherLockLogEnum {
    
	public enum Reason {
		RESCHEDULE, // RESCHEDULE次数过多
		// 邮件：
		SIGNUP_NO_FINISH_REGISTER,
		INTERVIEW_NO_BOOK,
		INTERVIEW_NO_RESCHEDULE,
		INTERVIEW_PASS_CONTINUE,
		PRACTICUM2_NO_BOOK,
		PRACTICUM_NO_BOOK,
		PRACTICUM_NO_RESCHEDULE,
		TRAINING_QUIZ_NO_QUIZ,
		CONTRACT_INFO_FAIL_UPLOAD,
		CONTRACT_INFO_UPLOAD
	}
}
