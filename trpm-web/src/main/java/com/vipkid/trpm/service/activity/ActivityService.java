package com.vipkid.trpm.service.activity;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vipkid.http.service.ManageGatewayService;
import com.vipkid.http.vo.ActivityShare;
import com.vipkid.rest.portal.vo.StudentCommentTotalVo;
import org.apache.commons.lang.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.testing.util.SecurityTestUtils;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.mchange.v2.ser.SerializableUtils;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.http.vo.ThirdYearAnniversaryData;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.dao.StudentDao;
import com.vipkid.trpm.dao.TeacherActivityDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Student;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.proxy.redis.RedisClient;
import com.vipkid.trpm.service.portal.TeacherService;
import com.vipkid.trpm.util.AES;

@Service
public class ActivityService {

	private static Logger logger = LoggerFactory.getLogger(ActivityService.class);

	@Autowired
	private TeacherActivityDao teacherActivityDao;

	@Autowired
	private TeacherDao teacherDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private StudentDao studentDao;

	@Autowired
	private RedisProxy redisProxy;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private ManageGatewayService manageGatewayService;

	/**
	 * 查询老师在一年内上了多少节课
	 *
	 * @param id
	 * @param yearmd
	 * @return int
	 * @Author:ALong (ZengWeiLong)
	 * @date 2016年3月18日
	 */
	private int countClassByTeacher(long id, String yearmd) {
		return this.teacherActivityDao.countClassByTeacherId(id, yearmd);
	}

