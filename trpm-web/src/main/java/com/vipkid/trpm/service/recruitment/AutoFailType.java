package com.vipkid.trpm.service.recruitment;

public enum AutoFailType {
            WORK_HOUR_LIMIT(1, "not enough work experience"),
            NATIONALITY_LIMIT(2, "nationality limitation"),
            LOCATION_LIMIT(3, "bad locations that we don't support well"),
            DEGREE_LIMIT(4, "not good enough education background");

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