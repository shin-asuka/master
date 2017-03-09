package com.vipkid.portal.classroom.model.mockclass;

import java.util.List;

public class PeDoAuditInputDto extends PeDto {

    private List<Integer> optionList;

    private String finishType;

    // TeacherPe Id
    private Integer peId;

    public Integer getPeId() {
        return peId;
    }

    public void setPeId(Integer peId) {
        this.peId = peId;
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
