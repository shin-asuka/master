package com.vipkid.portal.personal.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.community.config.PropertyConfigurer;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.util.Maps;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.FormType;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.file.model.FileVo;
import com.vipkid.file.service.AwsFileService;
import com.vipkid.file.utils.ActionHelp;
import com.vipkid.file.utils.StringUtils;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.portal.personal.model.TeachingInfoData;
import com.vipkid.portal.personal.service.PersonalInfoRestService;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;
import com.vipkid.trpm.entity.TeacherTaxpayerFormDetail;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.entity.personal.TaxpayerView;
import com.vipkid.trpm.security.SHA256PasswordEncoder;
import com.vipkid.trpm.service.portal.TeacherTaxpayerFormService;
import com.vipkid.trpm.util.AwsFileUtils;

/**
 * 
 * @author zhangbole
 *
 */
@RestController
@RestInterface(lifeCycle = LifeCycle.REGULAR)
@RequestMapping("/portal/personal")
public class TeacherPortalPersonalInfoRestController extends RestfulController {
	private final Logger logger = LoggerFactory.getLogger(TeacherPortalPersonalInfoRestController.class);

	public static final int USERPASSWORD_MIN_SIZE = 6;
	public static final int USERPASSWORD_MAX_SIZE = 30;
	public static final String PASSWORD_REGEX1 = "[0-9a-zA-Z]+";// 只包含字母与数字
	public static final String PASSWORD_REGEX2 = "[0-9]+";// 只包含数字
	public static final String PASSWORD_REGEX3 = "[a-zA-Z]+";// 只包含字母

	@Autowired
	private PersonalInfoRestService personalInfoRestService;

	@Autowired
	private AwsFileService awsFileService;

	@Autowired
	private TeacherTaxpayerFormService teacherTaxpayerFormService;

	@Autowired
	private SHA256PasswordEncoder mSHA256PasswordEncoder;

