/**
 * 
 */
package com.vipkid.payroll.model;

import java.io.Serializable;
import java.util.Map;

import com.google.api.client.util.Maps;

/**
 * @author zouqinghua
 * @date 2016年3月16日 下午3:04:51
 *
 */
public class Result implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int SLALARY_TYPE_COURSE_ALL_RULE = 0;
    public static final int SLALARY_TYPE_COURSE_ADDITION_RULE = 1;// 基本工资
    public static final int SLALARY_TYPE_BONUS_ATTENDANCE_RULE = 2; // 出席奖金
    public static final int SLALARY_TYPE_COURSE_DEDUCTION_RULE = 3; // 缺席扣费
    public static final int SLALARY_TYPE_OTHER_BONUS_NEW_VIPKID_RULE = 4; // 新生入学奖金
    public static final int SLALARY_TYPE_OTHER_BONUS_MONTHLY_RULE = 5; // 每月奖金
    public static final int SLALARY_TYPE_OTHER_BONUS_FINISHED_CLASS_RULE = 6; // 完成课程奖金
    public static final int SLALARY_TYPE_OTHER_BONUS_TEACHER_REFERRAL_FEE_RULE = 7; // 推荐教师奖金
    public static final int SLALARY_TYPE_ADJUSTMENT_RULE = 8; // 调整费用
    public static final int SLALARY_TYPE_TRIAL_CLASS_RULE = 9; // 试听课工资

    public static final String ATTR_PAGE = "page";
    public static final String ATTR_LIST = "list";
    public static final String ATTR_BEAN = "bean";
    public static final String ATTR_PARAM = "param";
	public static final String ATTR_PAGE_DE = "dePage";
	public static final String ATTR_COURSE_TOTAL = "courseTotal";
	public static final String ATTR_DE_TOTAL = "deTotal";
	

    private Map<String, Object> attribute = Maps.newHashMap();

    public Result() {

    }
    public Result(Map<String, Object> attribute) {
        super();
        this.attribute = attribute;
    }

    public Map<String, Object> getAttribute() {
        if (this.attribute == null) {
            this.attribute = Maps.newHashMap();
        }
        return this.attribute;
    }

    public void addAttribute(String attr, Object value) {
        getAttribute().put(attr, value);
    }

    public void removeAttribute(String attr) {
        getAttribute().remove(attr);
    }

    public void setAttribute(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

}
