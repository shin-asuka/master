package com.vipkid.trpm.controller.portal;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.entity.*;
import com.vipkid.trpm.entity.personal.BasicInfo;
import com.vipkid.trpm.entity.personal.ChangePassword;
import com.vipkid.trpm.entity.personal.TeacherBankVO;
import com.vipkid.trpm.security.SHA256PasswordEncoder;
import com.vipkid.trpm.service.passport.IndexService;
import com.vipkid.trpm.service.passport.RemberService;
import com.vipkid.trpm.service.portal.PersonalInfoService;
import com.vipkid.trpm.service.portal.TeacherAddressService;
import com.vipkid.trpm.service.portal.TeacherTaxpayerFormService;
import com.vipkid.trpm.service.rest.LoginService;
import com.vipkid.trpm.util.CookieUtils;
import com.vipkid.trpm.validator.ChangePasswordValidator;

/**
 * 基本信息修改
 * 
 * @author ALong
 *
 */
@Controller
public class PersonalInfoController extends AbstractPortalController {

	private Logger logger = LoggerFactory.getLogger(PersonalInfoController.class);

	@Resource
    PersonalInfoService personalInfoService;

	@Resource
	private ChangePasswordValidator changePasswordValidator;

	@Resource
	private SHA256PasswordEncoder mSHA256PasswordEncoder;

	@Autowired
	private IndexService indexService;

	@Autowired
	private RemberService remberService;

	@Autowired
	private TeacherAddressService teacherAddressService;
	
	@Autowired
	private TeacherTaxpayerFormService teacherTaxpayerFormService;
	
	@Autowired
    private LoginService loginService;
	

	@RequestMapping("/personalInfo")
	public String personalInfo(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.info("页面初始化");
		
		//传一个参数给前端，让前端判断是否提醒用户填写bank信息
		Teacher teacher = loginService.getTeacher();
		String bankCardNumber = teacher.getBankCardNumber();
		boolean isRemindEditBankInfo = bankCardNumber==null||bankCardNumber.isEmpty();//如果没有银行卡号，就提醒
		model.addAttribute("isRemindEditBankInfo", isRemindEditBankInfo);
		getBasicinfoView(request, response, model);
		String param = request.getParameter("p");
		model.addAttribute("index", param);
		return view("personal_info");
	}

	/**
	 * 老师的PersonalInfo首页面
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/personal/password")
	public String personalPassword(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.info("old页面初始化：子页面name = password");
		return view("personal/personal_password");
	}

	@RequestMapping("/personal/bankinfo_edit")
	public String getBankInfoEditView(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.info("页面初始化：子页面name = bankinfo_edit");

		/* 获取当前登录的老师信息 */
		Teacher teacher = loginService.getTeacher();

		// 查询国家信息
		model.addAttribute("countrys", personalInfoService.getCountrys());

		// 查询老师的当前地址
		model.addAttribute("beneficiaryAddress",
				personalInfoService.getTeacherAddress(teacher.getBeneficiaryAddressId()));

