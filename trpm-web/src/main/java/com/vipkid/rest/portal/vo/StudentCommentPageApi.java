package com.vipkid.rest.portal.vo;

import java.util.List;

/**
 * Created by LP-813 on 2017/1/12.
 */
public class StudentCommentPageApi {

    private Integer total;
    private List<StudentCommentApi> data;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<StudentCommentApi> getData() {
        return data;
    }

    public void setData(List<StudentCommentApi> data) {
        this.data = data;
    }
}
