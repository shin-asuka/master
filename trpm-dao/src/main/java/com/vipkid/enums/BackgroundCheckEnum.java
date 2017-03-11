package com.vipkid.enums;

/**
 * Created by luning on 2017/3/11.
 */
public class BackgroundCheckEnum {
    public enum DisputeStatus {

        ACTIVE("Active"), // 正在进行despute

        DEACTIVE("Deactivated");// despute已经结束

        private String value;

        DisputeStatus(String value) {
            this.value = value;
        }

        public String getVal() {
            return this.value;
        }
    }

    public enum Result {
        NA("N/A"), // 背调暂时没有结果

        CLEAR("Clear"), // 背调结果为 clear

        ALERT("Alert");//背调结果为 Alert

        private String value;

        Result(String value) {
            this.value = value;
        }

        public String getVal() {
            return this.value;
        }
    }
}
