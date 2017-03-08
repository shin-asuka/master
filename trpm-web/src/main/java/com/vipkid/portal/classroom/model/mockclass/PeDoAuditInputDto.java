package com.vipkid.portal.classroom.model.mockclass;

import java.util.List;

public class PeDoAuditInputDto extends PeDto {

    private List<Integer> optionList;

    private String stateReason;

    private String finishType;

    public String getStateReason() {
        return stateReason;
    }

    public void setStateReason(String stateReason) {
        this.stateReason = stateReason;
    }

    public String getFinishType() {
        return finishType;
    }

    public void setFinishType(String finishType) {
        this.finishType = finishType;
    }

    public List<Integer> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<Integer> optionList) {
        this.optionList = optionList;
    }

}
