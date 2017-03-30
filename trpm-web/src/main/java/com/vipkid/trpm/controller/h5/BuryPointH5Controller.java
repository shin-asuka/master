package com.vipkid.trpm.controller.h5;

import com.vipkid.rest.utils.BuryPointUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by LP-813 on 2017/3/30.
 */
@RestController
@RequestMapping("/h5/burypoint")
public class BuryPointH5Controller {
    public static Boolean shareParentFeedBack(Integer teacherId,Long onlineClassId,Long feedbackId){
        BuryPointUtils.shareParentFeedBack(teacherId,onlineClassId,feedbackId,"H5");
        return true;
    }
}
