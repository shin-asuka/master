package com.vipkid.enums;

public class TeacherAddressEnum {
    
 	public enum AddressType {
		NORMAL(0),
	    LATEST(1);

	private Integer val;

	private AddressType(Integer val) {
		this.val = val;
	}
	public Integer val() {
		return val;
	}
	}
}
