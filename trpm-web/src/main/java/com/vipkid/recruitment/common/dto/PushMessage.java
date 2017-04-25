package com.vipkid.recruitment.common.dto;

import java.io.Serializable;
import java.util.List;

/***
 * 保存消息请求实体
 */
public class PushMessage implements Serializable {

    private static final long serialVersionUID = -6557809226086887863L;

    private String title;           //消息标题
    private String message;         //消息实体
    private String operator;       //推送："push", 消息："message"
    private String jumpScheme;      //推送跳转落地页面
    /***
     *
     BOOKED_ONLINE_CLASS("booked_online_class", "预约课"), FEEDBACK_SUBMIT("feedback_submit","feedback提交"), FINISHTYPE_CONFIRM("finishtype_confirm","finishType确认"),
     OPEN_MORE_SLOT("open_more_slot", "老师多开timesLot"), SLOT_NOT_ENOUGH("slot_not_enough", "timesLot过少"), UA_SUBMIT("ua_submit", "ua提交"),
     START_CLASS_REMIND("start_class_remind", "开课提醒");
     */
    private String source;//消息来源
    private Long userId; //接收者的 Id


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getJumpScheme() {
        return jumpScheme;
    }

    public void setJumpScheme(String jumpScheme) {
        this.jumpScheme = jumpScheme;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
