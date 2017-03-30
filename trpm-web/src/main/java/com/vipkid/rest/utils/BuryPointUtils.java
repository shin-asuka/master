package com.vipkid.rest.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

/**
 * Created by LP-813 on 2017/3/30.
 */
public class BuryPointUtils {
    private static final Logger logger = LoggerFactory.getLogger(BuryPointUtils.class);

    public static void shareParentFeedBack(Integer teacherId,Long onlineClassId,Long feedbackId,String source){
        logger.info("【埋点】shareParentFeedBack--teacherId:{}，onlineClassId:{}，feedbackId:{},source:{}",teacherId,onlineClassId,feedbackId,source);
    }
}
