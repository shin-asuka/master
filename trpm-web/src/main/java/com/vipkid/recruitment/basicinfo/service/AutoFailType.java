package com.vipkid.recruitment.basicinfo.service;

public enum AutoFailType {
    WORK_HOUR_LIMIT(2, "not enough work experience"),
    NATIONALITY_LIMIT(4, "nationality limitation"),
    LOCATION_LIMIT(8, "bad locations that we don't support well"),
    DEGREE_LIMIT(16, "not good enough education background");

    private int code;
    private String tip;

    AutoFailType(int code, String tip) {
        this.code = code;
        this.tip = tip;
    }

    public int getCode() {
        return code;
    }

    public String getTip() {
        return tip;
    }
}