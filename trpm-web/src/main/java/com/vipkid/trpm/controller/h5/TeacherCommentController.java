package com.vipkid.trpm.controller.h5;

import com.vipkid.http.service.TeacherAppService;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.security.AppContext;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.entity.teachercomment.*;
import com.vipkid.trpm.service.portal.ReportService;
import com.vipkid.trpm.service.portal.TeacherService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 实现描述:
 * h5页面接口,
 * 对外提供的api路径实际上是
 * /api/h5/teacher/comment
 * /h5/teacher/comment
 * 两种
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2016/11/29 下午6:18
 */
@Controller
@RequestMapping("/h5/teacher/comment")
public class TeacherCommentController {

    private Logger logger = LoggerFactory.getLogger(TeacherCommentController.class);

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherAppService teacherAppService;

    @Autowired
    private ReportService reportService;

    @ResponseBody
    @RequestMapping(value = "/submit",produces = RestfulConfig.JSON_UTF_8)
    public Object updateStatus(@RequestBody SubmitTeacherCommentDto submitDto) {

        if (submitDto==null) {
            return ApiResponseUtils.buildErrorResp(-1, "参数不能为空!");
        }
        logger.info("updateStatus input={}",JsonUtils.toJSONString(submitDto));

        if (StringUtils.isBlank(submitDto.getTeacherFeedback())) {
            return ApiResponseUtils.buildErrorResp(-1, "参数格式错误!");
        }

        //TeacherComment tcuDto = new TeacherComment(inputDto);
        submitDto.setSubmitSource("APP_H5");
        //boolean result = teacherService.updateTeacherComment(tcuDto);
        Map<String, Object> parmMap = reportService.submitTeacherComment(submitDto, AppContext.getUser(),
            submitDto.getClassNumber(),null,true);


        if(parmMap!=null && parmMap.get("result")!=null&&((Boolean) parmMap.get("result"))){
            return ApiResponseUtils.buildSuccessDataResp("提交成功");
        }else{
            return ApiResponseUtils.buildErrorResp(-1,"提交失败");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/query")
    public Object queryTeacherComment(String teacherId, String studentId, String onlineclassId) {
        logger.info("queryTeacherComment teacherId={},studentId={},onlineclassId={}",teacherId,studentId,onlineclassId);
        if (!NumberUtils.isNumber(teacherId) || !NumberUtils.isNumber(studentId) || !NumberUtils
                .isNumber(onlineclassId)) {
            return ApiResponseUtils.buildErrorResp(-1, "参数错误!");
        }

        QueryTeacherCommentOutputDto result = teacherService.getTeacherComment(teacherId, studentId, onlineclassId);
        if (result != null) {
            return ApiResponseUtils.buildSuccessDataResp(result);
        } else {
            return ApiResponseUtils.buildErrorResp(-1, "获取tc信息失败");
        }
    }


    @ResponseBody
    @RequestMapping(value = "/levelAndUnits", method = RequestMethod.GET)
    public Object levelAndUnits() {

        List<StudentAbilityLevelRule> result = teacherAppService.findlevelAndUnits();
        if (result != null) {
            return ApiResponseUtils.buildSuccessDataResp(result);
        } else {
            return ApiResponseUtils.buildErrorResp(-1, "获取tc信息失败");
        }
    }
}
