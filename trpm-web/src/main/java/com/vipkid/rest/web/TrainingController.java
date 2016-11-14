package com.vipkid.rest.web;

import com.google.api.client.util.Maps;
import com.google.common.base.Preconditions;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.entity.TeacherApplication;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.rest.AdminQuizService;
import com.vipkid.trpm.service.rest.LoginService;
import com.vipkid.trpm.service.rest.TeacherPageLoginService;
import com.vipkid.trpm.service.rest.TrainingService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by zhangzhaojun on 2016/11/12.
 */
@RestController
@RequestMapping("/")
public class TrainingController {
    private static Logger logger = LoggerFactory.getLogger(AdminQuizController.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private TrainingService trainingService;
    @Autowired
    private AdminQuizService adminQuizService;

    @Autowired
    private TeacherPageLoginService teacherPageLoginService;

   /* @RequestMapping(value = "/training", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String, Object> training(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("result", false);
        try {
            String token = request.getHeader(ApplicationConstant.CookieKey.AUTOKEN);
            Preconditions.checkArgument(StringUtils.isNotBlank(token));
            User user = loginService.getUser(request);
            if (user == null) {
                response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
                logger.warn("用户{}不存在，token过期", user.getId());
                return result;
            }
            TeacherApplication application = trainingService.findAppliction(user.getId());
            if (application != null) {
                if (TeacherApplicationEnum.Status.TRAINING.toString().equals(application.getStatus()) && application.getResult() == null) {
                    application.setResult(TeacherApplicationEnum.Result.AUDITING.toString());
                }
            }

            result.put("result", );
        }
    }*/
}
