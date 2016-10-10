package com.vipkid.rest.app;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.UserEnum;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.TeacherLifeCycle;
import com.vipkid.trpm.entity.AppOnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherComment;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.entity.app.AppEnum;
import com.vipkid.trpm.entity.app.AppTeacher;
import com.vipkid.trpm.security.SHA256PasswordEncoder;
import com.vipkid.trpm.service.passport.PassportService;
import com.vipkid.trpm.service.portal.CommentsService;
import com.vipkid.trpm.service.rest.AppRestfulService;

@Controller
public class AppRestfulController {

	private static Logger logger = LoggerFactory.getLogger(AppRestfulController.class);

	@Autowired
	private PassportService passportService;

	@Autowired
	private AppRestfulService appRestfulService;

	@Autowired
	private CommentsService commentsService;

	@RequestMapping(value = "/app/login", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public @ResponseBody String login(HttpServletRequest request, HttpServletResponse response, @RequestParam String email,
	        @RequestParam String password) {
		Map<String, Object> result = Maps.newHashMap();
		
		if(StringUtils.isBlank(email) || StringUtils.isBlank(password)){
		    logger.error("Email OR password 不能为空！");
		    response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
		    return JsonTools.getJson(result);
		}
		
		try {
		    Preconditions.checkArgument(StringUtils.isNotBlank(email));
		    Preconditions.checkArgument(StringUtils.isNotBlank(password));
		    
			User user = passportService.findUserByUsername(email);
			// 根据email，检查是否有此账号。
			if (null == user) {
				logger.error(" User is Null 404 " + email);
				response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
				return JsonTools.getJson(result);
			}
			logger.info("password check start!");
			// 密码验证
			SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
			if (!(encoder.encode(password)).equals(user.getPassword())) {
				logger.error(" Username or password  error 404 !" + email);
				response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
				return JsonTools.getJson(result);
			}

			logger.info("password Dtype start!");
			// 非教师在此登陆
			if (!UserEnum.Dtype.TEACHER.toString().equals(user.getDtype())) {
				logger.error(" Username type error 404 !" + email);
				response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
				return JsonTools.getJson(result);
			}

			logger.info("teacher null start!");
			Teacher teacher = this.passportService.findTeacherById(user.getId());
			if (teacher == null) {
				logger.error(" Username teacher error 404 !" + email);
				response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
				return JsonTools.getJson(result);
			}

	        String rsultStr = this.checkUser(teacher, result);
            if(StringUtils.isNotEmpty(rsultStr)){
               return  rsultStr;
            }

			// 如果招聘Id不存在则set进去
			if (StringUtils.isEmpty(teacher.getRecruitmentId())) {
				teacher.setRecruitmentId(this.passportService.updateRecruitmentId(teacher));
			}

			// 如果用户不存在token 则新增一个token
			if (StringUtils.isEmpty(user.getToken())) {
				user = this.passportService.updateUserToken(user);
			}

			/* 判断老师的LifeCycle，进行项目跳转 */
			logger.info("登陆  REGULAR start !");
			if (TeacherLifeCycle.REGULAR.toString().equals(teacher.getLifeCycle())) {
				logger.info("to teacher 200 !");
                String token = this.appRestfulService.saveUpdateToken(user);
				result.put("token", token);
				result.put("status", AppEnum.LoginStatus.OK.val());				
				return JsonTools.getJson(result);
			}else{
                 String token = this.appRestfulService.saveUpdateToken(user);
                 result.put("token", token);
	             result.put("status", AppEnum.LoginStatus.NO_REGULAR.val());             
	             return JsonTools.getJson(result);
			}
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(RestfulConfig.HttpStatus.STATUS_500);
        }
		return JsonTools.getJson(result);
	}

	@RequestMapping(value = "/app/authByToken", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public @ResponseBody String getTeacherByToken(HttpServletRequest request, HttpServletResponse response, @RequestParam String token) {
	    Map<String, Object> result = Maps.newHashMap();
	    if(StringUtils.isBlank(token)){
            logger.error("token 不能为空！");
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
            return JsonTools.getJson(result);
        }
		try {
		    Preconditions.checkArgument(StringUtils.isNotBlank(token));
			String teacherId = this.appRestfulService.getUserIdByToken(token);
			if(StringUtils.isBlank(teacherId)){
			    response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
			    return JsonTools.getJson(result);
			}
			long teacher = Long.valueOf(teacherId);
			return getTeacherById(request, response, teacher);
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(RestfulConfig.HttpStatus.STATUS_500);
        }
		return JsonTools.getJson(result);
	}

	@RequestMapping(value = "/app/authById", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public @ResponseBody String getTeacherById(HttpServletRequest request, HttpServletResponse response, @RequestParam long teacherId) {
	    Map<String, Object> result = Maps.newHashMap();
	    if(teacherId == 0){
            logger.error("teacherId 不能为0！");
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
            return JsonTools.getJson(result);
        }
		try {
	        Preconditions.checkArgument(teacherId != 0);
			Teacher teacher = this.passportService.findTeacherById(teacherId);
            if(teacher == null){
                logger.error("teacher is null！");
                response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
                return JsonTools.getJson(result);
           }
			String rsultStr = this.checkUser(teacher, result);
			if(StringUtils.isNotEmpty(rsultStr)){
			   return  rsultStr;
			}
			AppTeacher ateacher = this.appRestfulService.findByTeacherId(teacher);
			if (null != ateacher) {
                result.put("status", AppEnum.LoginStatus.OK.val());
                result.put("data", ateacher);
                return JsonTools.getJson(result);
			}else{
			    response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
			}
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(RestfulConfig.HttpStatus.STATUS_500);
        }
		return JsonTools.getJson(result);
	}
	
	
	@RequestMapping(value = "/app/forgetPassword", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
    public @ResponseBody String getPassword(HttpServletRequest request, HttpServletResponse response,
            @RequestParam String email) {
        Map<String, Object> result = Maps.newHashMap();
        if(StringUtils.isBlank(email)){
            logger.error("email 不能为空！");
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
            return JsonTools.getJson(result);
        }
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(email));
            // 根据email，检查是否有此账号。
            User user = this.passportService.findUserByUsername(email);
            if (null == user) {
                response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
                return JsonTools.getJson(result);
            }
            // 检查用户类型
            if (!UserEnum.Dtype.TEACHER.toString().equals(user.getDtype())) {
                response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
                return JsonTools.getJson(result);
            }
            //teacher 判断
            Teacher teacher = this.passportService.findTeacherById(user.getId());
            if (teacher == null) {
                response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
                return JsonTools.getJson(result);
            }
            String resultStr = this.checkUser(teacher, result);
            if(StringUtils.isNotEmpty(resultStr)){
                return resultStr;
            }
            //发送密码修改邮件
            Map<String,String> map = this.passportService.senEmailForPassword(user);
            if(ApplicationConstant.AjaxCode.SUCCESS_CODE.equals(map.get("info"))){
                result.put("status",  AppEnum.LoginStatus.OK.val());
                return JsonTools.getJson(result);
            }            
        } catch (IllegalArgumentException e) {
            logger.error("内部参数转化异常:"+e.getMessage());
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(RestfulConfig.HttpStatus.STATUS_500);
        }
        return JsonTools.getJson(result);
    }
	
	@RequestMapping(value = "/app/classCount", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public @ResponseBody String getClassCount(HttpServletRequest request, HttpServletResponse response,
            @RequestParam long teacherId,@RequestParam String classStatuses,@RequestParam(value="courseTypes", required=false) String courseTypes) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            Preconditions.checkArgument(teacherId != 0);
            Preconditions.checkArgument(StringUtils.isNotBlank(classStatuses));
            List<Map<String, Object>> list = this.appRestfulService.getCountOnlineClass(teacherId,classStatuses,courseTypes);
            result.put("data", list);
            return JsonTools.getJson(result);
        } catch (IllegalArgumentException e) {
            logger.error("参数不合法:"+e.getMessage());
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(RestfulConfig.HttpStatus.STATUS_500);
        }
        return JsonTools.getJson(result);
	    
	}
	
	/* 扩展使用
    public @ResponseBody String getClassCount(HttpServletRequest request, HttpServletResponse response,
            @RequestParam long teacherId, @RequestParam long startTime,@RequestParam long endTime,
            @RequestParam String timezone,@RequestParam String classStatuses,@RequestParam String courseTypes) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            Preconditions.checkArgument(teacherId != 0);
            List<Map<String, Object>> list = this.appRestfulService.getCountOnlineClass(teacherId,timezone,startTime, endTime, classStatuses,courseTypes);
            result.put("data", list);
            return JsonTools.getJson(result);
        } catch (IllegalArgumentException e) {
            logger.error("参数不合法:"+e.getMessage());
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(RestfulConfig.HttpStatus.STATUS_500);
        }
        return JsonTools.getJson(result);
    }
    */
	
   @RequestMapping(value = "/app/classList", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
   public @ResponseBody String getClassList(HttpServletRequest request, HttpServletResponse response,
            @RequestParam long teacherId, @RequestParam long startTime,@RequestParam(value="order", required=false) Integer order,
            @RequestParam long endTime,@RequestParam String classStatuses,@RequestParam(value="courseTypes", required=false) String courseTypes) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            Preconditions.checkArgument(teacherId != 0);
            Preconditions.checkArgument(startTime != 0);
            Preconditions.checkArgument(endTime != 0);
            Preconditions.checkArgument(StringUtils.isNotBlank(classStatuses));
            List<AppOnlineClass> list = this.appRestfulService.getClassList(teacherId,startTime, endTime,order,classStatuses,courseTypes);
            result.put("data", list);
            return JsonTools.getJson(result);
        } catch (IllegalArgumentException e) {
            logger.error("参数不合法:"+e.getMessage());
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(RestfulConfig.HttpStatus.STATUS_500);
        }
        return JsonTools.getJson(result);
    }
   
