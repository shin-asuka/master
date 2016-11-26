package com.vipkid.trpm.controller.portal;

import com.google.api.client.util.Maps;
import com.vipkid.trpm.constant.ApplicationConstant.CourseType;
import com.vipkid.trpm.constant.ApplicationConstant.LoginType;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.service.activity.ActivityService;
import com.vipkid.trpm.service.passport.IndexService;
import com.vipkid.trpm.service.portal.ScheduleService;
import com.vipkid.trpm.service.rest.LoginService;
import com.vipkid.trpm.service.rest.TeacherPageLoginService;
import org.apache.commons.lang.StringUtils;
import org.community.config.PropertyConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Map;

@Controller
public class ScheduleController extends AbstractPortalController {

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private IndexService indexService;
	
	@Autowired
	private ActivityService activityService;
	
	@Autowired
	private TeacherPageLoginService teacherPageLoginService;
	
	@Autowired
    private LoginService loginService;

	@RequestMapping("/schedule")
	public String schedule(HttpServletRequest request, HttpServletResponse response, Model model) {
		/* 当前显示的课程类型 */
		String courseType = ServletRequestUtils.getStringParameter(request, "courseType", CourseType.MAJOR);
		model.addAttribute("courseType", courseType);

		/* 当前星期偏移量 */
		int offsetOfWeek = ServletRequestUtils.getIntParameter(request, "offsetOfWeek", 0);
		model.addAttribute("offsetOfWeek", offsetOfWeek);

		/* 获取当前登录的老师信息 */
		Teacher teacher = loginService.getTeacher();

		model.addAllAttributes(scheduleService.doSchedule(offsetOfWeek, teacher.getId(), teacher.getTimezone(),
				courseType));

		// 判断是否能上Practicum类型的课程
		model.addAttribute("showPracticum", false);
		if (loginService.enabledPracticum(teacher.getId())) {
		    model.addAttribute("showPracticum", teacherPageLoginService.isType(teacher.getId(), LoginType.PRACTICUM));
		}
		//判断是否显示AdminQuiz
		model.addAttribute("showAdminQuiz",teacherPageLoginService.isType(teacher.getId(), LoginType.ADMINQUIZ));
		
		//判断是否显示Evaluation
        model.addAttribute("showEvaluation",teacherPageLoginService.isType(teacher.getId(), LoginType.EVALUATION));
		
		// 判断是否需要显示24小时提示
		model.addAttribute("show24HoursInfo", scheduleService.isShow24HourInfo(request, response));
		
		//加入三周年活动参数
		model.addAttribute("isDuringThirdYeayAnniversary", activityService.isDuringThirdYeayAnniversary());
		
		String thirdYeayAnniversaryWebpageUrl = PropertyConfigurer.stringValue("third_year_anniversary_webpage_url");
		if(StringUtils.isEmpty(thirdYeayAnniversaryWebpageUrl)){
			thirdYeayAnniversaryWebpageUrl = "t.vipkid.com.cn";
		}
		model.addAttribute("thirdYeayAnniversaryWebpageUrl", thirdYeayAnniversaryWebpageUrl);

		return view("schedule");
	}

	@RequestMapping("/createTimeSlot")
	public String createTimeSlot(HttpServletRequest request, HttpServletResponse response, Model model) {
		/* 需新建的课程类型 */
		String courseType = ServletRequestUtils.getStringParameter(request, "courseType", null);
		String scheduleTime = ServletRequestUtils.getStringParameter(request, "scheduleTime", null);

		return jsonView(response,
				scheduleService.doCreateTimeSlotWithLock(loginService.getTeacher(), scheduleTime, courseType));
	}

	@RequestMapping("/cancelTimeSlot")
	public String cancelTimeSlot(HttpServletRequest request, HttpServletResponse response, Model model) {
		/* 需取消的课程类型 */
		String courseType = ServletRequestUtils.getStringParameter(request, "courseType", null);

		String scheduleTime = ServletRequestUtils.getStringParameter(request, "scheduleTime", null);
		long onlineClassId = ServletRequestUtils.getIntParameter(request, "onlineClassId", -1);

		return jsonView(response, scheduleService.doCancelTimeSlot(loginService.getTeacher(), onlineClassId,
				scheduleTime, courseType));
	}

	/** 密码修改 */
	@RequestMapping("/changePassword")
	public String changePassword(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("token",
				new String(Base64.getEncoder().encode(loginService.getUser().getPassword().getBytes())));
		return "portal/change_password";
	}

	@RequestMapping("/set24Hour")
	public String set24Hour(HttpServletRequest request, HttpServletResponse response, Model model) {
		int onlineClassId = ServletRequestUtils.getIntParameter(request, "onlineClassId", -1);
		int offsetOfWeek = ServletRequestUtils.getIntParameter(request, "offsetOfWeek", 0);
		/* 获取当前登录的老师信息 */
		Teacher teacher = loginService.getTeacher();

		Map<String, Object> resultMap = Maps.newHashMap();
		if (scheduleService.checkInOneHour(onlineClassId)) {
			resultMap.put("lessOneHourError", true);
			return jsonView(response, resultMap);
		}

		if (scheduleService.checkTimeSlots(teacher.getId(), teacher.getTimezone(), offsetOfWeek)) {
			resultMap.put("result", scheduleService.set24HourClass(teacher.getId(), onlineClassId));
		} else {
			resultMap.put("less15Error", true);
		}

		return jsonView(response, resultMap);
	}

	@RequestMapping("/delete24Hour")
	public String delete24Hour(HttpServletRequest request, HttpServletResponse response, Model model) {
		int onlineClassId = ServletRequestUtils.getIntParameter(request, "onlineClassId", -1);
		/* 获取当前登录的老师信息 */
		Teacher teacher = loginService.getTeacher();

		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("result", scheduleService.delete24HourClass(teacher.getId(), onlineClassId));

		return jsonView(response, resultMap);
	}

}