	/**
	 * 查询老师在一年内教了多少学生
	 *
	 * @param id
	 * @param yearmd
	 * @return int
	 * @Author:ALong (ZengWeiLong)
	 * @date 2016年3月18日
	 */
	private int countStudentByTeacherId(long id, String yearmd) {
		List<Map<String, Object>> list = this.teacherActivityDao.countStudentByTeacherId(id, yearmd);
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	private Map<String, String> findMoreClassStudent(long id, String yearmd) {
		Map<String, String> resultMap = Maps.newHashMap();
		List<Map<String, Object>> list = this.teacherActivityDao.countStudentByMax(id, yearmd);
		if (list != null && list.size() > 0) {
			Map<String, Object> map = list.get(0);
			long studentId = (Long) map.get("student_id");
			Student stud = this.studentDao.findById(studentId);
			if (stud != null) {
				resultMap.put("studentName", stud.getEnglishName());
				if ("1".equals(String.valueOf(map.get("counts")))) {
					resultMap.put("moreclass", "1 class");
				} else {
					resultMap.put("moreclass", map.get("counts") + " classes");
				}

				resultMap.put("avatar", "He");
				if (!StringUtils.isEmpty(stud.getAvatar())) {
					if (stud.getAvatar().startsWith("girl")) {
						resultMap.put("avatar", "She");
					}
				}
			}
		}
		return resultMap;
	}

	public Teacher findTeacherById(long id) {
		if (id != 0) {
			return this.teacherDao.findById(id);
		}
		return null;
	}

	public User findUserById(long id) {
		if (id != 0) {
			return this.userDao.findById(id);
		}
		return null;
	}

	/**
	 * ch
	 *
	 * @param model
	 * @param teacher
	 * @return Model
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 * @Author:ALong (ZengWeiLong)
	 * @date 2016年3月18日
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> readInfo(Teacher teacher, String yearmd) throws ClassNotFoundException, IOException {
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("info", "0");//默认读取失败
		try {
			logger.info("从Redis读取老师信息 -- start teacherId:" + teacher.getId());
			byte[] bytes = RedisClient.me().get((ApplicationConstant.REDIS_ACTIVITY_KEY + teacher.getId()).getBytes());
			if (bytes != null) {
				resultMap = (Map<String, Object>) SerializableUtils.fromByteArray(bytes);
				logger.info("从Redis读取老师信息 -- end -- " + teacher.getId());
				//读取成功
				resultMap.put("info", "1");
				return resultMap;
			}
		} catch (Exception e) {
			//读取异常
			logger.error("从Redis读取老师信息异常：" + e.getMessage(), e);
		}
		//继续查询数据库
		User user = this.findUserById(teacher.getId());
		Teacher teacherModul = new Teacher();
		teacherModul.setRealName(user.getName());
		teacherModul.setAvatar(teacher.getAvatar());
		resultMap.put("teacher", teacherModul);

		//如果entryDate 为 null 显示error
		if (teacher.getEntryDate() == null) {
			logger.error("Teacher id = " + teacher.getId() + ",entryDate is null");
			return resultMap;
		}
		long hour = this.countHour(teacher);
		if (hour <= 1) {
			resultMap.put("message", "1 hour");
		} else if (hour <= 24) {
			resultMap.put("message", hour + " hours");
		} else if (hour <= 48) {
			resultMap.put("message", "2 days");
		} else {
			hour = hour / (30 * 24);
			if (hour <= 1) {
				resultMap.put("message", "1 month");
			} else {
				resultMap.put("message", (hour + 1) + " months");
			}
		}

		int countStudent = this.countStudentByTeacherId(teacher.getId(), yearmd);
		//如果countStudent 为 0 显示error
		if (countStudent == 0) {
			return resultMap;
		}
		if (countStudent == 1) {
			resultMap.put("student", countStudent + " Chinese student");
		} else {
			resultMap.put("student", countStudent + " Chinese students");
		}

		int countClass = this.countClassByTeacher(teacher.getId(), yearmd);
		//如果countClass 为 0 显示error
		if (countClass == 0) {
			return resultMap;
		}
		if (countClass == 1) {
			resultMap.put("class", countClass + " class");
		} else {
			resultMap.put("class", countClass + " classes");
		}

		resultMap.put("minutes", (countClass * 30) + " minutes");
		resultMap.putAll(this.findMoreClassStudent(teacher.getId(), yearmd));

		//查询成功 放入redis
		try {
			logger.info("Redis放入老师信息:teacherId:" + teacher.getId());
			RedisClient.me().set((ApplicationConstant.REDIS_ACTIVITY_KEY + teacher.getId()).getBytes(), SerializableUtils.toByteArray(resultMap));
		} catch (Exception e) {
			logger.error("Redis放入老师信息异常：" + e.getMessage(), e);
		}
		resultMap.put("info", "1");
		return resultMap;
	}

	/**
	 * 计算入职时间
	 *
	 * @param user
	 * @return long
	 * @Author:ALong (ZengWeiLong)
	 * @date 2016年4月6日
	 */
	private long countHour(Teacher teacher) {
		long entryDate = teacher.getEntryDate().getTime();
		Calendar calendar = Calendar.getInstance();
		calendar.set(2016, 4, 12, 11, 30, 0);
		long count = (calendar.getTimeInMillis() - entryDate);
		count = count / (3600 * 1000);
		return count;
	}

	/**
	 * 获取某位老师的成功上过课的中国学生数量，去重(目前没用到，getTheMaxStuOfOneTeacher方法有此功能)
	 *
	 * @param teacherId
	 * @return int
	 * @Author:zhangbole
	 * @date 2016年9月21日
	 */
	public int queryStudentCountByTeacherId(long teacherId) {
		if (teacherId <= 0) return 0;
		return teacherActivityDao.queryStudentCountByTeacherId(teacherId);
	}

	/**
	 * 获取某位老师的
	 * 1,成功上过他最多课的中国学生的名字,头像与数量，键分别为“name”，“avatar”，“count”
	 * 2,上过他的课的学生数，去重，键为“stuNum”
	 *
	 * @param teacherId
	 * @return Map
	 * @Author:zhangbole
	 * @date 2016年9月21日
	 */
	public Map<String, Object> getTheMaxStuOfOneTeacher(long teacherId) {
		if (teacherId <= 0) {
			return null;
		}
		List<Map> stuInfoAndClassNumList = teacherActivityDao.getStudentListOfOneTeacher(teacherId);//按ClassNum递减排序的map的list
		if (stuInfoAndClassNumList == null || stuInfoAndClassNumList.isEmpty()) {
			return null;
		}
		Map stuInfoAndClassNum = stuInfoAndClassNumList.get(0);
		if (stuInfoAndClassNum == null) {
			return null;
		}
		Object name = stuInfoAndClassNum.get("english_name");//index为0的位置，上课最多的学生英文名称
		Object maxCount = stuInfoAndClassNum.get("num");//上课最多的学生上课数量
		Object avatar = stuInfoAndClassNum.get("avatar");//上课最多的学生头像
		Object id = stuInfoAndClassNum.get("id");//上课最多的学生id
		String gender = "MALE";
		User u = userDao.findById((Long) id);
		if (u != null) {
			String g = u.getGender();
			if (StringUtils.isNotEmpty(g)) {
				gender = g;
			}
		}
		int difStuNum = stuInfoAndClassNumList.size();
		Map<String, Object> result = Maps.newHashMap();
		Integer stuNum = Integer.valueOf(difStuNum);
		result.put("name", name);
		result.put("avatar", avatar);
		result.put("count", maxCount);
		result.put("stuNum", stuNum);
		result.put("gender", gender);
		return result;
	}

	/**
	 * 获取某位老师的总上课节数
	 *
	 * @param teacherId
	 * @return int
	 * @Author:zhangbole
	 * @date 2016年9月21日
	 */
	public int getClassNumOfOneTeacher(long teacherId) {
		if (teacherId <= 0) return 0;
		return teacherActivityDao.getClassNumOfOneTeacher(teacherId);
	}

	/**
	 * 通过teacherId获取教师成为正式教师的具备上课资格的天数，用于三周年庆教师数据活动页
	 *
	 * @param id
	 * @return long
	 * @date 2016年9月21日
	 */
	public long getDaysSinceBeRegularTeacher(Long id) {
		Date beRegularTeacherDate = getDateBeRegularTeacher(id);
		if (beRegularTeacherDate == null) {
			return 0;
		}
		Calendar cal = Calendar.getInstance();
		Date nowDate = cal.getTime();
		long days = (nowDate.getTime() - beRegularTeacherDate.getTime()) / (1000 * 60 * 60 * 24);
		return days;
	}

	/**
	 * 通过teacherId获取教师成为正式教师的具备上课资格的日期，用于三周年庆教师数据活动页
	 *
	 * @param id
	 * @return Date
	 * @date 2016年10月13日
	 */
	public Date getDateBeRegularTeacher(Long id) {
		if (id == null) return null;
		Teacher teacher = teacherDao.findById(id);
		if (teacher == null) return null;
		Date beRegularTeacherDate = teacher.getEntryDate();
		if (beRegularTeacherDate == null) {
			return null;
		}
		return beRegularTeacherDate;
	}

	/**
	 * 通过teacherId获取此教师推荐的老师数量，用于三周年庆教师数据活动页
	 *
	 * @param id
	 * @return int
	 * @date 2016年9月21日
	 */
	public int getNumOfReferalsByTeacherId(Long id) {
		if (id == null) return 0;
		return teacherActivityDao.getNumOfReferalsByTeacherId(id);
	}

	/**
	 * 通过teacherId获取此教师第一次上课的日期，用于三周年庆教师数据活动页
	 * (需求更改，已不用)
	 *
	 * @param id
	 * @return String
	 * @Author:zhangbole
	 * @date 2016年9月27日
	 */
	public String getFirstClassDateofOneTeacher(Long id) {
		if (id == null) return null;
		Timestamp time = teacherActivityDao.getFirstClassDateofOneTeacher(id);
		if (time == null) return null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(time);
	}

	/**
	 * 通过teacherId获取此教师推荐的老师的头像，用于三周年庆教师数据活动页
	 *
	 * @param id
	 * @return List<String>
	 * @Author:zhangbole
	 * @date 2016年9月27日
	 */
	public List<String> getAvatarListOfReferalsByTeacherId(Long id) {
		if (id == null) return null;
		return teacherActivityDao.getAvatarListOfReferalsByTeacherId(id);
	}


	/**
	 * 通过teacherId获取此教师的活动页的所有数据，用于三周年庆教师数据活动页
	 *
	 * @param teacherId
	 * @return ThirdYearAnniversaryData
	 * @Author:zhangbole
	 * @date 2016年9月23日
	 */
	public ThirdYearAnniversaryData getThirdYearAnniversaryData(long teacherId) {
		//优先缓存取数据
		String redisKey = "get_third_year_anniversary_data-" + teacherId;
		String value = redisProxy.get(redisKey);
		if (StringUtils.isNotEmpty(value)) {
			logger.info("getThirdYearAnniversaryData(), teacherId={}, json={} ,直接从redis中获取", teacherId, value);
			return JsonUtils.toBean(value, ThirdYearAnniversaryData.class);
		}
		//如果缓存没有
		ThirdYearAnniversaryData data = new ThirdYearAnniversaryData();
		if (teacherId <= 0) return null;
		long days = this.getDaysSinceBeRegularTeacher(teacherId);
		if (days < 0) {//如果这个id不是一个正式老师的id，直接返回null
			return null;
		}
		data.setLengthOfTime(days);
		Teacher t = teacherService.get(teacherId);
		String teacherAvatar = t.getAvatar();
		data.setTeacherAvatar(buildCompleteAvatarUrl(teacherAvatar));
		User u = userDao.findById(teacherId);
		data.setTeacherName(u.getName());
		data.setTeacherGender(u.getGender());
		data.setNumberOfReferrals(this.getNumOfReferalsByTeacherId(teacherId));
		int totalFinishedClasses = this.getClassNumOfOneTeacher(teacherId);
		data.setTotalFinishedClasses(totalFinishedClasses);
		data.setTotalFinishedClassesMin(totalFinishedClasses * 30);
		Map<String, Object> map = this.getTheMaxStuOfOneTeacher(teacherId);
		if (map != null) {
			data.setNumberOfClasses((long) map.get("count"));
			data.setStuNumber((int) map.get("stuNum"));
			data.setStuName((String) map.get("name"));
			data.setStuAvatar((String) map.get("avatar"));
			data.setStuGender((String) map.get("gender"));
		}
		Date dateBeRegularTeacher = getDateBeRegularTeacher(teacherId);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		data.setBecomeRegularDate(df.format(dateBeRegularTeacher));//变更为成为正式老师的时间
		List<String> avatarListOrigin = getAvatarListOfReferalsByTeacherId(teacherId);
		List<String> avatarList = Lists.newArrayList();
		for (String eachAvatar : avatarListOrigin) {
			avatarList.add(buildCompleteAvatarUrl(eachAvatar));
		}
		data.setReferralsAvatarList(avatarList);
		String token = encode(teacherId);
		data.setToken(token);
		String joinUsUrl = PropertyConfigurer.stringValue("third_year_anniversary_join_us_url");
		if (StringUtils.isNotEmpty(joinUsUrl) && joinUsUrl.contains("%d")) {
			joinUsUrl = String.format(joinUsUrl, teacherId);
		} else {
			logger.error("配置文件中的third_year_anniversary_join_us_url参数值错误");
			joinUsUrl = PropertyConfigurer.stringValue("teacher.www");
			if (StringUtils.isEmpty(joinUsUrl)) {
				joinUsUrl = "https://t.vipkid.com.cn/";
			}
		}

		data.setJoinUsUrl(joinUsUrl);
		//data加入缓存
		int expireSecond = 600;//缓存600秒
		String redisValue = JsonUtils.toJSONString(data);
		redisProxy.set(redisKey, redisValue, expireSecond);
		logger.info("getThirdYearAnniversaryData(), teacherId={}, json={} ,查询数据库获取数据，并存入redis", teacherId, redisValue);
		return data;
	}

	private String buildCompleteAvatarUrl(String urlOrigin) {
		if (StringUtils.isEmpty(urlOrigin)) return urlOrigin;//如果参数非法，就不加前缀
		String urlPreffix = PropertyConfigurer.stringValue("oss.url_preffix");
		if (StringUtils.isEmpty(urlPreffix)) return urlOrigin;//如果配置文件没有这个属性，就不加前缀
		if (urlOrigin.startsWith("/")) {    //数据库里的avatar字段格式不统一，有的不是“/”打头
			return urlPreffix + urlOrigin;
		} else {
			return urlPreffix + "/" + urlOrigin;
		}
	}

	/**
	 * 当前时间是不是在三周年活动期间
	 *
	 * @return boolean
	 * @Author:zhangbole
	 * @date 2016年9月28日
	 */
	public boolean isDuringThirdYeayAnniversary() {//活动页与对应接口的开关
		boolean ret = false;
		String masterSwitch = PropertyConfigurer.stringValue("third_year_anniversary_switch");
		if (StringUtils.isEmpty(masterSwitch) || !masterSwitch.equals("on")) {//总开关没开，视为不在活动期间
			logger.info("isDuringThirdYeayAnniversary(), 不--在--三周年活动期间。原因：总开关未开");
			return false;
		}
		String strStart = PropertyConfigurer.stringValue("third_year_anniversary_start");
		String strEnd = PropertyConfigurer.stringValue("third_year_anniversary_end");
		if (StringUtils.isEmpty(strEnd) || StringUtils.isEmpty(strStart)) {//如果配置文件中的两个属性有一个消失，就return false
			logger.warn("isDuringThirdYeayAnniversary(), 返回false。原因：配置文件中third_year_anniversary_start或third_year_anniversary_end没有值");
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date start = null;
		Date end = null;
		try {
			start = sdf.parse(strStart);
			end = sdf.parse(strEnd);
		} catch (ParseException e) {
			logger.warn("配置文件中third_year_anniversary_start或third_year_anniversary_end格式错误，应为yyyy-MM-dd HH:mm:ss");
			return false;
		}
		Date now = new Date();

		if (now.after(start) && now.before(end)) {
			ret = true;
			logger.info("isDuringThirdYeayAnniversary(), 在--三周年庆的活动时间");
		} else {
			logger.info("isDuringThirdYeayAnniversary(), 不--在--三周年庆的活动时间");
		}
		return ret;
	}

	/**
	 * teachenId加密，用于facebook分享链接
	 *
	 * @return boolean
	 * @Author:zhangbole
	 * @date 2016年10月8日
	 */
	private String encode(long teacherId) {
		return AES.encrypt(String.valueOf(teacherId), AES.getKey(AES.KEY_LENGTH_128, ApplicationConstant.AES_128_KEY));
	}

	/**
	 * teachenId解密，用于facebook分享链接
	 *
	 * @return boolean
	 * @Author:zhangbole
	 * @date 2016年10月8日
	 */
	public long decode(String token) {
		String teacherId = null;
		try {
			teacherId = AES.decrypt(token, AES.getKey(AES.KEY_LENGTH_128, ApplicationConstant.AES_128_KEY));
		} catch (Exception e) {
			logger.warn(token + " 是一个非法的加密字符串，无法解密");
			return -1;
		}

		if (teacherId.matches("[1-9]\\d+")) {
			return Long.parseLong(teacherId);
		}
		return -1;
	}

	/**
	 * 通过teacherId获取此教师的活动分享数据
	 *
	 * @param teacherId
	 * @return ActivityShare
	 * @Author:yangchao
	 * @date 2017年2月28日
	 */
	public ActivityShare getActivityShareData(Long teacherId) {
		String redisKey = "get_activity_share_data_20170228-" + teacherId;
		String value = redisProxy.get(redisKey);
		if (StringUtils.isNotEmpty(value)) {
			logger.info("getActivityShareData(), teacherId={}, json={} ,直接从redis中获取", teacherId, value);
			return JsonUtils.toBean(value, ActivityShare.class);
		}
		//如果缓存没有
		ActivityShare data = new ActivityShare();
		if (teacherId <= 0) return null;
		User u = userDao.findById(teacherId);
		data.setTeacherName(u.getName());
		int totalFinishedClasses = this.getClassNumOfOneTeacher(teacherId);
		data.setTotalFinishedClassesMin(totalFinishedClasses * 30);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = Calendar.getInstance().getTime();
		String yearmd = df.format(date);
		List<Map<String, Object>> ret = teacherActivityDao.countStudentByTeacherId(teacherId, yearmd);
		data.setStudentNum(ret.size());
		String token = encode(teacherId);
		data.setToken(token);
		//计算老师的综合评价分数
		StudentCommentTotalVo scores = manageGatewayService.getStudentCommentTotalByTeacherId(teacherId.intValue());
		Integer allComments = scores.getRating_1_count() +
				scores.getRating_2_count() +
				scores.getRating_3_count() +
				scores.getRating_4_count() +
				scores.getRating_5_count();
		String totalGradeStr = "0.0";
		if (allComments != 0) {
			Float totalGrade = (1f * scores.getRating_1_count() +
					2f * scores.getRating_2_count() +
					3f * scores.getRating_3_count() +
					4f * scores.getRating_4_count() +
					5f * scores.getRating_5_count()) / allComments;
			totalGradeStr = new DecimalFormat("0.0").format(totalGrade);
		}
		data.setRatings(totalGradeStr);

//		暂留，考虑后续是否管理链接
// 		String joinUsUrl = PropertyConfigurer.stringValue("third_year_anniversary_join_us_url");
//		if(StringUtils.isNotEmpty(joinUsUrl) && joinUsUrl.contains("%d")){
//			joinUsUrl = String.format(joinUsUrl, teacherId);
//		}
		//生成joinUs链接
		String joinUsUrl = PropertyConfigurer.stringValue("third_year_anniversary_join_us_url");
		if (StringUtils.isNotEmpty(joinUsUrl) && joinUsUrl.contains("%d")) {
			joinUsUrl = String.format(joinUsUrl, teacherId);
		} else {
			logger.error("配置文件中的third_year_anniversary_join_us_url参数值错误");
			joinUsUrl = PropertyConfigurer.stringValue("teacher.www");
			if (StringUtils.isEmpty(joinUsUrl)) {
				joinUsUrl = "https://t.vipkid.com.cn/";
			}
		}
		data.setJoinUsUrl(joinUsUrl);
		int expireSecond = 600;//缓存600秒
		String redisValue = JsonUtils.toJSONString(data);
		redisProxy.set(redisKey, redisValue, expireSecond);
		logger.info("getThirdYearAnniversaryData(), teacherId={}, json={} ,查询数据库获取数据，并存入redis", teacherId, redisValue);
		return data;
	}
}