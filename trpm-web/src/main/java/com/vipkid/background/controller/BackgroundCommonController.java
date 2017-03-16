package com.vipkid.background.controller;

import com.alibaba.druid.util.StringUtils;
import com.google.api.client.util.Maps;
import com.vipkid.background.service.BackgroundCommonService;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.trpm.entity.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RestInterface
@RequestMapping("/background")
public class BackgroundCommonController extends RestfulController{

    @Autowired
    private BackgroundCommonService backgroundCommonService;
    
    private static Logger logger = LoggerFactory.getLogger(BackgroundCommonController.class);
    @RequestMapping(value = "/queryBackgroundStatus", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getBackgoundStatus(HttpServletRequest request, HttpServletResponse response){
        try{
            Teacher teacher = getTeacher(request);
            String nationality = teacher.getCountry();
            Map<String ,Object> result = Maps.newHashMap();
            if (StringUtils.equalsIgnoreCase(nationality,"USA")){
                 result = backgroundCommonService.getUsaBackgroundStatus(teacher);
                 result.put("nationality","USA");
            }else if (StringUtils.equalsIgnoreCase(nationality,"CANADA")){
                result = backgroundCommonService.getCanadabackgroundStatus(teacher);
                result.put("nationality","CANADA");
            }else{
                result.put("nationality","others");
            }
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }

    }
    @RequestMapping(value = "/queryBackgroundFileStatus", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getBackgoundFileStatus(HttpServletRequest request, HttpServletResponse response){
        try{
            Teacher teacher = getTeacher(request);
            long teacherId = teacher.getId();
            String nationality = teacher.getCountry();
            Map<String,Object> result = backgroundCommonService.getBackgroundFileStatus(teacherId,nationality);
            return ReturnMapUtils.returnSuccess(result);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }

    }

}
