package com.vipkid.trpm.controller.h5;

import com.vipkid.rest.security.AppContext;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.entity.Teacher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现描述:
 *
 * @author steven
 * @version v1.0.0
 * @see
 * @since 2017/4/6 下午9:18
 */
@RestController
@RequestMapping("/h5/user")
public class AuthFromAppController {

    /**
     * 拦截器里认证从app过来的用户信息,认证后返回得到的teacherId供前端使用
     *
     * @return
     */
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public Object shareParentFeedback(){
        Teacher teacher = AppContext.getTeacher();
        if(teacher == null){
            return ApiResponseUtils.buildErrorResp(-1, "can not find teacher");
        }else{
            return ApiResponseUtils.buildSuccessDataResp(teacher.getId());
        }
    }
}
