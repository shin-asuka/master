package com.vipkid.trpm.service.portal;

import static com.vipkid.trpm.util.DateUtils.FMT_YMD;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.StringUtils;
import com.google.common.collect.Maps;
import com.vipkid.trpm.constant.ApplicationConstant.MediaType;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.*;
import com.vipkid.trpm.entity.media.UploadResult;
import com.vipkid.trpm.entity.personal.BasicInfo;
import com.vipkid.trpm.entity.personal.TeacherBankVO;
import com.vipkid.trpm.service.media.AbstarctMediaService;
import com.vipkid.trpm.service.media.OSSMediaService;

@Service
public class PersonalInfoService {

	private static final Logger logger = LoggerFactory.getLogger(PersonalInfoService.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private TeacherDao teacherDao;

	@Autowired
	private TeacherCertificatedCourseDao teacherCertificatedCourseDao;

	@Autowired
	private AbstarctMediaService mediaService;

	@Autowired
	private TeacherNationalityCodeDao teacherNationalityCodeDao;

	@Autowired
	private TeacherLocationDao teacherLocationDao;

	@Autowired
	private TeacherAddressDao teacherAddressDao;

	/**
	 * 处理用户密码修改逻辑
	 * 
	 * @param teacherId
	 * @param encodedNewPassword
	 * @return Map<String, Object>
	 */
	public Map<String, Object> doChangePassword(long teacherId, String encodedNewPassword) {
		Map<String, Object> modelMap = Maps.newHashMap();

		if (0 != userDao.updateWithNewPassword(teacherId, encodedNewPassword)) {
			modelMap.put("action", true);
		} else {
			modelMap.put("action", false);
		}

		return modelMap;
	}

	/**
	 * 处理银行信息更新逻辑
	 * 
	 * @param teacherId
	 * @param bankInfo
	 */
	public Map<String, Object> doSetBankInfo(Teacher teacher, TeacherBankVO bankInfo) {
		Map<String, Object> modelMap = Maps.newHashMap();
		modelMap.put("action", true);

		int teacherId = (int) teacher.getId();
		// bank account info
		TeacherAddress teacherBankAddress = new TeacherAddress();
		teacherBankAddress.setTeacherId(teacherId);

		Timestamp updateTime = new Timestamp((new java.util.Date()).getTime());

		teacherBankAddress.setId(teacher.getBeneficiaryBankAddressId());
		teacherBankAddress.setCity(bankInfo.getBankCityId());
		teacherBankAddress.setStateId(bankInfo.getBankStateId());
		teacherBankAddress.setCountryId(bankInfo.getBankCountryId());
		teacherBankAddress.setStreetAddress(bankInfo.getBankStreetAddress());
		teacherBankAddress.setZipCode(bankInfo.getBankZipCode());
		teacherBankAddress.setUpdateTime(updateTime);
		teacherAddressDao.updateOrSave(teacherBankAddress);
		// 获取id
		int beneficiaryBankAddressId = teacherBankAddress.getId();

		TeacherAddress teacherBeneficiaryAddress = new TeacherAddress();
		teacherBeneficiaryAddress.setTeacherId(teacherId);
		teacherBeneficiaryAddress.setId(teacher.getBeneficiaryAddressId());
		teacherBeneficiaryAddress.setCity(bankInfo.getBeneficiaryCityId());
		teacherBeneficiaryAddress.setStateId(bankInfo.getBeneficiaryStateId());
		teacherBeneficiaryAddress.setCountryId(bankInfo.getBeneficiaryCountryId());
		teacherBeneficiaryAddress.setStreetAddress(bankInfo.getBeneficiaryStreetAddress());
		teacherBeneficiaryAddress.setZipCode(bankInfo.getBeneficiaryZipCode());
		teacherBeneficiaryAddress.setUpdateTime(updateTime);
		teacherAddressDao.updateOrSave(teacherBeneficiaryAddress);
		// 获取id
		int beneficiaryAddressId = teacherBeneficiaryAddress.getId();

		teacher.setBeneficiaryAddressId(beneficiaryAddressId);
		teacher.setBeneficiaryBankAddressId(beneficiaryBankAddressId);
		//

		//
		teacher.setBankName(bankInfo.getBeneficiaryBankName());
		teacher.setBankAccountName(bankInfo.getBeneficiaryAccountName());
		teacher.setBankCardNumber(bankInfo.getBeneficiaryAccountNumber());

		teacher.setBankSwiftCode(bankInfo.getSwiftCode());
		teacher.setBankABARoutingNumber(bankInfo.getBankABARoutingNumber());
		teacher.setBankACHNumber(bankInfo.getBankACHNumber());

		teacher.setIdentityType(bankInfo.getIdType());
		teacher.setPassport(bankInfo.getPassportURL());

		teacher.setIdentityNumber(bankInfo.getIdNumber());
		teacher.setIssuanceCountry(bankInfo.getIssuanceCountryId());

		teacherDao.update(teacher);
		modelMap.put("teacher", teacher);
		return modelMap;
	}

	/**
	 * 处理基本信息更新逻辑
	 * 
	 * @param teacherId
	 * @param basicInfo
	 * @return Map<String, Object>
	 */
	public Map<String, Object> doSetBasicInfo(Teacher teacher, BasicInfo basicInfo, TeacherAddress teacherAddress) {
		Map<String, Object> modelMap = Maps.newHashMap();
		modelMap.put("action", true);

		teacher.setAddress(basicInfo.getAddress());
		
		teacher.setEmail(basicInfo.getEmail());
		teacher.setIntroduction(basicInfo.getIntroduction());
		teacher.setPhoneNationCode(basicInfo.getPhoneNationCode());
		teacher.setPhoneNationId(basicInfo.getPhoneNationId());
		teacher.setMobile(basicInfo.getMobile());

		teacher.setSkype(basicInfo.getSkype());
		teacher.setTimezone(basicInfo.getTimezone());
		teacher.setGraduatedFrom(basicInfo.getGraduatedFrom());
		teacher.setHighestLevelOfEdu(basicInfo.getHighestLevelOfEdu());
		teacher.setEvaluationBio(basicInfo.getEvaluationBio());
		teacher.setPhoneType(basicInfo.getPhoneType());

		if (!StringUtils.isEmpty(basicInfo.getBirthday())) {
			LocalDate localDate = LocalDate.parse(basicInfo.getBirthday(), FMT_YMD);
			teacher.setBirthday(java.sql.Date.valueOf(localDate));
		}

		teacherAddress.setId(basicInfo.getCurrentAddressId());
		teacherAddressDao.updateOrSave(teacherAddress);
		logger.info("Update teacher address: {}", JsonTools.getJson(teacherAddress));

		teacher.setCurrentAddressId(teacherAddress.getId());
		
		//country 
		String strCountry = basicInfo.getCountry();
		teacher.setCountry(strCountry);
		teacherDao.update(teacher);
		logger.info("Update teacher basic info: {}", JsonTools.getJson(teacher));

		User user = new User();
		user.setId(teacher.getId());
		user.setGender(basicInfo.getGender());
		user.setName(basicInfo.getName());

		userDao.update(user);
		
		return modelMap;
	}

	/**
	 * 处理头像上传
	 * 
	 * @param file
	 * @param teacherId
	 * @return Map<String, Object>
	 */
	public Map<String, Object> doUploadImage(MultipartFile file, long teacherId) {
		Map<String, Object> modelMap = Maps.newHashMap();

		logger.info("upload file for filename = {}", file.getOriginalFilename());

		String fileSize = String.valueOf(file.getSize());
		UploadResult uploadResult = mediaService.handleUpload(file, MediaType.AVATAR, fileSize, null);

		modelMap.put("uploadResult", uploadResult);

		// 成功后，更新数据库
		if (uploadResult.isResult()) {
			Teacher teacher = new Teacher();
			teacher.setId(teacherId);
			teacher.setAvatar(uploadResult.getEncodeUrl());
			teacherDao.update(teacher);

			// aliyun对avater图片进行压缩裁剪
			logger.info("now compact the avatar img: {}", uploadResult.getUrl());
			OSSMediaService.compactAvatarInAliyun(uploadResult.getUrl());

			modelMap.put("teacher", teacherDao.findById(teacherId));
		}

		return modelMap;
	}

	/**
	 * 处理老师个人信息页面逻辑
	 * 
	 * @param teacherId
	 * @return Map<String, Object>
	 */
	public Map<String, Object> personalTeaching(long teacherId) {
		Map<String, Object> modelMap = Maps.newHashMap();

		List<Map<String, Object>> certificatedCourses = teacherCertificatedCourseDao
				.findCertificatedCourseNameByTeacherId(teacherId);

		String teacherCertificatedCourseName = certificatedCourses.stream()
				.map((certificatedCourse) -> (String) certificatedCourse.get("courseName"))
				.collect(Collectors.joining("|"));

		modelMap.put("teacherCertificatedCourseName", teacherCertificatedCourseName);

		return modelMap;
	}

	/**
	 * 查询国家代码列表
	 *
	 * @return
	 */
	public List<TeacherNationalityCode> getTeacherNationalityCodes() {
		return teacherNationalityCodeDao.getTeacherNationalityCodes();
	}

	/**
	 * 通过id和code查询国家代码
	 *
	 * @return
	 */
	public TeacherNationalityCode getTeacherNationalityCode(int id, String code) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}
		return teacherNationalityCodeDao.getTeacherNationalityCode(id, code);
	}

	/**
	 * 查询国家列表
	 *
	 * @return
	 */
	public List<TeacherLocation> getCountrys() {
		return teacherLocationDao.getCountrys();
	}

	public TeacherLocation getLocationById(int id) {
		if (id == 0)
			return null;
		return teacherLocationDao.findById(id);
	}

	/**
	 * 通过id查询地址信息
	 *
	 * @return
	 */
	public TeacherAddress getTeacherAddress(int id) {
		return teacherAddressDao.getTeacherAddress(id);
	}

}
