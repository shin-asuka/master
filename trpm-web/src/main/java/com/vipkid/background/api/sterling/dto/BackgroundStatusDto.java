package com.vipkid.background.api.sterling.dto;

import java.io.Serializable;

/**
 * Created by luning on 2017/3/14.
 */
public class BackgroundStatusDto implements Serializable {

    private static final long serialVersionUID = -3805545654536384132L;
    boolean needBackgroundCheck;
    String phase;
    String result;
    String nationality;

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public boolean isNeedBackgroundCheck() {
        return needBackgroundCheck;
    }

    public void setNeedBackgroundCheck(boolean needBackgroundCheck) {
        this.needBackgroundCheck = needBackgroundCheck;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
