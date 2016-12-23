package com.vipkid.trpm.controller.portal;

import com.google.common.collect.Maps;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.teachercomment.TeacherCommentResult;
import com.vipkid.trpm.service.portal.TeacherService;
import com.vipkid.trpm.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
public class CommentsController extends AbstractPortalController {

    @Autowired
    private LoginService loginService;

	@Autowired
	private TeacherService teacherService;
    
	/**
	 * 老师的Comments首页面
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/comments")
	public String comments(HttpServletRequest request, HttpServletResponse response, Model model) {
		/* 月份偏移量 */
		int offsetOfMonth = ServletRequestUtils.getIntParameter(request, "offsetOfMonth", 0);
		model.addAttribute("offsetOfMonth", offsetOfMonth);

		/* 用于显示的月份 */
		model.addAttribute("monthOfYear",
				DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_MMM_YYYY_US));
		model.addAttribute("linePerPage", LINE_PER_PAGE);

		String monthOfYear = DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_YM);
		Teacher teacher = loginService.getTeacher();
		Map<String, Object> modelMap = Maps.newHashMap();
		List<TeacherCommentResult> results = teacherService
				.findTeacherCommentByTeacherIdAndMonthOfYear(teacher.getId(), monthOfYear,
						teacher.getTimezone(), null, null);
		modelMap.put("totalLine", CollectionUtils.isEmpty(results) ? 0 : results.size());
		model.addAllAttributes(modelMap);

		return view("comments");
	}

	/**
	 * 老师的Comments分页列表
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/commentsList")
	public String commentsList(HttpServletRequest request, HttpServletResponse response, Model model) {
		int curPage = ServletRequestUtils.getIntParameter(request, "curPage", 1);

		/* 月份偏移量 */
		int offsetOfMonth = ServletRequestUtils.getIntParameter(request, "offsetOfMonth", 0);
		String monthOfYear = DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_YM);

		Teacher teacher = loginService.getTeacher();
		List<TeacherCommentResult> dataList = teacherService
				.findTeacherCommentByTeacherIdAndMonthOfYear(teacher.getId(), monthOfYear,
						teacher.getTimezone(), curPage, LINE_PER_PAGE);
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("dataList", dataList);
		model.addAllAttributes(paramMap);

		return jsonView();
	}

}
