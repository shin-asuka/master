package com.vipkid.portal.personal.controller;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
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

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.FormType;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.file.model.FileVo;
import com.vipkid.file.service.AwsFileService;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.portal.personal.model.ReferralTeacherVo;
import com.vipkid.portal.personal.model.TeachingInfoData;
import com.vipkid.portal.personal.service.PortalPersonalInfoService;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.teacher.tools.security.SHA256PasswordEncoder;
import com.vipkid.trpm.dao.TeacherTaxpayerFormDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherTaxpayerForm;
import com.vipkid.trpm.entity.TeacherTaxpayerFormDetail;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.portal.TeacherService;
import com.vipkid.trpm.service.portal.TeacherTaxpayerFormService;
import com.vipkid.trpm.util.AwsFileUtils;

/**
 * 
 * @author zhangbole
 *
 */
@RestController
@RestInterface(lifeCycle = {LifeCycle.REGULAR,LifeCycle.QUIT})
@RequestMapping("/portal/personal")
public class PortalPersonalInfoController extends RestfulController {
	private final Logger logger = LoggerFactory.getLogger(PortalPersonalInfoController.class);

	public static final int USERPASSWORD_MIN_SIZE = 6;
	public static final int USERPASSWORD_MAX_SIZE = 30;
	public static final String PASSWORD_REGEX1 = "[0-9a-zA-Z]+";// 只包含字母与数字
	public static final String PASSWORD_REGEX2 = "[0-9]+";// 只包含数字
	public static final String PASSWORD_REGEX3 = "[a-zA-Z]+";// 只包含字母

	@Autowired
	private PortalPersonalInfoService portalPersonalInfoService;

	@Autowired
	private AwsFileService awsFileService;

	@Autowired
	private TeacherTaxpayerFormService teacherTaxpayerFormService;

	@Autowired
	private TeacherTaxpayerFormDao teacherTaxpayerFormDao;

	@Autowired
	private TeacherService teacherService;

	@RequestMapping(value = "restTeachingInfo", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> restTeachingInfo(HttpServletRequest request, HttpServletResponse response) {
		Teacher teacher = getTeacher(request); 
		long teacherId = teacher.getId();
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restTeachingInfo接口。传入参数：teacherId = {}", teacherId);
			User user = getUser(request);
			TeachingInfoData data = portalPersonalInfoService.getTeachingInfoData(teacher, user);
			Map<String, Object> ret = ApiResponseUtils.buildSuccessDataResp(data);

			long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("结束调用restTeachingInfo接口。传入参数：teacherId = {}，返回json = {}。用时{}ms", teacherId,JsonUtils.toJSONString(ret), millis);
			return ret;
		} catch (Exception e) {
			logger.error("调用restTeachingInfo接口抛异常。传入参数：teacherId = {}，异常 = {}。", teacherId, e);
		}
		return ApiResponseUtils.buildErrorResp(1002, "服务器抛异常");
	}

	@RequestMapping(value = "restChangePassword", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> restChangPassword(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> param) {
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			// 验证入参的json
			String	currentPassword = (String)param.get("currentPassword");
			String	newPassword = (String) param.get("newPassword");
			logger.info("开始调用restChangPassword接口。传入参数：teacherId = {}", getTeacher(request).getId());
			Teacher teacher = getTeacher(request);
			User user = getUser(request);			
			Map<String, Object> ret = verifyPassword(currentPassword, newPassword, user, teacher);

			if (MapUtils.isEmpty(ret)) {// 如果验证通过,
				Map<String, Object> data = portalPersonalInfoService.updatePassword(teacher, user, currentPassword,
						newPassword, request, response);
				if ((Boolean) data.get("isSuccess")) {
					ret = ApiResponseUtils.buildSuccessDataResp(data);
				} else {
					ret = ApiResponseUtils.buildErrorResp(1002, "服务器端错误导致密码修改失败");
				}
			}

			long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info(
					"结束调用restChangPassword接口。传入参数：teacherId = {}，currentPassword = 缺省， newPassword = 缺省。返回json = {}。用时{}ms",getTeacher(request).getId(), JsonUtils.toJSONString(ret), millis);
			return ret;
		} catch (IllegalArgumentException e) {
			logger.warn("调用restChangePassword接口传入参数错误。param = {}", JsonUtils.toJSONString(param));
			return ApiResponseUtils.buildErrorResp(1001, "参数错误");
		} catch (Exception e) {
			logger.error("调用restChangPassword接口抛异常。传入参数：post json = {}。异常 = {}。", JsonUtils.toJSONString(param), e);
		}
		return ApiResponseUtils.buildErrorResp(1002, "抛异常");
	}

