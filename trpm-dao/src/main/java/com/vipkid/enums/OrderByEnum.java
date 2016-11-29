package com.vipkid.enums;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2016/11/28 下午5:25
 */
public enum OrderByEnum {

    id(" a.id desc ");

    private String val;

    private OrderByEnum(String val) {
        this.val = val;
    }

    public String val() {
        return this.val;
    }
}
