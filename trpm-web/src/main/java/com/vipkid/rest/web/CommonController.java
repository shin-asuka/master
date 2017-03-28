package com.vipkid.rest.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.dto.PasswordDto;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.teacher.tools.security.SHA256PasswordEncoder;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.passport.PassportService;

/**
 * 
 * 用于TR 和 TP 登陆后的用户任意life_cycle状态下可访问的接口
 * 
 * 目前包括 登陆后 修改密码,退出等操作
 * 
 * @author zengweilong
 *
 */
@RestController
@RestInterface
@RequestMapping("/common")
public class CommonController extends RestfulController{

	private static Logger logger = LoggerFactory.getLogger(CommonController.class);
	
    @Autowired
    private PassportService passportService;
    
    
    @Autowired
    private LoginService loginService;
    
	/**
     * 密码修改
     * @param request
     * @param response
     * @param bean
     * @return    
     * Map<String,Object>
     */
    @RequestMapping(value = "/password", method = RequestMethod.PUT, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> updatePassword(HttpServletRequest request, HttpServletResponse response,@RequestBody PasswordDto bean){
        try{
            Teacher teacher = getTeacher(request);
            List<Result> list = ValidateUtils.checkBean(bean,false);
            if(CollectionUtils.isNotEmpty(list) && list.get(0).isResult()){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(list.get(0).getName() + "," + list.get(0).getMessages());
            }
            logger.info("user:{},updatePassword",teacher.getId());
            User user = getUser(request);
            SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
            //旧密码相等则进行修改逻辑
            if(!StringUtils.equals(user.getPassword(), encoder.encode(bean.getOldPassword()))){
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return ReturnMapUtils.returnFail(" old password is error! ");
            }
            Map<String,Object> result = this.passportService.updatePassword(teacher,bean.getNewPassword());
            if(ReturnMapUtils.isFail(result)){
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return result;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ReturnMapUtils.returnFail(e.getMessage(),e);
        }
    }
    
    @RequestMapping(value = "/logout", method = RequestMethod.PUT, produces = RestfulConfig.JSON_UTF_8)
	public  Map<String,Object> logout(HttpServletRequest request, HttpServletResponse response) {
    	try{
	    	loginService.removeLoginCooke(request, response);
	    	return ReturnMapUtils.returnSuccess();
	    } catch (IllegalArgumentException e) {
	        response.setStatus(HttpStatus.BAD_REQUEST.value());
	        return ReturnMapUtils.returnFail(e.getMessage(),e);
	    } catch (Exception e) {
	        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        return ReturnMapUtils.returnFail(e.getMessage(),e);
	    }
	}
}