	@RequestMapping(value = "restTaxpayer", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> restTaxpayer(HttpServletRequest request, HttpServletResponse response) {
		long teacherId = getTeacher(request).getId();
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			logger.info("开始调用restTaxpayer接口。传入参数：teacherId = {}", teacherId);

			Map<String, Object> resultMap = portalPersonalInfoService.getTaxpayerData(teacherId);
			Map<String, Object> ret = ApiResponseUtils.buildSuccessDataResp(resultMap);

			long millis = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
			logger.info("结束调用restTaxpayer接口。传入参数：teacherId = {}。返回json = {}。用时{}ms", teacherId,
					JsonUtils.toJSONString(ret), millis);
			return ret;
		} catch (Exception e) {
			logger.error("调用restTaxpayer接口抛异常。传入参数：teacherId = {}。异常 = {}。", teacherId, e);
		}
		return ApiResponseUtils.buildErrorResp(1002, "抛异常");
	}

	@RequestMapping(value = "/restTaxpayerUpload", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8) // 上传文件接口
	public Map<String,Object> taxpayerUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("开始调用restTaxpayerUpload接口上传文件");
		try {
			Integer formType = FormType.W9.val();// 目前只有W9一种
			Teacher teacher = getTeacher(request);
			long teacherId = teacher.getId();
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

				fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), size);
				
				if (fileVo != null) {
					String url = bucketName + "/" + key;
					fileVo.setUrl(url);
					fileVo.setName(AwsFileUtils.getFileName(fileVo.getUrl()));
				}
			}
			return ApiResponseUtils.buildSuccessDataResp(fileVo);
		} catch(IllegalArgumentException e){
			logger.warn("调用restTaxpayerUpload接口发生非法参数异常，参数不合法", e);
		} catch (Exception e) {
			logger.error("调用restTaxpayerUpload接口抛异常，errorMessage = "+e.getMessage(), e);
		}
		return ApiResponseUtils.buildErrorResp(1002, "抛异常");
	}

	@RequestMapping(value = "/restSaveTaxpayer", method = RequestMethod.POST, produces = RestfulConfig.JSON_UTF_8)
	public Map<String, Object> saveTaxpayer(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> param) {
		logger.info("开始调用restSaveTaxpayer接口，传入参数param = {}", JsonUtils.toJSONString(param));
		try {


			String url = (String) param.get("url");
			Integer formType = FormType.W9.val();// 目前只有W9一种formType;
			Preconditions.checkArgument(StringUtils.isNotBlank(url), "url 不能为空!");
			Preconditions.checkArgument(formType != null, "formType 不能为空!");
			Long teacherTaxpayerFormId = null; //如果为空则创建，不为空则更新
			Teacher teacher = getTeacher(request);
			long teacherId = teacher.getId();

			if(null != param.get("id")) {
				teacherTaxpayerFormId = Long.valueOf(param.get("id") + "");


				// 验证接口入参id的合法性
				TeacherTaxpayerForm originTeacherTaxpayerForm = teacherTaxpayerFormDao.findById(teacherTaxpayerFormId);
				if (null != originTeacherTaxpayerForm && originTeacherTaxpayerForm.getTeacherId() != null
						&& !originTeacherTaxpayerForm.getTeacherId().equals(teacherId)) {// 如果id不合法
					logger.warn("调用restSaveTaxpayer接口,teacherId = {},恶意调用接口，传入非法id = {}。此Id对应的teacherTaxpayerForm并不属于此老师",
							teacherId, teacherTaxpayerFormId);

					return ApiResponseUtils.buildErrorResp(1001, "入参Id不合法");
				}
				if (StringUtils.equals(originTeacherTaxpayerForm.getUrl(), url)) {
					Map<String, Object> data = portalPersonalInfoService.getTaxpayerData(teacherId);
					return ApiResponseUtils.buildSuccessDataResp(data);// 提升效率，直接返回成功
				}

			}

			TeacherTaxpayerForm teacherTaxpayerForm = new TeacherTaxpayerForm();
			teacherTaxpayerForm.setId(teacherTaxpayerFormId);
			teacherTaxpayerForm.setUrl(url);
			teacherTaxpayerForm.setFormType(formType);
			setTeacherTaxpayerFormInfo(teacherTaxpayerForm, teacher);
			logger.info("调用restSaveTaxpayer接口，teacherTaxpayerForm = {}", JsonUtils.toJSONString(teacherTaxpayerForm));
			teacherTaxpayerFormService.saveTeacherTaxpayerForm(teacherTaxpayerForm);
			
			// 查库验证是否保存成功
			Map<String, Object> data = portalPersonalInfoService.getTaxpayerData(teacherId);
			if (MapUtils.isNotEmpty(data) && StringUtils.equals(url, (String) data.get("url"))) {// 如果url一样，就视为保存成功
				return ApiResponseUtils.buildSuccessDataResp(data);
			} else {
				logger.error("保存TeacherTaxpayerForm失败.teacherId = {},url={},teacherTaxpayerFormId={}", teacherId, url,teacherTaxpayerFormId);
				return ApiResponseUtils.buildErrorResp(2001, "保存TeacherTaxpayerForm失败");
			}

		} catch(IllegalArgumentException e){
			logger.warn("保存TaxpayerForm失败，发生非法参数异常，参数不合法，参数param = "+JsonUtils.toJSONString(param), e);
			return ApiResponseUtils.buildErrorResp(1002, "参数不合法");
		} catch (Exception e) {
			logger.error("保存TaxpayerForm失败，抛异常。e = {}", e);
			return ApiResponseUtils.buildErrorResp(1002, "抛异常");
		}
	}

	private void setTeacherTaxpayerFormInfo(TeacherTaxpayerForm teacherTaxpayerForm, Teacher teacher) {
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
		detail.setUploaderName(teacherName);
		teacherTaxpayerForm.setTeacherTaxpayerFormDetail(detail);

	}

	private Map<String, Object> verifyPassword(String currentPassword, String newPassword, User user, Teacher teacher) {
		//验证
		if(StringUtils.isBlank(currentPassword) || StringUtils.isBlank(newPassword)){
			logger.warn("新密码或原密码不能为空。teacherId = {}", teacher.getId());
			return ApiResponseUtils.buildErrorResp(1000, "输入密码错误！");
		}
		
		//验证密码格式
		if (newPassword.length() < USERPASSWORD_MIN_SIZE || newPassword.length() > USERPASSWORD_MAX_SIZE || 
				!newPassword.matches(PASSWORD_REGEX1) || newPassword.matches(PASSWORD_REGEX2) ||
				newPassword.matches(PASSWORD_REGEX3)) {
			
			logger.warn("老师越过前端限制修改密码，输入非法的密码格式。teacherId = {}, newPassword = {}", teacher.getId(), newPassword);
			return ApiResponseUtils.buildErrorResp(1001, "非法请求，密码格式不对");
		}

		//检查新旧密码是否相同
		if (StringUtils.equals(newPassword, currentPassword)) {
			logger.warn("老师越过前端限制修改密码，输入的原密码与新密码相同。teacherId = {}", teacher.getId());
			return ApiResponseUtils.buildErrorResp(1002, "非法请求，新密码与旧密码相同");
		}

		//验证用户密码
        SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
		String encodedPassword = encoder.encode(currentPassword);
		if (!StringUtils.equals(encodedPassword,user.getPassword())) {
			return ApiResponseUtils.buildErrorResp(2001, "原密码输入错误");
		}

		return null;// 如果验证通过，返回null
	}

	@RequestMapping(value = "/findReferrals", method = RequestMethod.POST)
	public Map<String, Object> findReferrals(HttpServletRequest request, HttpServletResponse response,@RequestBody ReferralTeacherVo bean) {
		try{
			if(bean.getDataType() != null){
				if(StringUtils.isBlank(bean.getStartTime()) || StringUtils.isBlank(bean.getEndTime())){
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					return ApiResponseUtils.buildErrorResp(1001, "result::[startTime] and [endTime] field is required!");
				}
			}

			Teacher teacher = getTeacher(request);
			bean.setTeacherId(teacher.getId());

			Map<String, Object> resultMap = Maps.newHashMap();
			resultMap.put("list", teacherService.findReferralTeachers(bean));
			resultMap.put("count", teacherService.findReferralTeachersCount(bean));
			return ApiResponseUtils.buildSuccessDataResp(resultMap);
		} catch (IllegalArgumentException e) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return ApiResponseUtils.buildErrorResp(2001,e.getMessage(),e);
		} catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return ApiResponseUtils.buildErrorResp(2002,e.getMessage(),e);
		}
 	}
}
