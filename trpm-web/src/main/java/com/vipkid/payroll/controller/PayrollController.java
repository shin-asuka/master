package com.vipkid.payroll.controller;

import java.time.LocalDateTime;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.vipkid.http.service.PayrollService;
import com.vipkid.payroll.model.Page;
import com.vipkid.payroll.model.PayrollItemVo;
import com.vipkid.payroll.model.PayrollPage;
import com.vipkid.payroll.model.Result;
import com.vipkid.payroll.utils.DateUtils;
import com.vipkid.payroll.utils.JsonMapper;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.controller.portal.AbstractPortalController;
import com.vipkid.trpm.entity.Teacher;

@Controller
public class PayrollController extends AbstractPortalController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PayrollController.class);
	@Resource
	private PayrollService payrollService;

	@Autowired
	private LoginService loginService;

	@RequestMapping(value = "/portal/payment")
	public Map<String, Object> payment(
			@RequestParam(value = "offsetOfMonth", required = false, defaultValue = "0") Integer offsetOfMonth,
			HttpServletRequest request, HttpServletResponse response) {
		Teacher teacher = loginService.getTeacher();
		// model.addAttribute("offsetOfMonth", offsetOfMonth);
		LocalDateTime monthOfYear = DateUtils.monthOfYear(offsetOfMonth);
		int month = monthOfYear.getYear() * 100 + monthOfYear.getMonthValue();
		Result result = new Result();
		LOGGER.info("获取老师工资详情, teacherId={} ,month={}", teacher.getId(), month);
		String message = "";
		Integer status = HttpStatus.OK.value();

		int teacherId = new Long(teacher.getId()).intValue();

		try {
			JSONObject jsonObject = payrollService.getPayrollItemByTeacherAndMonth(teacherId, month);
			if(jsonObject != null){
				for (String key : jsonObject.keySet()) {
					result.addAttribute(key, jsonObject.get(key));
				}
			}
			message = "查询成功";
		} catch (Exception e) {
			LOGGER.error("获取工资明细时出现异常 , uri="+request.getRequestURI(), e);
			message = "查询失败 ";
			status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		} finally {
			response.setStatus(status);
			result.addAttribute("message", message);
		}

		/* 月份偏移量 */
		result.addAttribute("offsetOfMonth", offsetOfMonth);

		/* 用于显示的月份 */
		result.addAttribute("monthOfYear", DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_MMM_YYYY_US));

		/* 当前显示的课程类型 */
		// if(teacher.getContractType() !=null){
		// boolean isTypeOne = isTypeOneContract(teacher.getContractType());
		// result.addAttribute("is2VersionContactType", !isTypeOne);
		// }
		result.addAttribute("linePerPage", LINE_PER_PAGE);
		return result.getAttribute();
	}

	@RequestMapping("/portal/priceList")
	public Map<String, Object> priceList(
			@RequestParam(value = "offsetOfMonth", required = false, defaultValue = "0") Integer offsetOfMonth,
			HttpServletRequest request, HttpServletResponse response) {
		Teacher teacher = loginService.getTeacher();
		LocalDateTime monthOfYear = DateUtils.monthOfYear(offsetOfMonth);
		int month = monthOfYear.getYear() * 100 + monthOfYear.getMonthValue();
		Integer teacherId = new Long(teacher.getId()).intValue();
		LOGGER.info("获取教师各种规则详情, teacherId={} ,month={}", teacher.getId(), month);
		String message = "";
		JSONObject jsonObject = null;
		Integer status = HttpStatus.OK.value();
		Result result = new Result();

		try {
			// 获取某个教师某月份的工资规则列表
			jsonObject = payrollService.findRuleByTeacherMonth(teacherId, month);
			if(jsonObject != null){
				for (Object key : jsonObject.keySet()) {
					result.addAttribute(key.toString(), jsonObject.get(key));
				}
			}
			message = "查询成功";
		} catch (Exception e) {
			LOGGER.error("request 请求PayRollServerProto异常 , uri="+request.getRequestURI(), e);
			message = "获取教师工资规则失败 ";
			status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		} finally {
			response.setStatus(status);
		}
		/* 用于显示的月份 */
		result.addAttribute("monthOfYear", DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_MMM_YYYY_US));
		result.addAttribute("message", message);
		result.addAttribute("offsetOfMonth", offsetOfMonth);
		return result.getAttribute();
	}

	@RequestMapping("/portal/salaryList")
	public Map<String, Object> salaryList(
			@RequestParam(value = "offsetOfMonth", required = false, defaultValue = "0") Integer offsetOfMonth,
			@RequestParam(value = "itemType") Integer itemType, HttpServletRequest request, HttpServletResponse response) {

		PayrollPage<PayrollItemVo> rePageAd = new PayrollPage<PayrollItemVo>();
		PayrollPage<PayrollItemVo> rePageDe = new PayrollPage<PayrollItemVo>();
		String message = "";
		Integer status = HttpStatus.OK.value();
		Teacher teacher = loginService.getTeacher();
		Integer allTotalSalary = 0;
		Integer deAllTotalSalary = 0;
		int teacherId = new Long(teacher.getId()).intValue();

		LocalDateTime monthOfYear = DateUtils.monthOfYear(offsetOfMonth);
		Result result = new Result();
		int month = monthOfYear.getYear() * 100 + monthOfYear.getMonthValue();
		//itemType = ServletRequestUtils.getIntParameter(request, "itemType", Result.SLALARY_TYPE_COURSE_ALL_RULE);
		result.addAttribute("offsetOfMonth", offsetOfMonth);

		try {
			LOGGER.info("获取教师各种规则详情, teacherId={} ,month={}", teacherId, month);
			Map<String, Object> param = Maps.newHashMap();
			param.put("month", month);
			param.put("teacherId", teacherId);

			Page payPage = new Page(request, response);
			if (itemType == 0) {
				String responseAd = payrollService.findPayrollItemByTypeWithPage(
						Result.SLALARY_TYPE_COURSE_ADDITION_RULE, teacherId, month, payPage);
				String responseDe = payrollService.findPayrollItemByTypeWithPage(
						Result.SLALARY_TYPE_COURSE_DEDUCTION_RULE, teacherId, month, payPage);
				if (responseAd != null) {

					rePageAd = (PayrollPage) JsonMapper.fromJsonString(responseAd, rePageAd.getClass());

					result.addAttribute(Result.ATTR_COURSE_TOTAL, rePageAd.getCount());
					allTotalSalary = rePageAd.getAllTotalSalary();
					message = "查询成功";
				} else {
					LOGGER.error("查询工资明细失败");
					message = "查询失败";
					status = HttpStatus.INTERNAL_SERVER_ERROR.value();
				}
				if (responseDe != null) {
					rePageDe = (PayrollPage) JsonMapper.fromJsonString(responseDe, rePageDe.getClass());
					deAllTotalSalary = rePageDe.getAllTotalSalary();
					result.addAttribute(Result.ATTR_DE_TOTAL, rePageDe.getCount());
					message = "查询成功";
				} else {
					LOGGER.error("RPC查询工资明细失败");
					message = "查询失败";
					status = HttpStatus.INTERNAL_SERVER_ERROR.value();
				}

			} else {
				param.put("itemType", itemType);
				LOGGER.info("request  uri={} , param={}", request.getRequestURI(), param);
				String responseAd = payrollService.findPayrollItemByTypeWithPage(itemType, teacherId, month, payPage);

				if (responseAd != null) {
					rePageAd = (PayrollPage) JsonMapper.fromJsonString(responseAd, rePageAd.getClass());
					result.addAttribute(Result.ATTR_COURSE_TOTAL, rePageAd.getCount());
					allTotalSalary = rePageAd.getAllTotalSalary();
					message = "查询成功";
				} else {
					LOGGER.error("RPC查询工资明细失败");
					message = "查询失败";
					status = HttpStatus.INTERNAL_SERVER_ERROR.value();
				}
			}

			if (itemType == 0) {
				result.addAttribute(Result.ATTR_PAGE, rePageAd);
				result.addAttribute(Result.ATTR_PAGE_DE, rePageDe);

			} else {
				result.addAttribute(Result.ATTR_PAGE, rePageAd);
			}
			result.addAttribute("offsetOfMonth", offsetOfMonth);

		} catch (Exception e) {
			LOGGER.error("查询工资明细时出现异常 , uri="+request.getRequestURI(), e);
			message = "查询失败 ";
			status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		} finally {
			response.setStatus(status);
			result.addAttribute("message", message);

		}
		return result.getAttribute();
	}
	
	

	@RequestMapping("/portal/listReferral")
	public Map<String, Object> listReferral(
			@RequestParam(value = "offsetOfMonth", required = false, defaultValue = "0") Integer offsetOfMonth,
			 HttpServletRequest request, HttpServletResponse response) {

		PayrollPage<PayrollItemVo> rePage = new PayrollPage<PayrollItemVo>();
		String message = "";
		Integer status = HttpStatus.OK.value();
		Teacher teacher = loginService.getTeacher();
		int teacherId = new Long(teacher.getId()).intValue();

		LocalDateTime monthOfYear = DateUtils.monthOfYear(offsetOfMonth);
		Result result = new Result();
		int month = monthOfYear.getYear() * 100 + monthOfYear.getMonthValue();
		result.addAttribute("offsetOfMonth", offsetOfMonth);
		int allTotalSalary = 0 ;
		try {
			LOGGER.info("获取referral详情, teacherId={} ,month={}", teacherId, month);
			Map<String, Object> param = Maps.newHashMap();
			param.put("month", month);
			param.put("teacherId", teacherId);

			Page payPage = new Page(request, response);
			String responseAd = payrollService.listReferralWithPage(
					Result.SLALARY_TYPE_OTHER_BONUS_TEACHER_REFERRAL_FEE_RULE, teacherId, month, payPage);

			if (responseAd != null) {
				rePage = (PayrollPage) JsonMapper.fromJsonString(responseAd, rePage.getClass());
				result.addAttribute(Result.ATTR_COURSE_TOTAL, rePage.getCount());
				allTotalSalary = rePage.getAllTotalSalary();
			}
			message = "查询成功";
			result.addAttribute(Result.ATTR_PAGE, rePage);
			result.addAttribute("offsetOfMonth", offsetOfMonth);

		} catch (Exception e) {
			LOGGER.error("查询工资明细时出现异常 , uri={} ,e={}", request.getRequestURI(), e);
			message = "查询失败 ";
			status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		} finally {
			response.setStatus(status);
			result.addAttribute("message", message);
		}
		return result.getAttribute();
	}
}
