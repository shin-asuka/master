package com.vipkid.trpm.entity.classroom;

public class UpdateStarDto {

    private int code;

    private String msg;

    private StarData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public StarData getData() {
        return data;
    }

    public void setData(StarData data) {
        this.data = data;
    }

    public static class StarData {

        private boolean result;

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

    }

}
