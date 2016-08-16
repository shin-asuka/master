package com.vipkid.payroll.controller;

import java.time.LocalDateTime;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.vipkid.mq.service.PayrollMessageService;
import com.vipkid.neo.client.NeoClient;
import com.vipkid.payroll.model.Page;
import com.vipkid.payroll.model.Result;
import com.vipkid.payroll.service.StudentService;
import com.vipkid.payroll.utils.DateUtils;
import com.vipkid.payroll.utils.ProtoUtils;
import com.vipkid.service.neo.grpc.FindPayrollItemByTypeWithPageResponse;
import com.vipkid.service.neo.grpc.FindRuleResponse;
import com.vipkid.service.neo.grpc.PayrollItemResponse;
import com.vipkid.trpm.controller.portal.AbstractPortalController;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.service.passport.IndexService;
import com.vipkid.trpm.service.portal.ReportService;

@Controller
public class PayrollController extends AbstractPortalController {

	

	@Autowired
	private IndexService indexService;

	private static final Logger LOGGER = LoggerFactory.getLogger(PayrollController.class);

	@Resource
	private NeoClient neoClient;

	@Autowired
    private ReportService reportService;
	
	@Autowired
	private PayrollMessageService payrollMessageService;
	
	@Autowired
	private StudentService studentService;
	 
	@RequestMapping("/payroll")
	public String payroll(HttpServletRequest request, HttpServletResponse response, Model model) {
		int offsetOfMonth = ServletRequestUtils.getIntParameter(request, "offsetOfMonth", 0);
		Teacher teacher = indexService.getTeacher(request);
		model.addAttribute("offsetOfMonth", offsetOfMonth);
		LocalDateTime monthOfYear = DateUtils.monthOfYear(offsetOfMonth);
		int month = monthOfYear.getYear() * 100 + monthOfYear.getMonthValue();

		LOGGER.info("获取老师工资详情, teacherId={} ,month={}", teacher.getId(), month);
		String message = "";
		Integer status = HttpStatus.OK.value();
		
	    int teacherId = new Long(teacher.getId()).intValue();
	    String payrollType = ServletRequestUtils.getStringParameter(request, "payrollType",
				"SALARY");
	    if(payrollType!=null&&payrollType.contains("PRICE")){
	    	return priceList(request, response, model,payrollType);
	    }
		
//	    teacherId = 1167406;
//		month = 201605;
		
		try {

			PayrollItemResponse payrollRpc = neoClient.getPayrollItemByTeacherAndMonth(teacherId, month);
			JSONObject jsonObject = JSONObject.fromObject(payrollRpc.getPayrollStr());
			for (Object key : jsonObject.keySet()) {
				model.addAttribute(key.toString(), jsonObject.get(key));
			}
			message = "查询成功";
		} catch (Exception e) {
			LOGGER.error("获取工资明细时出现异常 , uri={} ,e={}", request.getRequestURI(), e);
			message = "查询失败 ";
			status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		} finally {
			response.setStatus(status);
			model.addAttribute("message", message);
		}

		/* 月份偏移量 */

		model.addAttribute("offsetOfMonth", offsetOfMonth);

		/* 用于显示的月份 */
		model.addAttribute("monthOfYear",
				DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_MMM_YYYY_US));

