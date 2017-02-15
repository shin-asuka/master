package com.vipkid.rest.portal.vo;

import java.util.List;

/**
 * Created by LP-813 on 2017/1/12.
 */
public class StudentCommentPageVo {

    private Integer total;
    private List<StudentCommentVo> data;
    private Integer curPageNo;
    private Integer totalPageNo;

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

    public Integer getTotalPageNo() {
        return totalPageNo;
    }

    public void setTotalPageNo(Integer totalPageNo) {
        this.totalPageNo = totalPageNo;
    }

    public Integer getCurPageNo() {
        return curPageNo;
    }

    public void setCurPageNo(Integer curPageNo) {
        this.curPageNo = curPageNo;
    }
}