		// 查询老师银行所在的地址
		if (teacher.getBeneficiaryBankAddressId() != 0) {
			model.addAttribute("beneficiaryBankAddress",
					personalInfoService.getTeacherAddress(teacher.getBeneficiaryBankAddressId()));
		}
		return view("personal/personal_bankinfo_edit");
	}

	@RequestMapping("/personal/teaching")
	public String getTeaching(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.info("页面初始化：子页面name = teaching");
		/* 获取当前登录的老师信息 */
		Teacher teacher = loginService.getTeacher();
		model.addAllAttributes(personalInfoService.personalTeaching(teacher.getId()));
		return view("personal/personal_teaching");
	}

	@RequestMapping("/personal/basicinfo")
	public String getBasicinfoView(HttpServletRequest request, HttpServletResponse response, Model model) {
		Teacher teacher = loginService.getTeacher();
		TeacherAddress currentAddress = personalInfoService.getTeacherAddress(teacher.getCurrentAddressId());
		if (currentAddress != null) {
			TeacherLocation currentCountry = personalInfoService.getLocationById(currentAddress.getCountryId());
			TeacherLocation currentState = personalInfoService.getLocationById(currentAddress.getStateId());
			TeacherLocation currentCity = personalInfoService.getLocationById(currentAddress.getCity());
			model.addAttribute("currentAddress", currentAddress);
			model.addAttribute("currentCountry", currentCountry);
			model.addAttribute("currentState", currentState);
			model.addAttribute("currentCity", currentCity);
			model.addAttribute("currentAddress", currentAddress);
		}
		return view("personal/personal_basicinfo");
	}

	@RequestMapping("/personal/basicinfo_edit")
	public String getBasicinfoEdit(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.info("页面初始化：子页面name = basicinfo_edit");

		/* 获取当前登录的老师信息 */
		Teacher teacher = loginService.getTeacher();

		// 查询国家代码
		model.addAttribute("teacherNationalityCodes", personalInfoService.getTeacherNationalityCodes());

		// 缺省美国 代码
		TeacherNationalityCode tnc = null;
		if (teacher.getPhoneNationId() != 0) {
			tnc = personalInfoService.getTeacherNationalityCode(teacher.getPhoneNationId(),
					teacher.getPhoneNationCode());
		} else {
			tnc = personalInfoService.getTeacherNationalityCode(224, "+1");
		}

		model.addAttribute("currentNationalityCode", tnc);
		// 查询国家列表
		model.addAttribute("countrys", personalInfoService.getCountrys());

		// 查询老师的当前地址
		model.addAttribute("currentAddress", personalInfoService.getTeacherAddress(teacher.getCurrentAddressId()));

		return view("personal/personal_basicinfo_edit");
	}

	@RequestMapping("/personal/bankinfo")
	public String getBankinfoView(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.info("页面初始化：子页面name = bankinfo");

		/* 获取当前登录的老师信息 */
		Teacher teacher = loginService.getTeacher();

		String accountName  = teacher.getBankAccountName();
        accountName  = personalInfoService.hideNameInfo(accountName);

		String accountNumber = teacher.getBankCardNumber();
		int len = accountNumber.length();
		accountNumber = personalInfoService.hideInfo(accountNumber,len-4,len-1);

		String swiftCode = teacher.getBankSwiftCode();
		len = swiftCode.length();
		swiftCode = personalInfoService.hideInfo(swiftCode,len-2,len-1);

		String bankABARoutingNumber = teacher.getBankABARoutingNumber();
		len = bankABARoutingNumber.length();
        bankABARoutingNumber = personalInfoService.hideInfo(bankABARoutingNumber,len-4,len-1);

		String bankACHNumber= teacher.getBankACHNumber();
		len = bankACHNumber.length();
        bankACHNumber = personalInfoService.hideInfo(bankACHNumber,len-4,len-1);


		String idNumber = teacher.getIdentityNumber();
		idNumber = personalInfoService.hideInfo(idNumber,0,0);

        model.addAttribute("accountName",accountName);
        model.addAttribute("accountNumber",accountNumber);
        model.addAttribute("swiftCode",swiftCode);
        model.addAttribute("bankABARoutingNumber",bankABARoutingNumber);
        model.addAttribute("bankACHNumber",bankACHNumber);
        model.addAttribute("idNumber",idNumber);

		TeacherAddress beneficiaryAddress = personalInfoService.getTeacherAddress(teacher.getBeneficiaryAddressId());
		if (null != beneficiaryAddress) {
			TeacherLocation beneficiaryCountry = personalInfoService.getLocationById(beneficiaryAddress.getCountryId());
			TeacherLocation beneficiaryState = personalInfoService.getLocationById(beneficiaryAddress.getStateId());
			TeacherLocation beneficiaryCity = personalInfoService.getLocationById(beneficiaryAddress.getCity());
			model.addAttribute("beneficiaryAddress", beneficiaryAddress);
			model.addAttribute("beneficiaryCountry", beneficiaryCountry);
			model.addAttribute("beneficiaryState", beneficiaryState);
			model.addAttribute("beneficiaryCity", beneficiaryCity);
		}
		TeacherAddress beneficiaryBankAddress = personalInfoService.getTeacherAddress(teacher
				.getBeneficiaryBankAddressId());
		if (beneficiaryBankAddress != null) {
			TeacherLocation beneficiaryBankCountry = personalInfoService.getLocationById(beneficiaryBankAddress
					.getCountryId());
			TeacherLocation beneficiaryBankState = personalInfoService.getLocationById(beneficiaryBankAddress
					.getStateId());
			TeacherLocation beneficiaryBankCity = personalInfoService.getLocationById(beneficiaryBankAddress.getCity());
			model.addAttribute("beneficiaryBankAddress", beneficiaryBankAddress);
			model.addAttribute("beneficiaryBankCountry", beneficiaryBankCountry);
			model.addAttribute("beneficiaryBankState", beneficiaryBankState);
			model.addAttribute("beneficiaryBankCity", beneficiaryBankCity);
		}
		TeacherLocation issuanceCountry = personalInfoService.getLocationById(teacher.getIssuanceCountry());
		model.addAttribute("issuanceCountry", issuanceCountry);

		return view("personal/personal_bankinfo");
	}

	/**
	 * 处理老师基本信息更新
	 * 
	 * @param request
	 * @param response
	 * @param basicInfo
	 * @return
	 */
	@RequestMapping(value = "/setBasicInfoAction", method = RequestMethod.POST)
	public String setBasicInfoAction(HttpServletRequest request, HttpServletResponse response, BasicInfo basicInfo,
			TeacherAddress teacherAddress) {
		Teacher teacher = loginService.getTeacher();
		teacherAddress.setTeacherId((int) teacher.getId());
		Map<String, Object> modelMap = personalInfoService.doSetBasicInfo(teacher, basicInfo, teacherAddress);
		
		User user = (User) loginService.getUser();
		loginService.setLoginCooke(response, user);
		return jsonView(response, modelMap);
	}

	/**
	 * 处理用户密码修改逻辑
	 * 
	 * @param request
	 * @param response
	 * @param changePassword
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/changePasswordAction", method = RequestMethod.POST)
	public String changePasswordAction(HttpServletRequest request, HttpServletResponse response,
			ChangePassword changePassword, BindingResult result) {
		Map<String, Object> modelMap = new HashMap<String, Object>();

		changePasswordValidator.validate(changePassword, result);
		if (result.hasFieldErrors()) {
			modelMap.put("patternErr", true);
			return jsonView(response, modelMap);
		}

		// 验证用户密码
		String encodedPassword = mSHA256PasswordEncoder.encode(changePassword.getOriginalPassword());
		//新密码不相等
		boolean a = StringUtils.equals(encodedPassword, loginService.getUser().getPassword());
		//获取的旧密码解密后与
		boolean b = false;
		try{
		    //前面强制修改密码处理比较
		    b = new String(Base64.getDecoder().decode(changePassword.getOriginalPassword())).equals(loginService.getUser().getPassword());
		}catch(Exception e){
		   logger.error("changePasswordAction error",e);
		}
		if ( !a && !b) {
			modelMap.put("originalPasswordErr", true);
			return jsonView(response, modelMap);
		}

		Teacher teacher = loginService.getTeacher();

		// 执行密码修改操作
		String encodedNewPassword = mSHA256PasswordEncoder.encode(changePassword.getUserpassword());
		modelMap = personalInfoService.doChangePassword(teacher.getId(), encodedNewPassword);
		remberService.delkeys(request, response);
		CookieUtils.removeCookie(response, CookieKey.TRPM_CHANGE_WINDOW, null, null);

		return jsonView(response, modelMap);
	}

	/**
	 * 处理老师银行信息更新
	 * 
	 * @param request
	 * @param response
	 * @param bankInfo
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/setBankInfoAction", method = RequestMethod.POST)
	public String setBankInfoAction(HttpServletRequest request, HttpServletResponse response, TeacherBankVO bankInfo) {
		logger.info("setBankInfoAction");
		Teacher teacher = loginService.getTeacher();
		Map<String, Object> modelMap = personalInfoService.doSetBankInfo(teacher, bankInfo);
		return jsonView(response, modelMap);
	}

	/**
	 * 修改avatar的请求处理
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/uploadAvatar")
	public String uploadAvatar(HttpServletRequest request, HttpServletResponse response, Model model) {
		return view("personal/upload_avatar");
	}

	/**
	 * 处理上传avatar
	 * 
	 * @param request
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
	public String upload(MultipartHttpServletRequest request, HttpServletResponse response,
			@RequestParam("file") MultipartFile file) {
		Teacher teacher = loginService.getTeacher();
		Map<String, Object> modelMap = personalInfoService.doUploadImage(file, teacher.getId());
		return jsonView(response, modelMap);
	}

}
