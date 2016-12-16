package com.vipkid.enums;

public class TeacherLockLogEnum {
    
	public enum Reason {
		RESCHEDULE, // RESCHEDULE次数过多
		// 邮件：
		INTERVIEW_NO_BOOK,
		INTERVIEW_NO_RESCHEDULE,
		NO_FINISH_REGISTER,
		CONTRACT_INFO_NO_UPLOAD,
		PRACTICUM2_NO_BOOK,
		PRACTICUM_NO_BOOK,
		PRACTICUM_NO_RESCHEDULE,
		TRAINING_QUIZ_NO_QUIZ,
		INTERVIEW_PASS_CONTINUE_REMINDER_JOB
	}
}