		/* 当前显示的课程类型 */
		if(teacher.getContractType() !=null){
			boolean isTypeOne = isTypeOneContract(teacher.getContractType());
			model.addAttribute("is2VersionContactType", !isTypeOne);
		}
		model.addAttribute("payrollType", payrollType);
		model.addAttribute("linePerPage", LINE_PER_PAGE);
		return view("payroll");
	}
	
	private boolean isTypeOneContract(String contract) {
		return StringUtils.contains(contract, "1.0");
	}
	
	
	 @RequestMapping("/priceList")
	public String priceList(HttpServletRequest request, HttpServletResponse response, Model model, String payrollType) {
		int offsetOfMonth = ServletRequestUtils.getIntParameter(request, "offsetOfMonth", 0);
		Teacher teacher = indexService.getTeacher(request);
		model.addAttribute("offsetOfMonth", offsetOfMonth);
		LocalDateTime monthOfYear = DateUtils.monthOfYear(offsetOfMonth);
		int month = monthOfYear.getYear() * 100 + monthOfYear.getMonthValue() ;
		Integer teacherId = new Long(teacher.getId()).intValue();
		
//		teacherId = 1167406;
//		month = 201605;
		
		LOGGER.info("获取教师各种规则详情, teacherId={} ,month={}", teacher.getId(), month);

		String message = "";
		JSONObject jsonObject = null;
		Integer status = HttpStatus.OK.value();
		
		try {
			// 获取某个教师某月份的工资规则列表
			FindRuleResponse responseRpc = neoClient.findRuleByTeacherMonth(teacherId, month);
			jsonObject = JSONObject.fromObject(responseRpc.getPriceStr());
			for (Object key : jsonObject.keySet()) {
				model.addAttribute(key.toString(), jsonObject.get(key));
			}
			message = "查询成功";
		} catch (Exception e) {
			LOGGER.error("request 请求PayRollServerProto异常 , uri={} ,e={}", request.getRequestURI(),
					e);
			e.printStackTrace();
			message = "获取教师工资规则失败 ";
			//status = HttpStatus.INTERNAL_SERVER_ERROR.value();
			status = 200;
		} finally {
			response.setStatus(status);
		}
		/* 用于显示的月份 */
		model.addAttribute("monthOfYear",
				DateUtils.monthOfYear(offsetOfMonth, DateUtils.FMT_MMM_YYYY_US));
		model.addAttribute("payrollType", payrollType);
		model.addAttribute("message", message);
		return view("payroll");
	}
	 @RequestMapping("/salaryList")
	public String salaryList(HttpServletRequest request, HttpServletResponse response, Model model) {
		Integer itemType = null;
		Page page = null;
		Page dePage = null;
		String message = "";
		Integer status = HttpStatus.OK.value();
		Teacher teacher = indexService.getTeacher(request);
		Integer allTotalSalary = 0;
		Integer deAllTotalSalary = 0;
		int teacherId = new Long(teacher.getId()).intValue();
		int offsetOfMonth = ServletRequestUtils.getIntParameter(request, "offsetOfMonth", 0);
		LocalDateTime monthOfYear = DateUtils.monthOfYear(offsetOfMonth);
		int month = monthOfYear.getYear() * 100 + monthOfYear.getMonthValue() ;
		itemType = ServletRequestUtils.getIntParameter(request, "itemType",
				Result.SLALARY_TYPE_COURSE_ALL_RULE);
		//Date..formatTo(instant, formatter)
		
//		teacherId = 1167406;
//		month = 201605;
		
		model.addAttribute("offsetOfMonth", offsetOfMonth);
		
		try {
			LOGGER.info("获取教师各种规则详情, teacherId={} ,month={}", teacherId, month);
			Map<String, Object> param = Maps.newHashMap();
			param.put("month", month);
			param.put("teacherId", teacherId);
			if (itemType == 0) {
				FindPayrollItemByTypeWithPageResponse responseRpcAdd = neoClient
						.findPayrollItemByTypeWithPage(ProtoUtils.buildPage(request),
								Result.SLALARY_TYPE_COURSE_ADDITION_RULE,
								teacherId, month);
				FindPayrollItemByTypeWithPageResponse responseRpcDe = neoClient
						.findPayrollItemByTypeWithPage(ProtoUtils.buildPage(request),
								Result.SLALARY_TYPE_COURSE_DEDUCTION_RULE, teacherId, month);
				if (responseRpcAdd != null) {
					page = ProtoUtils.protoToPage(responseRpcAdd.getPage());
					model.addAttribute(Result.ATTR_COURSE_TOTAL, page.getCount());
					page.setList(ProtoUtils.ListStrToList(responseRpcAdd.getListStr()));
					allTotalSalary = responseRpcAdd.getAllTotalSalary();
					message = "查询成功";
				} else {
					LOGGER.error("RPC查询工资明细失败");
					message = "查询失败";
					status = HttpStatus.INTERNAL_SERVER_ERROR.value();
				}
				if (responseRpcDe != null) {
					dePage = ProtoUtils.protoToPage(responseRpcDe.getPage());
					dePage.setList(ProtoUtils.ListStrToList(responseRpcDe.getListStr()));
					deAllTotalSalary = responseRpcDe.getAllTotalSalary();					
					model.addAttribute(Result.ATTR_DE_TOTAL, dePage.getCount());
					message = "查询成功";
				} else {
					LOGGER.error("RPC查询工资明细失败");
					message = "查询失败";
					status = HttpStatus.INTERNAL_SERVER_ERROR.value();
				}

			} else {
				param.put("itemType", itemType);
				LOGGER.info("request  uri={} , param={}", request.getRequestURI(), param);
				FindPayrollItemByTypeWithPageResponse responseRpcAdd = neoClient
						.findPayrollItemByTypeWithPage(ProtoUtils.buildPage(request), itemType,
								new Long(teacher.getId()).intValue(), month);

				if (responseRpcAdd != null) {
					page = ProtoUtils.protoToPage(responseRpcAdd.getPage());
					page.setList(ProtoUtils.ListStrToList(responseRpcAdd.getListStr()));
					allTotalSalary = responseRpcAdd.getAllTotalSalary();
					message = "查询成功";
				} else {
					LOGGER.error("RPC查询工资明细失败");
					message = "查询失败";
					status = HttpStatus.INTERNAL_SERVER_ERROR.value();
				}
			}

			LOGGER.info("PayrollServerProto请求 , method = findSalaryPage, page={} ,param={}",
					ProtoUtils.buildPage(request), param);

		} catch (Exception e) {
			LOGGER.error("查询工资明细时出现异常 , uri={} ,e={}", request.getRequestURI(), e);
			message = "查询失败 ";
			status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		} finally {
			response.setStatus(status);
			model.addAttribute("message", message);
			if (itemType == 0) {
				model.addAttribute(Result.ATTR_PAGE, page);
				model.addAttribute("allTotalSalary", allTotalSalary);
				if (page != null) {
					model.addAttribute("dataList", page.getList());
				}
				
				model.addAttribute(Result.ATTR_PAGE_DE, dePage);
				model.addAttribute("allTotalSalaryde", deAllTotalSalary);
				if (dePage != null) {
					model.addAttribute("deDataList", dePage.getList());
				}
				
			} else {
				model.addAttribute(Result.ATTR_PAGE, page);
				model.addAttribute("allTotalSalary", allTotalSalary);
				model.addAttribute("dataList", page.getList());
			}
		}
		return jsonView();
	}
