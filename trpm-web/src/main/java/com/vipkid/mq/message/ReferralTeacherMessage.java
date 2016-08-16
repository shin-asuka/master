/**
 * 
 */
package com.vipkid.mq.message;

import java.io.Serializable;

import com.vipkid.trpm.entity.Teacher;


/**
 * @author zouqinghua
 * @date 2016年5月10日 下午2:46:21
 *
 */
public class ReferralTeacherMessage implements Serializable {

	private static final long serialVersionUID = 7422401967408523712L;
	/*  */
	private int referralTeacherId;
	/*  */
	private String realName;
	/*  */
	private String serialNumber;

	private String contractType;// 合同类型
	/*  */
	private long signingDate;
	/*  */
	private long registrationDate;

	private int month;

	private int appliedTeacherId;

	public ReferralTeacherMessage() {
	}

	public ReferralTeacherMessage(int id, String realName, String serialNumber, String contractType, int signingDate,
			int registrationDate) {
		super();
		this.referralTeacherId = id;
		this.realName = realName;
		this.serialNumber = serialNumber;
		this.contractType = contractType;
		this.signingDate = signingDate;
		this.registrationDate = registrationDate;
	}

	public String getSerialNumber() {
		return this.serialNumber;
	}

	public ReferralTeacherMessage setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
		return this;
	}

	public String getRealName() {
		return this.realName;
	}

	public ReferralTeacherMessage setRealName(String realName) {
		this.realName = realName;
		return this;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public long getSigningDate() {
		return signingDate;
	}

	public void setSigningDate(long signingDate) {
		this.signingDate = signingDate;
	}

	public long getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(long registrationDate) {
		this.registrationDate = registrationDate;
	}

	public int getReferralTeacherId() {
		return referralTeacherId;
	}

	public void setReferralTeacherId(int referralTeacherId) {
		this.referralTeacherId = referralTeacherId;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getAppliedTeacherId() {
		return appliedTeacherId;
	}

	public void setAppliedTeacherId(int appliedTeacherId) {
		this.appliedTeacherId = appliedTeacherId;
	}

	public void setReferralTeacher(Long appliedTeacherId, Teacher refereeTeacher){
		if(refereeTeacher!=null){
			this.setReferralTeacherId(new Long(refereeTeacher.getId()).intValue());
			this.setRealName(refereeTeacher.getRealName());
			//this.setContractType(refereeTeacher.getContractType());
			this.setSerialNumber(refereeTeacher.getSerialNumber());
			this.setAppliedTeacherId(new Long(appliedTeacherId).intValue());
			
		}
		this.setReferralTeacherId(referralTeacherId);
	}
}
