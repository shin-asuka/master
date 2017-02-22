package com.vipkid.portal.classroom.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.vipkid.portal.classroom.model.bo.FeedbackBo;
import com.vipkid.portal.classroom.model.bo.MajorCommentsBo;
import com.vipkid.portal.classroom.util.Convertor;
import com.vipkid.rest.security.AppContext;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.entity.teachercomment.TeacherComment;
import com.vipkid.trpm.entity.teachercomment.TeacherCommentResult;
import com.vipkid.trpm.entity.teachercomment.TeacherCommentUpdateDto;
import com.vipkid.trpm.util.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by LP-813 on 2017/2/21.
 */
public interface FeedbackService {
    public String checkInputArgument(FeedbackBo feedbackBo,String serialNumber);
    public void sendFeedbackMessage(FeedbackBo feedbackBo,String scheduledDateTime,String serialNumber);
    public Map<String, Object> submitTeacherComment(FeedbackBo teacherComment, User user, String serialNumber,
                                                    String scheduledDateTime, boolean isFromH5);
}
