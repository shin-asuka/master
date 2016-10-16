/**
 * 
 */
package com.vipkid.trpm.entity.personal;

import java.io.Serializable;

import com.vipkid.trpm.entity.TeacherTaxpayerForm;

/**
 * @author zouqinghua
 * @date 2016年10月14日  下午5:50:12
 *
 */
public class TaxpayerView implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private TeacherTaxpayerForm formW9;
	private TeacherTaxpayerForm formT4A;
	public TeacherTaxpayerForm getFormW9() {
		return formW9;
	}
	public void setFormW9(TeacherTaxpayerForm formW9) {
		this.formW9 = formW9;
	}
	public TeacherTaxpayerForm getFormT4A() {
		return formT4A;
	}
	public void setFormT4A(TeacherTaxpayerForm formT4A) {
		this.formT4A = formT4A;
	}
	
	
}
