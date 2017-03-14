package com.vipkid.background.enums;

/***
 * 系统错误码
 */
public enum TeacherPortalCodeEnum {

	// 成功返回码
	RES_SUCCESS("TP30000", "success"),


	/**结果失败 */
	SYS_FAIL("TP30001", "failure"),

	/**参数错误*/
	SYS_PARAM_ERROR("TP30002","illegal arguments"),

	//订单不存在
	ORDER_NOT_EXISTS("TP30003", "the item does not exists");

	private String code;
	private String msg;

	private TeacherPortalCodeEnum(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}


	public static TeacherPortalCodeEnum getEnumWithDefault(String code) {
		TeacherPortalCodeEnum[] values = TeacherPortalCodeEnum.values();
		for (TeacherPortalCodeEnum codeEnum : values) {
			if (codeEnum.getCode().equals(code)) {
				return codeEnum;
			}
		}
		return TeacherPortalCodeEnum.SYS_FAIL;
	}

}
