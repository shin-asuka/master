package com.vipkid.enums;

/**
 * Created by luning on 2017/3/11.
 */
public class BackgroundCheckEnum {
    public enum DisputeStatus {

        ACTIVE("Active"), // 正在进行dispute

        DEACTIVATED("Deactivated");// dispute已经结束

        private String value;

        DisputeStatus(String value) {
            this.value = value;
        }

        public String getVal() {
            return this.value;
        }
    }

    public enum BackgroundResult {
        NA("N/A"), // 背调暂时没有结果

        CLEAR("Clear"), // 背调结果为 clear

        ALERT("Alert"),//背调结果为Alert即第一次背调失败

        FAIL("Fail"); //背调结果为Fail

        private String value;

        BackgroundResult(String value) {
            this.value = value;
        }

        public String getVal() {
            return this.value;
        }
    }
    public  enum BackgroundPhase{
        START("Start"),//Sterling还未开始背调
        PENDING("Pending"),//Sterling开始背调等待背调的结果
        PREADVERSE("Preadverse"),//第一次背调失败，等待老师dispute
        DISPUTE("Dispute"),//正在进行dispute
        CLEAR("Clear"),//背调结果为Clear
        FAIL("Fail");//背调的最终结果为失败，超过5天没有进行dispute也是为失败
        private String value;
        BackgroundPhase(String value) {
            this.value = value;
        }

        public String getVal() {
            return this.value;
        }
    }
}
