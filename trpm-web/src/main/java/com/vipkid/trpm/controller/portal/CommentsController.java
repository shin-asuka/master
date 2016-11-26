package com.vipkid.trpm.controller.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.service.passport.IndexService;
import com.vipkid.trpm.service.portal.CommentsService;
import com.vipkid.trpm.service.rest.LoginService;
import com.vipkid.trpm.util.DateUtils;

@Controller
public class CommentsController extends AbstractPortalController {

    @Autowired
	private CommentsService commentsService;

    @Autowired
    private IndexService indexService;
    
    @Autowired
    private LoginService loginService;
    
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
		model.addAllAttributes(commentsService.doComments(teacher.getId(), monthOfYear,
				teacher.getTimezone()));

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
		model.addAllAttributes(commentsService.doCommentsList(teacher.getId(), monthOfYear,
				teacher.getTimezone(), curPage, LINE_PER_PAGE));

		return jsonView();
	}

}