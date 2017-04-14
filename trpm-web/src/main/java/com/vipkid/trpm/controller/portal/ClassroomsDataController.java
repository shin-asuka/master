package com.vipkid.trpm.controller.portal;

import com.google.api.client.util.Maps;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.http.constant.HttpUrlConstant;
import com.vipkid.http.service.HttpApiClient;
import com.vipkid.http.utils.JacksonUtils;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.service.portal.OnlineClassService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.vipkid.rest.RestfulController.TEACHER;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2017/4/13 下午3:51
 */
@RestController
@RestInterface(lifeCycle = { TeacherEnum.LifeCycle.REGULAR})
@RequestMapping("/classroom")
public class ClassroomsDataController {

    private static final Logger logger = LoggerFactory.getLogger(ClassroomsDataController.class);

    ///api/invoker/service/class/{class}/classroom/{classroom}/supplier/{supplierCode}
    //https://code.vipkid.com.cn/alchemy/invoker/blob/master/invoker-interface.md
    private static final String API_SWITCH_CLASSROOM_URL = "/api/invoker/service/class/%s/classroom/%s/supplier/%s";

    private static final String API_URL_SEP = "?";

    private static final String API_SWITCH_CLASSROOM_PARAM = "operatorId=%s&operatorType=Teacher";

    @Resource
    private OnlineClassService onlineClassService;

    @Resource
    private HttpApiClient httpApiClient;

    @Resource
    private HttpUrlConstant httpUrlConstant;


    /**
     * supplierCode
     * XUEDIANYUN="1";DUOBEIYUN = "2";SHENGWANG = "3";DUOBEIYUN2 ="4";VIPKID= "5";VIPKID2 = "6"
     *
     * @param supplierCode
     * @return
     */
    @RequestMapping(value = "/switchLine",method = RequestMethod.GET)
    public Map<String, Object> traceLogFromFE(
            @RequestParam String supplierCode,
            @RequestParam String onlineClassId,
            HttpServletRequest request){

        Teacher teacher = (Teacher) request.getAttribute(TEACHER);
        if (teacher == null || teacher.getId() == 0) {
            logger.warn("can not find teacher");
            return ApiResponseUtils.buildErrorResp(-1, "wrong teacher");
        }

        OnlineClass onlineClass = onlineClassService.getOnlineClassById(Long.valueOf(onlineClassId));

        if (onlineClass == null
                || StringUtils.isBlank(onlineClass.getClassroom())
                || onlineClass.getTeacherId() == 0) {
            logger.warn("onlineClassId:{},不存在", onlineClassId);
            return ApiResponseUtils.buildErrorResp(-1, "wrong onlineClass");
        } else if (teacher.getId() != onlineClass.getTeacherId()) {
            logger.warn("没有权限操作,onlineClassId:{}, teacherId:{}, 与当前登录teacherId:{}不匹配,onlineClassId:{}",
                    onlineClassId,
                    onlineClass.getTeacherId(),
                    teacher.getId());
            return ApiResponseUtils.buildErrorResp(-1, "wrong teacher");
        }
        String requestUrl = httpUrlConstant.getApiClassroomServerUrl()
                + String.format(API_SWITCH_CLASSROOM_URL, onlineClassId, onlineClass.getClassroom(), supplierCode)
                + API_URL_SEP
                + String.format(API_SWITCH_CLASSROOM_PARAM, onlineClass.getTeacherId());

        String result = httpApiClient.doPut(requestUrl,null);

        if(StringUtils.isBlank(result)){
            return ApiResponseUtils.buildErrorResp(-1,"fail");
        }else{
            return ApiResponseUtils.buildSuccessDataResp("success");
        }

    }
}
