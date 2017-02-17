package com.vipkid.trpm.service.portal;

import static com.vipkid.trpm.util.DateUtils.FMT_YMD;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.druid.util.StringUtils;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.vipkid.file.model.FileUploadStatus;
import com.vipkid.file.model.FileVo;
import com.vipkid.file.service.AwsFileService;
import com.vipkid.file.utils.Encodes;
import com.vipkid.file.utils.FileUtils;
import com.vipkid.http.service.FileHttpService;
import com.vipkid.trpm.constant.ApplicationConstant.MediaType;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.*;
import com.vipkid.trpm.entity.media.UploadResult;
import com.vipkid.trpm.entity.personal.BasicInfo;
import com.vipkid.trpm.entity.personal.TeacherBankVO;
import com.vipkid.trpm.service.media.AbstarctMediaService;
import com.vipkid.trpm.service.media.OSSMediaService;
import com.vipkid.trpm.util.AwsFileUtils;

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

	@Autowired
    private AwsFileService awsFileService;

    @Autowired
    private FileHttpService fileHttpService;

    
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
	 * @param teacher
	 * @param bankInfo
	 */
	public Map<String, Object> doSetBankInfo(Teacher teacher, TeacherBankVO bankInfo) {
		Map<String, Object> modelMap = Maps.newHashMap();
		modelMap.put("action", true);

		long teacherId = teacher.getId();
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
		String bankAccountName = bankInfo.getBeneficiaryAccountName();
		String bankSwiftCode = bankInfo.getSwiftCode();
		String bankABARoutingNumber = bankInfo.getBankABARoutingNumber();
		String bankACHNumber = bankInfo.getBankACHNumber();
		String identityNumber = bankInfo.getIdNumber();
		StringBuffer stringBuffer = new StringBuffer();
		String bankName = bankInfo.getBeneficiaryBankName();{
			if (!StringUtils.equals(bankName,teacher.getBankName())){
				stringBuffer.append("更新bankName:from " + teacher.getBankName() + "to " + bankName);
			}
		}
		teacher.setBankName(bankName);
		if (bankAccountName.indexOf("*")!=-1){
			if (!StringUtils.equals(bankAccountName,teacher.getBankAccountName())){
				stringBuffer.append("更新bankName:from " + hideNameInfo(teacher.getBankAccountName()) + "to " + hideNameInfo(bankAccountName));
			}
			teacher.setBankAccountName(bankAccountName);
		}
		String bankCardNumber = bankInfo.getBeneficiaryAccountNumber();
		if (!StringUtils.equals(bankCardNumber,teacher.getBankCardNumber())){
			stringBuffer.append("更新bankName:from " + teacher.getBankCardNumber() + "to " + bankCardNumber);
		}
		teacher.setBankCardNumber(bankInfo.getBeneficiaryAccountNumber());

		if (bankSwiftCode.indexOf("*")!=-1){
			String swiftCode = teacher.getBankSwiftCode();
			if (!StringUtils.equals(bankSwiftCode,swiftCode)){
				stringBuffer.append("更新bankName:from " + hideInfo(swiftCode,0,swiftCode.length()-2)+ "to " + hideInfo(bankSwiftCode,0,bankSwiftCode.length()-2));
			}
			teacher.setBankSwiftCode(bankSwiftCode);
		}
		if(bankABARoutingNumber.indexOf("*")!=-1){
			String ABARoutingNumber = teacher.getBankABARoutingNumber();
			if (!StringUtils.equals(bankABARoutingNumber,ABARoutingNumber)){
				stringBuffer.append("更新bankName:from " + hideInfo(ABARoutingNumber,0,ABARoutingNumber.length()-4) + "to " + hideInfo(bankABARoutingNumber,0,bankABARoutingNumber.length()-4));
			}
			teacher.setBankABARoutingNumber(bankABARoutingNumber);
		}
		if (bankACHNumber.indexOf("*")!=-1){
			String ACHNumber = teacher.getBankACHNumber();
			if (!StringUtils.equals(bankACHNumber,ACHNumber)){
				stringBuffer.append("更新bankName:from " + hideInfo(ACHNumber,0,ACHNumber.length()-4) + "to " + hideInfo(bankACHNumber,0,bankACHNumber.length()-4));
			}
			teacher.setBankACHNumber(bankACHNumber);
		}
		String identityType = bankInfo.getIdType().toString();
		teacher.setIdentityType(bankInfo.getIdType());
		if (!StringUtils.equals(identityType,String.valueOf(teacher.getIdentityType()))){
			stringBuffer.append("更新bankName:from " + teacher.getIdentityType() + "to " + identityType);
		}
		if (identityNumber.indexOf("*")!=-1){
			String IdNumber = teacher.getIdentityNumber();
			if (!StringUtils.equals(identityNumber,IdNumber)){
				stringBuffer.append("更新bankName:from " + hideInfo(IdNumber,1,IdNumber.length()) + "to " + hideInfo(identityNumber,1,identityNumber.length()));
			}
			teacher.setIdentityNumber(identityNumber);
		}
		String passPort = bankInfo.getPassportURL();
		if (!StringUtils.equals(passPort,teacher.getPassport())){
			stringBuffer.append("更新bankName:from " + teacher.getPassport() + "to " + passPort);
		}
		teacher.setPassport(bankInfo.getPassportURL());

		String issuanceCountry = bankInfo.getIssuanceCountryId().toString();
		if (!StringUtils.equals(issuanceCountry,String.valueOf(teacher.getIssuanceCountry()))){
			stringBuffer.append("更新bankName:from " + teacher.getIssuanceCountry() + "to " + issuanceCountry);
		}
		teacher.setIssuanceCountry(bankInfo.getIssuanceCountryId());

		teacherDao.update(teacher);
		modelMap.put("teacher", teacher);
		return modelMap;
	}

	/**
	 * 处理基本信息更新逻辑
	 *
	 * @param teacher
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
	
	public Map<String, Object> doUploadAvatarImage(MultipartFile file, Long teacherId) {
		
		Map<String, Object> modelMap = Maps.newHashMap();

		logger.info("upload file for filename = {}", file.getOriginalFilename());

		String fileName = file.getOriginalFilename();
        String bucketName = AwsFileUtils.getAwsBucketName();
        String key = AwsFileUtils.getAvatarKey(fileName);
        Long fileSize = file.getSize();

        Preconditions.checkArgument(AwsFileUtils.checkAvatarFileType(fileName), "文件类型不正确，支持类型为" + AwsFileUtils.AVATAR_FILE_TYPE);
        Preconditions.checkArgument(AwsFileUtils.checkAvatarFileSize(fileSize), "文件太大，maxSize = " + AwsFileUtils.AVATAR_MAX_SIZE);
		
        UploadResult uploadResult = new UploadResult();
        boolean succeed = false;
		try {
            FileVo fileVo = awsFileService.upload(bucketName, key, file.getInputStream(), fileSize);
            if (fileVo != null) {
                FileUploadStatus fileUploadStatus = fileHttpService.uploadAvatar(teacherId, key);
                String url = fileUploadStatus.getUrl();
                String encodeUrl = FileUtils.EncodeURLFileName(url);
                
                modelMap.put("teacher", teacherDao.findById(teacherId));
                modelMap.put("result", true);
                modelMap.put("url", url);
				uploadResult.setEncodeUrl(encodeUrl );
                succeed = true;
            } else {
                logger.error("Failed to upload avatar!");
                modelMap.put("result", false);
            }
        } catch (Exception e) {
        	logger.error("upload avatar Exception", e);
        	modelMap.put("result", false);
        }
		uploadResult.setResult(succeed);
        modelMap.put("uploadResult", uploadResult);
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
		List<TeacherLocation> result = teacherLocationDao.getCountrys();
		if (CollectionUtils.isNotEmpty(result)) {
			StringBuffer sb = new StringBuffer();
			for (TeacherLocation one : result) {
				sb.append("countryId=");
				sb.append(one.getId());
				sb.append(",");
				sb.append("name=");
				sb.append(one.getName());
				sb.append(";");
			}
			logger.info("teacherLocationDao#getCountrys:" + sb.toString());
		} else {
			logger.info("teacherLocationDao#getCountrys: none");
		}
		return result;
		//return teacherLocationDao.getCountrys();
	}

	public TeacherLocation getLocationById(Integer id) {
		if (null== id||0==id)
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

	/**
	 * 隐藏BankInfo的部分信息
	 * @param source
	 * @param start 开始隐藏信息的位置
	 * @param end 结束隐藏信息的位置
	 * @return
	 */

	public static String hideInfo(String source ,int start ,int end) {
		if (StringUtils.isEmpty(source)) {
			return source;
		}

		int len = source.length();
		if (start < 0 || start > len || end > len || end < 0 || start > end) {
			logger.warn("invalid params: start={}, end={}", start, end);
			return source;
		}

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(source.substring(0, start));

		for (int i = start; i < end; i++) {
			stringBuffer.append("*");
		}

		stringBuffer.append(source.substring(end, len));
		return stringBuffer.toString();
	}

	public String hideNameInfo(String source){
		if (source == null || source.length()<=0){
			logger.warn("invalid params:source={}", source);
			return source;
		}else {
            source = source.trim();
			int n = source.lastIndexOf(" ");
			StringBuffer sb = new StringBuffer();
			String name;
			if (n > -1) {
				for (int i = 0; i < n; i++) {
					sb.append("*");
				}
				String na = source.substring(n+1, source.length());
				sb.append(na);
				name = sb.toString();
			} else {
				name = source;
			}
			return name;
		}

	}

}
