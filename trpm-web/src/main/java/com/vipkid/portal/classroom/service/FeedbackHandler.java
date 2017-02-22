package com.vipkid.portal.classroom.service;

import com.vipkid.portal.classroom.model.PrevipCommentsVo;
import com.vipkid.portal.classroom.model.bo.FeedbackBo;
import com.vipkid.portal.classroom.model.bo.MajorCommentsBo;
import com.vipkid.portal.classroom.model.bo.PrevipCommentsBo;
import com.vipkid.portal.classroom.util.Convertor;
import org.springframework.stereotype.Service;

/**
 * Created by LP-813 on 2017/2/21.
 */
//@Service
//public class FeedbackHandler {
//    private FeedbackBo feedbackBo;
//    private FeedbackService feedbackService;
//    public <T> void init(T feedbackVo){
//        if(feedbackVo instanceof PrevipCommentsVo){
//            feedbackBo = Convertor.toPrevipCommentsBo(feedbackVo);
//        }else {
//            feedbackBo = Convertor.toMajorCommentsBo(feedbackVo);
//        }
//        feedbackService = FeedbackFactory.createFeedbackService(feedbackBo);
//    }
//
//    public FeedbackBo getFeedbackBo() {
//        return feedbackBo;
//    }
//
//    public FeedbackService getFeedbackService() {
//        return feedbackService;
//    }
//
//}
