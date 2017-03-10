package com.vipkid.portal.classroom.controller;

import com.vipkid.enums.TeacherEnum;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.portal.classroom.model.mockclass.CandidateFeedbackInputDto;
import com.vipkid.portal.classroom.model.mockclass.PeDoAuditInputDto;
import com.vipkid.portal.classroom.model.mockclass.PeReviewOutputDto;
import com.vipkid.portal.classroom.service.MockClassService;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.vipkid.enums.MockClassEnum.SUBMIT;
import static com.vipkid.enums.TbdResultEnum.ReScheduleEnum.ReSchedule;

@RestController
@RestInterface(lifeCycle = TeacherEnum.LifeCycle.REGULAR)
@RequestMapping("/portal/mockclass")
public class MockClassController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(MockClassController.class);

    @Autowired
    private MockClassService mockClassService;

    @RequestMapping(value = "/pe/review", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> peReview(HttpServletRequest request, HttpServletResponse response,
                    @RequestParam("applicationId") Integer applicationId) {
        try {
            logger.info("Invocation peReview() applicationId: {}", applicationId);

            if (null == applicationId || 0 == applicationId) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(),
                                "Argument 'applicationId' is illegal");
            }

            PeReviewOutputDto peReviewOutputDto = mockClassService.doPeReview(applicationId);
            return ApiResponseUtils.buildSuccessDataResp(peReviewOutputDto);
        } catch (Exception e) {
            logger.error("Internal server error", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            ExceptionUtils.getFullStackTrace(e));
        }
    }

    @RequestMapping(value = "/pe/doAudit", method = RequestMethod.PUT, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> peDoAudit(HttpServletRequest request, HttpServletResponse response,
                    @RequestBody PeDoAuditInputDto peDoAuditInputDto) {
        try {
            logger.info("Invocation peDoAudit() request body: {}", JsonUtils.toJSONString(peDoAuditInputDto));

            if (null == peDoAuditInputDto.getApplicationId() || 0 == peDoAuditInputDto.getApplicationId()) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(),
                                "Argument 'applicationId' is illegal");
            }

            if (StringUtils.isBlank(peDoAuditInputDto.getStatus())) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "Argument 'status' is required");
            }

            if (StringUtils.equals(peDoAuditInputDto.getStatus(), ReSchedule.val())
                            && StringUtils.isBlank(peDoAuditInputDto.getFinishType())) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(),
                                "Argument 'finishType' is required");
            }

            if (StringUtils.equals(peDoAuditInputDto.getStatus(), SUBMIT.name())) {
                if (StringUtils.isBlank(peDoAuditInputDto.getThingsDidWell())
                                || StringUtils.isBlank(peDoAuditInputDto.getAreasImprovement())
                                || CollectionUtils.isEmpty(peDoAuditInputDto.getOptionList())
                                || CollectionUtils.isEmpty(peDoAuditInputDto.getTagsList())
                                || CollectionUtils.isEmpty(peDoAuditInputDto.getLevelsList())) {
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "Form is not finished");
                }
            }

            String msg = mockClassService.peDoAudit(peDoAuditInputDto);
            logger.info("Invocation peDoAudit() response body: {}", msg);

            if (HttpStatus.OK.getReasonPhrase().equals(msg)) {
                return ApiResponseUtils.buildSuccessDataResp(HttpStatus.OK);
            } else {
                return ApiResponseUtils.buildSuccessDataResp(msg);
            }
        } catch (Exception e) {
            logger.error("Internal server error", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            ExceptionUtils.getFullStackTrace(e));
        }
    }

    @RequestMapping(value = "/candidate/feedback", method = RequestMethod.PUT, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> candidateFeedback(HttpServletRequest request, HttpServletResponse response,
                    @RequestBody CandidateFeedbackInputDto candidateFeedbackInputDto) {
        try {
            logger.info("Invocation candidateFeedback() request body: {}",
                            JsonUtils.toJSONString(candidateFeedbackInputDto));

            if (null == candidateFeedbackInputDto.getApplicationId()
                            || 0 == candidateFeedbackInputDto.getApplicationId()) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(),
                                "Argument 'applicationId' is illegal");
            }

            String msg = mockClassService.doCandidateFeedback(candidateFeedbackInputDto);
            logger.info("Invocation candidateFeedback() response body: {}", msg);

            if (HttpStatus.OK.getReasonPhrase().equals(msg)) {
                return ApiResponseUtils.buildSuccessDataResp(HttpStatus.OK);
            } else {
                return ApiResponseUtils.buildSuccessDataResp(msg);
            }
        } catch (Exception e) {
            logger.error("Internal server error", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            ExceptionUtils.getFullStackTrace(e));
        }
    }

}
