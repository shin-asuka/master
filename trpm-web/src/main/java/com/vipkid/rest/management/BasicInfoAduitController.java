package com.vipkid.rest.management;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Maps;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.service.management.BasicInfoAduitService;

@RestController
@RequestMapping("/management")
public class BasicInfoAduitController {

    private static Logger logger = LoggerFactory.getLogger(BasicInfoAduitController.class);
    
    @Autowired
    private BasicInfoAduitService basicInfoAduitService;
    
    
    @RequestMapping(value = "/basicReview", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> basicReview(HttpServletRequest request, HttpServletResponse response,long teacherApplicationId){
        try{
            return this.basicInfoAduitService.basicReview(teacherApplicationId);
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return Maps.newHashMap();
    }
    
    
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> changeStatus(HttpServletRequest request, HttpServletResponse response,
            long teacherApplicationId,long userId,String remark){
        Map<String,Object> result = Maps.newHashMap();
        try{
            result = this.basicInfoAduitService.changeStatus(teacherApplicationId, userId, remark);
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.put("status", false);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.put("status", false);
        }
        return result;
    }
}