	@RequestMapping(value = "restTeachingInfo", method = RequestMethod.GET)
	public Map<String, Object> restTeachingInfo(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "teacherId", required = true) long teacherId) {
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restTeachingInfo接口。传入参数：teacherId = {}", teacherId);

			Teacher teacher = getTeacher(request);
			User user = getUser(request);
			if (teacherId != teacher.getId()) {
				return ApiResponseUtils.buildErrorResp(1001, "教师id非法");
			}
			if (null == teacher || null == user) {
				return ApiResponseUtils.buildErrorResp(1001, "教师未登录");
			}

			TeachingInfoData data = personalInfoRestService.getTeachingInfoData(teacher, user);
			Map<String, Object> result = ApiResponseUtils.buildSuccessDataResp(data);

			long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("结束调用restTeachingInfo接口。传入参数：teacherId = {}，返回json = {}。用时{}ms", teacherId,
					JsonUtils.toJSONString(result), millis);
			return result;
		} catch (Exception e) {
			logger.error("调用restTeachingInfo接口抛异常。传入参数：teacherId = {}，异常 = {}。", teacherId, e);
		}
		return ApiResponseUtils.buildErrorResp(1002, "服务器抛异常");
	}

	@RequestMapping(value = "restChangePassword", method = RequestMethod.POST)
	public Map<String, Object> restChangPassword(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> param) {
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();

			Map<String, Object> ret = Maps.newHashMap();

			List<Result> list = ValidateUtils.checkBean(param, false);
			if (CollectionUtils.isNotEmpty(list) && list.get(0).isResult()) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				logger.warn("resultCheck:" + JsonTools.getJson(list));
				logger.warn("调用restChangePassword接口传入参数错误。param = {}", JsonUtils.toJSONString(param));
				return ApiResponseUtils.buildErrorResp(1001, "参数错误");
			}
			Long teacherId = null;
			String currentPassword = null;
			String newPassword = null;
			try {
				Integer teacherIdInt = (Integer) param.get("teacherId");
				teacherId = Long.valueOf(teacherIdInt);
				currentPassword = (String) param.get("currentPassword");
				newPassword = (String) param.get("newPassword");
			} catch (Exception e) {
				logger.warn("调用restChangePassword接口传入参数错误。param = {}", JsonUtils.toJSONString(param));
				return ApiResponseUtils.buildErrorResp(1001, "参数错误");
			}

			logger.info("开始调用restChangPassword接口。传入参数：teacherId = {}", teacherId);

			Teacher teacher = getTeacher(request);
			User user = getUser(request);
			if (teacherId != teacher.getId()) {
				return ApiResponseUtils.buildErrorResp(1001, "教师id非法");
			}

			if (null == user || null == teacher) {
				return ApiResponseUtils.buildErrorResp(1001, "教师未登录");
			}

			ret = verifyPassword(currentPassword, newPassword, user, teacher);

			if (null == ret) {// 如果验证通过,
				Map<String, Object> data = personalInfoRestService.changePassword(teacher, user, currentPassword,
						newPassword, request, response);
				if ((Boolean) data.get("isSuccess")) {
					ret = ApiResponseUtils.buildSuccessDataResp(data);
				} else {
					ret = ApiResponseUtils.buildErrorResp(1002, "服务器端错误导致密码修改失败");
				}
			}

			long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info(
					"结束调用restChangPassword接口。传入参数：teacherId = {}，currentPassword = 缺省， newPassword = 缺省。返回json = {}。用时{}ms",
					teacherId, JsonUtils.toJSONString(ret), millis);
			return ret;
		} catch (Exception e) {
			logger.error("调用restChangPassword接口抛异常。传入参数：post json = {}。异常 = {}。", JsonUtils.toJSONString(param), e);
		}
		return ApiResponseUtils.buildErrorResp(1002, "抛异常");
	}

	@RequestMapping(value = "restTaxpayer", method = RequestMethod.GET)
	public Map<String, Object> restTaxpayer(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "teacherId", required = true) long teacherId) {
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restTaxpayer接口。传入参数：teacherId = {}", teacherId);

			Teacher teacher = getTeacher(request);
			if (null == teacher) {
				return ApiResponseUtils.buildErrorResp(1001, "teacher未登录");
			}
			if (teacher.getId() != teacherId) {
				return ApiResponseUtils.buildErrorResp(1001, "非法teacherId");
			}

			Map<String, Object> data = personalInfoRestService.getTaxpayerData(teacherId);
			Map<String, Object> ret = ApiResponseUtils.buildSuccessDataResp(data);

			long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("结束调用restTaxpayer接口。传入参数：teacherId = {}。返回json = {}。用时{}ms", teacherId,
					JsonUtils.toJSONString(ret), millis);
			return ret;
		} catch (Exception e) {
			logger.error("调用restTaxpayer接口抛异常。传入参数：teacherId = {}。异常 = {}。", teacherId, e);
		}
		return ApiResponseUtils.buildErrorResp(1002, "抛异常");
	}

	@RequestMapping(value = "/restTaxpayerUpload", method = RequestMethod.POST) // 上传文件接口
	public void taxpayerUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("开始调用restTaxpayerUpload接口");
		try {
			Integer formType = FormType.W9.val();// 目前只有W9一种

			Teacher teacher = getTeacher(request);
			if (null == teacher) {
				Map<String, Object> ret = ApiResponseUtils.buildErrorResp(1001, "老师未登录，不能访问此接口");
				ActionHelp.WriteStrToOut(response, ret);
				return;
			}
			Long teacherId = teacher.getId();
			String teacherName = teacher.getRealName();

			logger.info("开始调用restTaxpayerUpload接口，teacherId = {}", teacherId);

			logger.info("upload taxpayer  teacherId = {}, teacherName = {}, formType = {},file = {}", teacherId,
					teacherName, TeacherEnum.getFormTypeById(formType), file);
			FileVo fileVo = null;
			if (file != null) {
				String name = file.getOriginalFilename();
				String bucketName = PropertyConfigurer.stringValue("aws.bucketName");
				String fileName = AwsFileUtils.reNewFileName(name); // 处理文件名
				FormType formTypeEnum = TeacherEnum.getFormTypeById(formType);
				String formTypeName = formTypeEnum.name();
				String awsName = teacherId + "-" + fileName;
				if (!name.equals(fileName)) {
					awsName = teacherId + "-" + formTypeName + "-" + fileName;
				}
				String key = AwsFileUtils.getTaxpayerkey(teacherId, awsName);
				Long size = file.getSize();

				Preconditions.checkArgument(AwsFileUtils.checkTaxPayerFileType(name),
						"文件类型不正确，支持类型为" + AwsFileUtils.TAPXPAYER_FILE_TYPE);
				Preconditions.checkArgument(AwsFileUtils.checkTaxPayerFileSize(size),
						"文件太大，maxSize = " + AwsFileUtils.TAPXPAYER_FILE_MAX_SIZE);

				try {
					fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), size);
				} catch (IOException e) {
					logger.error("上传文件失败。teacherId = {}", teacherId);
					throw new RuntimeException(e);
				}

				if (fileVo != null) {
					String url = bucketName + "/" + key;
					fileVo.setUrl(url);
				}
			}
			Map<String, Object> ret = ApiResponseUtils.buildSuccessDataResp(fileVo);
			ActionHelp.WriteStrToOut(response, ret); // 解决中文乱码问题
		} catch (Exception e) {
			logger.error("调用restTaxpayerUpload接口抛异常，e = ", e);
		}
		Map<String, Object> ret = ApiResponseUtils.buildErrorResp(1002, "抛异常");
		ActionHelp.WriteStrToOut(response, ret);
	}

	@RequestMapping(value = "/restSaveTaxpayer", method = RequestMethod.POST)
	public Map<String, Object> saveTaxpayer(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> param) {

		logger.info("开始调用restSaveTaxpayer接口，传入参数param = {}", JsonUtils.toJSONString(param));
		try {
			Long id = null;
			String url = null;
			if (null != param) {
				id = Long.valueOf((int) param.get("id"));
				url = (String) param.get("url");
			}

			Integer formType = FormType.W9.val();// 目前只有W9一种;

			Teacher teacher = getTeacher(request);
			if (null == teacher) {
				logger.warn("开始调用restSaveTaxpayer接口，传入参数param = {}。教师未登录，调用失败", JsonUtils.toJSONString(param));
				return ApiResponseUtils.buildErrorResp(1001, "老师未登录");
			}
			logger.info("save taxpayer formType = {} url={} ", formType, url);

			Preconditions.checkArgument(StringUtils.isNotBlank(url), "url 不能为空!");
			Preconditions.checkArgument(formType != null, "formType 不能为空!");

			TeacherTaxpayerForm teacherTaxpayerForm = new TeacherTaxpayerForm();
			teacherTaxpayerForm.setId(id);
			teacherTaxpayerForm.setUrl(url);
			teacherTaxpayerForm.setFormType(formType);
			setTeacherTaxpayerFormInfo(teacherTaxpayerForm, teacher);
			teacherTaxpayerFormService.saveTeacherTaxpayerForm(teacherTaxpayerForm);

			TaxpayerView taxpayerView = teacherTaxpayerFormService
					.getTeacherTaxpayerView(teacherTaxpayerForm.getTeacherId());
			return ApiResponseUtils.buildSuccessDataResp(taxpayerView);
		} catch (Exception e) {
			logger.error("保存TaxpayerForm失败。e = {}", e);
		}
		return ApiResponseUtils.buildErrorResp(1001, "抛异常");
	}

	private void setTeacherTaxpayerFormInfo(TeacherTaxpayerForm teacherTaxpayerForm, Teacher teacher) {
		if (null == teacher) {
			return;
		}
		Long teacherId = teacher.getId();
		String teacherName = teacher.getRealName();

		teacherTaxpayerForm.setTeacherId(teacherId);
		teacherTaxpayerForm.setTeacherName(teacherName);
		teacherTaxpayerForm.setUploader(teacherId);
		teacherTaxpayerForm.setUploadTime(new Date());

		teacherTaxpayerForm.setIsNew(TeacherEnum.ISNew.NEW.val());
		teacherTaxpayerForm.setUploaded(TeacherEnum.UploadStatus.UPLOADED.val());
		teacherTaxpayerForm.setCreateBy(teacherId);
		teacherTaxpayerForm.setUpdateBy(teacherId);

		TeacherTaxpayerFormDetail detail = new TeacherTaxpayerFormDetail();
		teacherTaxpayerForm.setTeacherTaxpayerFormDetail(detail);

		detail.setUploaderName(teacherName);
	}

	private Map<String, Object> verifyPassword(String currentPassword, String newPassword, User user, Teacher teacher) {
		// 验证密码格式
		if (null == newPassword || newPassword.length() < USERPASSWORD_MIN_SIZE
				|| newPassword.length() > USERPASSWORD_MAX_SIZE || !newPassword.matches(PASSWORD_REGEX1)
				|| newPassword.matches(PASSWORD_REGEX2) || newPassword.matches(PASSWORD_REGEX3)) {
			logger.warn("老师越过前端限制修改密码，输入非法的密码格式。teacherId = {}, newPassword = {}", teacher.getId(), newPassword);
			return ApiResponseUtils.buildErrorResp(1001, "非法请求，密码格式不对");
		}

		// 检查新旧密码是否相同
		if (newPassword.equals(currentPassword)) {
			return ApiResponseUtils.buildErrorResp(1001, "非法请求，新密码与旧密码相同");
		}

		// 验证用户密码
		String encodedPassword = null;
		if (null != currentPassword) {
			encodedPassword = mSHA256PasswordEncoder.encode(currentPassword);
		}

		if (encodedPassword == null || !encodedPassword.equals(user.getPassword())) {
			return ApiResponseUtils.buildErrorResp(2001, "原密码输入错误");
		}

		return null;// 如果验证通过，返回null
	}
}
