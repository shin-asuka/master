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
	
    /**招募端新增3种渠道分类*/
    public enum RecruitmentChannel {
        //2016-11 新增渠道分类
        TEACHER,
        PARTNER,
        OTHER // 其他
    }

	public enum LifeCycle {
	    ALL("All"),
		SIGNUP("Sign Up"), // 注册 + 收集基本信息
		BASIC_INFO("Basic Info"), // 2015-08-08 添加basic-info 状态，从signup分离
		INTERVIEW("Interview"), // 面试
		SIGN_CONTRACT("Sign Contract"), // 签合同
		TRAINING("Teaching Prep"), // 教师培训
		PRACTICUM("Mock Class"), // 试讲
		CONTRACT_INFO("Contract Info"), //合同 + 个人信息 (头像, 生活照)
		REGULAR("Regular"), // 成为正式老师
		QUIT("Quit"), // 离职
		FAIL("Fail"); // 被剔除的老师，永不录用的那种


		private String value;

		LifeCycle(String value) {
			this.value = value;
		}

		public String getVal() {
			return this.value;
		}
	}

	public enum Hide {
		ALL, SCHEDULE, TRIAL, NONE
	}

	public enum PageEnum {
		SIGNUP,INFOMATION,RESULT
	}

	public enum FormType{
		W9(1),
		T4A(2);
		
		private Integer val;   
		
        private FormType(Integer val) {
            this.val = val;
        }        
        public Integer val() {
            return val;
        }
        
	}
	
    /**学历*/
    public enum DegreeType{
        HIGH_SCHOOL,
        ASSOCIATES,
        BACHELORS,
        MASTERS,
        PHD,
        OTHER
    }
	
	public static FormType getFormTypeByCode(String code){
		FormType formType = null;
		try {
			formType = FormType.valueOf(code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formType;
	}
	
	public static FormType getFormTypeById(Integer id){
		FormType formType = null;
		try {
			FormType[] list = FormType.values();
			for (FormType e : list) {
				if(e.val().equals(id)){
					formType = e;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formType;
	}
	
	public enum UploadStatus{
		NOT_NEED_UPLOAD(2),
		UPLOADED(1),
		UN_UPLOAD(0);
		
		private Integer val;   
		
        private UploadStatus(Integer val) {
            this.val = val;
        }        
        public Integer val() {
            return val;
        }
	}
	
	public enum ISNew{
		NEW(1),
		OLD(0);
		
		private Integer val;   
		
        private ISNew(Integer val) {
            this.val = val;
        }        
        public Integer val() {
            return val;
        }
	}
	
	public static UploadStatus getUploadStatusById(Integer id){
		UploadStatus status = null;
		try {
			UploadStatus[] list = UploadStatus.values();
			for (UploadStatus e : list) {
				if(e.val().equals(id)){
					status = e;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	
	public static ISNew getNewById(Integer id){
		ISNew isNew = null;
		try {
			ISNew[] list = ISNew.values();
			for (ISNew e : list) {
				if(e.val().equals(id)){
					isNew = e;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isNew;
	}
}
