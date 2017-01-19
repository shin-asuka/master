package com.vipkid.rest.portal.vo;

import java.util.List;

/**
 * Created by LP-813 on 2017/1/12.
 */
public class StudentCommentPageVo {

    private Integer total;
    private List<StudentCommentVo> data;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<StudentCommentVo> getData() {
        return data;
    }

    public void setData(List<StudentCommentVo> data) {
        this.data = data;
    }
}