//
//	 @ResponseBody
//	 @RequestMapping("/doComment")
//	 public String doComment(HttpServletRequest request){
//		TeacherComment teacherComment = new TeacherComment();
//		teacherComment.setId(51003L);
//		teacherComment.setFeedbackTranslation("test comment");
//		reportService.submitTeacherComment( teacherComment , getUser(request));
//		return "success";
//	 }
//	 
//	 @ResponseBody
//	 @RequestMapping("/doua")
//	 public String doua(HttpServletRequest request){
//		Long onlineClassId = 1526947L;
//		payrollMessageService.sendFinishOnlineClassMessage(onlineClassId, OperatorType.ADD_UNIT_ASSESSMENT);
//		
//		return "success";
//	 }
//	 
//	 @ResponseBody
//	 @RequestMapping("/doTrial")
//	 public String doTrial(HttpServletRequest request){
//		Long onlineClassId = 2577268L;
//		onlineClassId = 2577503L; //ua
//		payrollMessageService.sendFinishOnlineClassMessage(onlineClassId, OperatorType.ADD_UNIT_ASSESSMENT);
//		
//		return "success";
//	 }
//	 
//	 @ResponseBody
//	 @RequestMapping("/isPaid")
//	 public String isPaid(HttpServletRequest request){
//		Long studentId = 329L;
//		studentId = 11525191L;
////		studentId = 1599758L;
//		String paidDateTime = "2015-03";
//		paidDateTime = "2016-02";
////		paidDateTime = "2016-05";
//		
//		Boolean flag = studentService.findIsPaidByStudentIdAndPayDate(studentId, paidDateTime);
//		System.out.println("isPaid : " +flag);
//		return "res : "+flag;
//	 }
}
