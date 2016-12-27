package com.vipkid.trpm.controller.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vipkid.dataSource.annotation.Slave;
import com.vipkid.enums.OnlineClassEnum.CourseType;
import com.vipkid.enums.TeacherPageLoginEnum.LoginType;
import com.vipkid.rest.service.LoginService;
import com.vipkid.rest.service.TeacherPageLoginService;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.service.portal.ClassroomsService;
import com.vipkid.trpm.util.DateUtils;

@Controller
public class ClassroomsController extends AbstractPortalController {

	@Autowired
	private ClassroomsService classroomsService;
	
    /*@Autowired
    private IndexService indexService;*/
    
    @Autowired
    private TeacherPageLoginService teacherPageLoginService;

    @Autowired
    private LoginService loginService;

    @Deprecated
	@Slave
	@RequestMapping("/classrooms")
	public String classrooms(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	return "redirect:/classrooms";//强行重定向前后端分离classrooms页面
//    	
//		/* 月份偏移量 */
//		int offsetOfMonth = ServletRequestUtils.getIntParameter(request, "offsetOfMonth", 0);
//		model.addAttribute("offsetOfMonth", offsetOfMonth);
//
//		/* 用于显示的月份 */
//		model.addAttribute("monthOfYear", DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_MMM_YYYY_US));
//
//		/* 当前显示的课程类型 */
//		String courseType = ServletRequestUtils.getStringParameter(request, "courseType", CourseType.MAJOR.toString());
//		model.addAttribute("courseType", courseType);
//		model.addAttribute("linePerPage", LINE_PER_PAGE);
//
//		Teacher teacher = loginService.getTeacher();
//
//		// 判断是否能上Practicum类型的课程
//		if (loginService.isPe(teacher.getId())) {
//			model.addAttribute("showLayer", teacherPageLoginService.isType(teacher.getId(),LoginType.CLASSROOMS));
//		}
//
//		/* 根据不同类型加载不同数据 */
//		String monthOfYear = DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_YM);
//		model.addAllAttributes(classroomsService.doClassrooms(teacher, monthOfYear, courseType, LINE_PER_PAGE));
//
//		return view("classrooms");
	}

    @Deprecated
	@Slave
	@RequestMapping("/majorList")
	public String majorList(HttpServletRequest request, HttpServletResponse response, Model model) {
		int curPage = ServletRequestUtils.getIntParameter(request, "curPage", 1);

		/* 月份偏移量 */
		int offsetOfMonth = ServletRequestUtils.getIntParameter(request, "offsetOfMonth", 0);

		/* 用于显示的月份 */
		model.addAttribute("monthOfYear", DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_MMM_YYYY_US));

		Teacher teacher = loginService.getTeacher();

		/* 计算日期 */
		String monthOfYear = DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_YM);
		model.addAllAttributes(classroomsService.majorList(teacher, monthOfYear, curPage, LINE_PER_PAGE));

		return jsonView();
	}

    @Deprecated
	@Slave
	@RequestMapping("/practicumList")
	public String practicumList(HttpServletRequest request, HttpServletResponse response, Model model) {
		int curPage = ServletRequestUtils.getIntParameter(request, "curPage", 1);

		/* 月份偏移量 */
		int offsetOfMonth = ServletRequestUtils.getIntParameter(request, "offsetOfMonth", 0);

		/* 用于显示的月份 */
		model.addAttribute("monthOfYear", DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_MMM_YYYY_US));

		Teacher teacher = loginService.getTeacher();

		/* 计算日期 */
		String monthOfYear = DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_YM);
		model.addAllAttributes(classroomsService.practicumList(teacher, monthOfYear, curPage, LINE_PER_PAGE));

		return jsonView();
	}

	/**
	 * 根据serialNumber显示不同的Report链接
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
    @Deprecated
	@RequestMapping("/showReport")
	public String showReport(HttpServletRequest request, HttpServletResponse response, Model model) {
		String serialNumber = ServletRequestUtils.getStringParameter(request, "serialNumber", null);
		long scheduledTime = ServletRequestUtils.getLongParameter(request, "scheduledTime", 0);
		
		/*参数**/
		long onlineClassId = ServletRequestUtils.getLongParameter(request, "onlineClassId", 0);
		long lessonId = ServletRequestUtils.getLongParameter(request, "lessonId", 0);
		long studentId = ServletRequestUtils.getLongParameter(request, "studentId", 0);

		model.addAttribute("serialNumber", serialNumber);
		model.addAttribute("onlineClassId", onlineClassId);
		model.addAttribute("studentId", studentId);
		model.addAttribute("lessonId",lessonId);
		/*Practicum 报告显示*/
		if(serialNumber.startsWith("P")){
			model.addAllAttributes(classroomsService.practicumReport(serialNumber, onlineClassId, lessonId, studentId,scheduledTime));
		/*Major报告显示*/
		}else{
			model.addAllAttributes(classroomsService.doShowReport(serialNumber, onlineClassId, lessonId, studentId,scheduledTime));
		}
		
		String viewName = (String) model.asMap().get("viewName");
		return view(viewName);
	}

	/**
	 * 显示materials
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param lessonId
	 * @param serialNumber
	 * @return
	 */
    @Deprecated
	@RequestMapping("/showMaterials")
	public String showMaterials(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam long lessonId, @RequestParam String serialNumber) {
		model.addAttribute("serialNumber", serialNumber);
		model.addAllAttributes(classroomsService.doShowMaterials(lessonId));

		return view("materials");
	}
}
