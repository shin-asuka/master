package com.vipkid.trpm.entity.personal;


import java.util.List;

/**
 * Created by rentingji on 2017/4/25.
 */
public class APIQueryContractListByTeacherIdMapResult {
    List<APIQueryContractListByTeacherIdResult> unSignList;
    List<APIQueryContractListByTeacherIdResult> ineffectiveList;

    public List<APIQueryContractListByTeacherIdResult> getUnSignList() {
        return unSignList;
    }

    public void setUnSignList(List<APIQueryContractListByTeacherIdResult> unSignList) {
        this.unSignList = unSignList;
    }

    public List<APIQueryContractListByTeacherIdResult> getIneffectiveList() {
        return ineffectiveList;
    }

    public void setIneffectiveList(List<APIQueryContractListByTeacherIdResult> ineffectiveList) {
        this.ineffectiveList = ineffectiveList;
    }
}