   @RequestMapping(value = "/app/classListPage", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
   public @ResponseBody String getClassListByPage(HttpServletRequest request, HttpServletResponse response,
            @RequestParam long teacherId,@RequestParam int classStatus,@RequestParam(value="courseTypes", required=false) String courseTypes,
            @RequestParam long order,@RequestParam long start,@RequestParam long limit) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            Preconditions.checkArgument(teacherId != 0);
            Preconditions.checkArgument(0 <= classStatus && classStatus < 3);
            //查询条数最多只能大于20条限制处理
            limit = limit > 20 ? 20 : limit;
            List<AppOnlineClass> list = this.appRestfulService.getClassListPage(teacherId,start, limit,order,classStatus,courseTypes);
            result.put("data", list);
            result.putAll(this.appRestfulService.getClassListCount(teacherId, classStatus, courseTypes));
            return JsonTools.getJson(result);
        } catch (IllegalArgumentException e) {
            logger.error("参数不合法:"+e.getMessage());
            response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(RestfulConfig.HttpStatus.STATUS_500);
        }
        return JsonTools.getJson(result);
    }
	

	@RequestMapping(value = "/app/studentList", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public @ResponseBody String getStudents(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String studentIds) {
		Map<String, Object> resultMap = Maps.newHashMap();

		try {
			Preconditions.checkArgument(StringUtils.isNotBlank(studentIds));

			String[] ids = StringUtils.split(studentIds, ",");
			List<Map<String, Object>> students = appRestfulService.getStudents(ids);
			
			Map<String, Object> dataMap = Maps.newHashMap();
			students.stream().forEach(student -> {
				String gender = (String) student.get("gender");
				if (StringUtils.isNotBlank(gender)) {
					student.put("gender", AppEnum.Gender.valueOf(gender).ordinal());
				}

				dataMap.put(student.get("id").toString(), student);
			});
			resultMap.put("data", dataMap);
		} catch (IllegalArgumentException e) {
		    logger.error("参数不合法:"+e.getMessage());
			response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
		} catch (Exception e) {
		    logger.error(e.getMessage(), e);
			response.setStatus(RestfulConfig.HttpStatus.STATUS_500);
		}

		return JsonTools.getJson(resultMap);
	}

	@RequestMapping(value = "/app/feedback", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public @ResponseBody String getOnlineClassComment(HttpServletRequest request, HttpServletResponse response,
			@RequestParam long studentId, @RequestParam long onlineClassId, @RequestParam long teacherId) {
		Map<String, Object> resultMap = Maps.newHashMap();

		try {
			Preconditions.checkArgument(0 != studentId);
			Preconditions.checkArgument(0 != onlineClassId);
			Preconditions.checkArgument(0 != teacherId);

			TeacherComment teacherComment = commentsService.getTeacherComment(studentId, onlineClassId, teacherId);
			if (null != teacherComment) {
				resultMap.put("id", teacherComment.getId());
				resultMap.put("onlineClassId", onlineClassId);
				resultMap.put("studentId", studentId);
				resultMap.put("teacherId", teacherId);
				resultMap.put("comment", teacherComment.getTeacherFeedback());
				resultMap.put("createTime", teacherComment.getCreateDateTime().getTime());
				resultMap.put("stars", teacherComment.getStars());
			} else {
				response.setStatus(RestfulConfig.HttpStatus.STATUS_404);
			}
		} catch (IllegalArgumentException e) {
		    logger.error("参数不合法:"+e.getMessage());
			response.setStatus(RestfulConfig.HttpStatus.STATUS_400);
		} catch (Exception e) {
		    logger.error(e.getMessage(), e);
			response.setStatus(RestfulConfig.HttpStatus.STATUS_500);
		}

		return JsonTools.getJson(resultMap);
	}

	/**
	 * FAIL  QUIT ACTIVITY  LOCKED
	 * @Author:ALong (ZengWeiLong)
	 * @param teacher
	 * @param result
	 * @return    
	 * String
	 * @date 2016年6月12日
	 */
	private String checkUser(Teacher teacher,Map<String, Object> result){
	    if(teacher == null){
	        return JsonTools.getJson(result);
	    }
	    logger.info("登陆  FAIL start !");
        // 检查老师状态是否FAIL
        if (TeacherEnum.LifeCycle.FAIL.toString().equals(teacher.getLifeCycle())) {
            logger.error(" Username fail error 200 !");
            result.put("status", AppEnum.LoginStatus.FAIL.val());               
            return JsonTools.getJson(result);
        }

        logger.info("登陆  QUIT start !");
        // 检查老师状态是否QUIT
        if (TeacherEnum.LifeCycle.QUIT.toString().equals(teacher.getLifeCycle())) {
            logger.error(" Username quit error 200 !");
            result.put("status", AppEnum.LoginStatus.QUIT.val());               
            return JsonTools.getJson(result);
        }
        // 检查用户状态是否锁住
        logger.info("登陆  isLocked start !");
        User user = this.passportService.findUserById(teacher.getId());
        if (UserEnum.Status.isLocked(user.getStatus())) {
            // 新注册的需要激活
            if (TeacherEnum.LifeCycle.SIGNUP.toString().equals(teacher.getLifeCycle())) {
                logger.error(" Username 没有激活 error 200 !");
                result.put("status", AppEnum.LoginStatus.ACTIVITY.val());
                return JsonTools.getJson(result);
            } else {
                // 否则告诉被锁定
                logger.error(" Username locked error 200 !");
                result.put("status", AppEnum.LoginStatus.LOCKED.val());
                return JsonTools.getJson(result);
            }
        }
        return null;
	}
}
