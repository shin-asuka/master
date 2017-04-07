package com.vipkid.background.api.sterling.dto;

/**
 * Created by liyang on 2017/3/11.
 * 此类用于请求Sterling Screening 接口的参数类;
 */
public class ScreeningInputDto implements  java.io.Serializable{

    private static final long serialVersionUID = 2120717438489441605L;
    private String packageId;
    private String candidateId;
    private CallBack callback;


    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public CallBack getCallback() {
        return callback;
    }

    public void setCallback(CallBack callback) {
        this.callback = callback;
    }



    public static class CallBack implements java.io.Serializable{
        private static final long serialVersionUID = 310310049902957967L;
        private String uri;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
