package com.vipkid.portal.classroom.controller;

import com.vipkid.enums.TeacherEnum;
import com.vipkid.portal.classroom.model.mockclass.PeViewOutputDto;
import com.vipkid.portal.classroom.service.MockClassService;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RestInterface(lifeCycle = TeacherEnum.LifeCycle.REGULAR)
@RequestMapping("/portal/mockclass/")
public class MockClassController extends RestfulController {

    private static Logger logger = LoggerFactory.getLogger(MockClassController.class);

    @Autowired
    private MockClassService mockClassService;

    @RequestMapping(value = "/pe/view", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> peView(HttpServletRequest request, HttpServletResponse response,
                    @RequestParam("applicationId") Integer applicationId) {
        try {
            logger.info("Invocation peView() applicationId: {}", applicationId);

            if (null == applicationId || 0 == applicationId) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ApiResponseUtils.buildErrorResp(HttpStatus.BAD_REQUEST.value(), "Argument 'applicationId' is illegal");
            }

            PeViewOutputDto peViewOutputDto = mockClassService.doPeView(applicationId);
            return ApiResponseUtils.buildSuccessDataResp(peViewOutputDto);
        } catch (Exception e) {
            logger.error("Internal server error", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            ExceptionUtils.getFullStackTrace(e));
        }
    }

}
