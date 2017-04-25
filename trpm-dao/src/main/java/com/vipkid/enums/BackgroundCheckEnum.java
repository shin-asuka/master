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

        CLEAR("CLEAR"), // 背调结果为 clear

        ALERT("ALERT"),//背调结果为Alert即第一次背调失败

        FAIL("FAIL"); //背调结果为Fail

        private String value;

        BackgroundResult(String value) {
            this.value = value;
        }

        public String getVal() {
            return this.value;
        }
    }
    public  enum BackgroundPhase{
        START("START"),//Sterling还未开始背调
        PENDING("PENDING"),//Sterling开始背调等待背调的结果
        PREADVERSE("PREADVERSE"),//第一次背调失败，等待老师dispute
        DISPUTE("DISPUTE"),//正在进行dispute
        DIDNOTDISPUTE("DIDNOTDISPUTE"),//超过5天没有进行dispute
        CLEAR("CLEAR"),//背调结果为Clear
        FAIL("FAIL");//背调的最终结果为失败，超过5天没有进行dispute也是为失败
        private String value;
        BackgroundPhase(String value) {
            this.value = value;
        }

        public String getVal() {
            return this.value;
        }
    }
    public enum FileStatus{
        SAVE("SAVE"),
        SUBMIT("SUBMIT");
        private String value;
        FileStatus(String value){this.value = value;}

        public String getValue(){ return this.value;}
    }
    public enum FileResult{
        PASS("PASS"),
        FAIL("FAIL"),
        PENDING("PENDING");
        private String value;
        FileResult(String value){this.value = value;}

        public String getValue(){ return this.value;}
    }
    public enum AdverseStatus{
        INITIATED("initiated"),
        AWAITINGDISPUTE("awaiting-dispute"),
        COMPLETE("complete"),
        CANCELLED("cancelled");

        private String value;

        AdverseStatus(String value){this.value = value;}

        public String getValue(){return this.value;}
    }
    public enum ReportType{
        SSN("SSN Trace"),
        CRIMINAL("Multi-State Instant Criminal Check"),
        CRIMINAL_BY_COUNTY("Criminal Check by County"),
        DOJ("DOJ Sex Offender");

        private String value;

        ReportType(String value){this.value = value;}

        public String getValue(){return this.value;}
    }
    public enum ReportResult{
        PENDING("pending"),
        SUCCESS("success"),
        CLEAR("clear"),
        COMPLETE("complete"),
        NO_DATA("no data"),
        ERROR("error"),
        NA("n/a"),
        ALERT("alert");

        private String value;

        ReportResult(String value){this.value = value;}

        public String getValue(){return this.value;}

    }

}
