package com.vipkid.rest;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.teacher.tools.utils.validation.ValidateUtils;
import com.vipkid.teacher.tools.utils.validation.tools.Result;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;

public class RestfulController {
	
	private static Logger logger = LoggerFactory.getLogger(RestfulController.class);
    
    public static final String AUTOKEN = "Authorization";
    
    public static final String TEACHER = "Teacher";

    protected User getUser(HttpServletRequest request) throws IllegalArgumentException {
        Preconditions.checkArgument(request.getAttribute(AUTOKEN) != null);
        User user = (User) request.getAttribute(AUTOKEN);
        return user;
    }
    
    protected Teacher getTeacher(HttpServletRequest request) throws IllegalArgumentException {
        Preconditions.checkArgument(request.getAttribute(TEACHER) != null);
        Teacher teacher = (Teacher) request.getAttribute(TEACHER);
        return teacher;
    }
    
    protected Map<String, Object> checkParmar(Object bean,HttpServletResponse response){
    	List<Result> list = ValidateUtils.checkBean(bean, false);
        if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            logger.info(list.get(0).getName() + "," + list.get(0).getMessages());
            return ApiResponseUtils.buildErrorResp(-1, "Parameter validation results:"+list.get(0).getName() + "," + list.get(0).getMessages());
        }
        return Maps.newHashMap();
    }
}
