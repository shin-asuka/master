package com.vipkid.portal.classroom.service;

import com.vipkid.portal.classroom.model.bo.PrevipCommentsBo;
import com.vipkid.rest.utils.SpringContextHolder;

/**
 * Created by LP-813 on 2017/2/21.
 */
public class FeedbackFactory {
    public static <T> FeedbackService createFeedbackService(T feedbackBo){
        FeedbackService feedbackService = null;
        if(feedbackBo instanceof PrevipCommentsBo){
            feedbackService = SpringContextHolder.getBean("PrevipFeedbackService");
        }else {
            feedbackService = SpringContextHolder.getBean("MajorFeedbackService");
        }
        return feedbackService;
    }
}
