package com.vipkid.trpm.service.activity;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.mchange.v2.ser.SerializableUtils;
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
    /**
     * 查询老师在一年内上了多少节课
     * @Author:ALong (ZengWeiLong)
     * @param id
     * @param yearmd
     * @return int
     * @date 2016年3月18日
     */
    private int countClassByTeacher(long id,String yearmd){
        return this.teacherActivityDao.countClassByTeacherId(id, yearmd);
    }
    
    /**
     * 查询老师在一年内教了多少学生
     * @Author:ALong (ZengWeiLong)
     * @param id
     * @param yearmd
     * @return    
     * int
     * @date 2016年3月18日
     */
    private int countStudentByTeacherId(long id,String yearmd){
        List<Map<String, Object>> list = this.teacherActivityDao.countStudentByTeacherId(id, yearmd);
        if(list != null){
            return list.size();
        }
        return 0;
    }
    
    private Map<String,String> findMoreClassStudent(long id,String yearmd){
        Map<String,String> resultMap = Maps.newHashMap();
        List<Map<String, Object>> list = this.teacherActivityDao.countStudentByMax(id,yearmd);
        if(list != null && list.size() > 0){
            Map<String, Object> map = list.get(0);
            long studentId = (Long)map.get("student_id");
            Student stud = this.studentDao.findById(studentId);
            if(stud != null){
                resultMap.put("studentName",stud.getEnglishName());
                if("1".equals(String.valueOf(map.get("counts")))){
                    resultMap.put("moreclass","1 class");
                }else{
                    resultMap.put("moreclass",map.get("counts")+" classes");
                }
                
                resultMap.put("avatar","He");
                if(!StringUtils.isEmpty(stud.getAvatar())){
                    if(stud.getAvatar().startsWith("girl")){
                        resultMap.put("avatar","She");
                    }
                }
            }
        }
        return resultMap;
    }
            
    public Teacher findTeacherById(long id){
        if( id != 0){
            return this.teacherDao.findById(id);
        }
        return null;
    }
    
    public User findUserById(long id){
        if( id != 0){
            return this.userDao.findById(id);
        }
        return null;
    }
    /**
     * ch 
     * @Author:ALong (ZengWeiLong)
     * @param model
     * @param teacher
     * @return    
     * Model
     * @throws java.io.IOException
     * @throws ClassNotFoundException 
     * @date 2016年3月18日
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> readInfo(Teacher teacher,String yearmd) throws ClassNotFoundException, IOException{
        Map<String,Object> resultMap = Maps.newHashMap();
        resultMap.put("info","0");//默认读取失败
        try{
            logger.info("从Redis读取老师信息 -- start teacherId:" + teacher.getId());
            byte[] bytes = RedisClient.me().get((ApplicationConstant.REDIS_ACTIVITY_KEY+teacher.getId()).getBytes());
            if(bytes != null){
                resultMap = (Map<String, Object>) SerializableUtils.fromByteArray(bytes);
                logger.info("从Redis读取老师信息 -- end -- "+teacher.getId());
                //读取成功
                resultMap.put("info","1");
                return resultMap;
            }
        }catch(Exception e){
            //读取异常
            logger.error("从Redis读取老师信息异常："+e.getMessage(),e);
        }
        //继续查询数据库
        User user = this.findUserById(teacher.getId());
        Teacher teacherModul = new Teacher();
        teacherModul.setRealName(user.getName());
        teacherModul.setAvatar(teacher.getAvatar());
        resultMap.put("teacher", teacherModul);
        
        //如果entryDate 为 null 显示error
        if(teacher.getEntryDate() == null){
            logger.error("Teacher id = " + teacher.getId() + ",entryDate is null");
            return resultMap;
        }
        long hour = this.countHour(teacher);
        if(hour <= 1){
            resultMap.put("message","1 hour");
        }else if(hour <= 24){
            resultMap.put("message",hour + " hours");
        }else if(hour <= 48){
            resultMap.put("message","2 days");
        }else{
            hour = hour/(30*24);
            if(hour <= 1){
                resultMap.put("message","1 month"); 
            }else{
                resultMap.put("message", (hour+1) + " months");
            }
        }
        
        int countStudent = this.countStudentByTeacherId(teacher.getId(), yearmd);
        //如果countStudent 为 0 显示error
        if(countStudent == 0){
            return resultMap;
        }
        if(countStudent == 1){
            resultMap.put("student",countStudent+" Chinese student");
        }else{
            resultMap.put("student",countStudent+" Chinese students");
        }
        
        int countClass = this.countClassByTeacher(teacher.getId(),yearmd);
        //如果countClass 为 0 显示error
        if(countClass == 0){
            return resultMap;
        }
        if(countClass == 1){
            resultMap.put("class", countClass + " class");
        }else{
            resultMap.put("class", countClass+ " classes");
        }
        
        resultMap.put("minutes",(countClass * 30) + " minutes");
        resultMap.putAll(this.findMoreClassStudent(teacher.getId(), yearmd));
        
        //查询成功 放入redis
        try{
            logger.info("Redis放入老师信息:teacherId:"+teacher.getId());
            RedisClient.me().set((ApplicationConstant.REDIS_ACTIVITY_KEY+teacher.getId()).getBytes(), SerializableUtils.toByteArray(resultMap));
        }catch(Exception e){
            logger.error("Redis放入老师信息异常："+e.getMessage(),e);
        }
        resultMap.put("info","1");
        return resultMap;
    } 
    
    /**
     * 计算入职时间 
     * @Author:ALong (ZengWeiLong)
     * @param user
     * @return    
     * long
     * @date 2016年4月6日
     */
    private long countHour(Teacher teacher){
        long entryDate = teacher.getEntryDate().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016,4,12,11,30,0);
        long count = (calendar.getTimeInMillis() - entryDate);
        count = count/(3600*1000);
        return count;
    }
    
    /**获取某位老师的成功上过课的中国学生数量，去重(目前没用到，getTheMaxStuOfOneTeacher方法有此功能)
    *
    * @Author:zhangbole
    * @param teacherId
    * @return int
    * @date 2016年9月21日
    */
   public int countStuNumOfOneTeacher(long teacherId){
   	if(teacherId<=0) return 0;
   	return teacherActivityDao.countStuNumOfOneTeacher(teacherId);
   }
    
    /**获取某位老师的
     *1,成功上过他最多课的中国学生的名字,头像与数量，键分别为“name”，“avatar”，“count”
     *2,上过他的课的学生数，去重，键为“stuNum”
     * @Author:zhangbole
     * @param teacherId
     * @return Map
     * @date 2016年9月21日
     */
     public Map<String, Object> getTheMaxStuOfOneTeacher(long teacherId){
     	if(teacherId<=0) return null;
     	List<Map> stuIdAndClassNumList = teacherActivityDao.getStudentListOfOneTeacher(teacherId);//按ClassNum递减排序的map的list
     	if(stuIdAndClassNumList==null || stuIdAndClassNumList.isEmpty()) return null;
     	Object name =  stuIdAndClassNumList.get(0).get("english_name");//index为0的位置，上课最多的学生英文名称
     	Object maxCount =  stuIdAndClassNumList.get(0).get("num");//上课最多的学生上课数量
     	Object avatar =  stuIdAndClassNumList.get(0).get("avatar");//上课最多的学生上课数量
     	int difStuNum = stuIdAndClassNumList.size();
     	Map<String, Object> result = Maps.newHashMap();
     	Integer stuNum = Integer.valueOf(difStuNum);
     	result.put("name", name);
     	result.put("avatar", avatar);
     	result.put("count", maxCount);
     	result.put("stuNum", stuNum);
     	return result;
     }
     
     /**获取某位老师的总上课节数
      * @Author:zhangbole
      * @param teacherId
      * @return int
      * @date 2016年9月21日
      */
     public int getClassNumOfOneTeacher(long teacherId){
     	if(teacherId<=0)  return 0;
     	return teacherActivityDao.getClassNumOfOneTeacher(teacherId);
     }
     
     /**
      * 通过teacherId获取教师成为正式教师的具备上课资格的天数，用于三周年庆教师数据活动页
      * @param id
      * @return long
      * @date 2016年9月21日
      */
 	public long getDaysSinceBeRegularTeacher(Long id){
 		if(id == null) return -1;
 		Teacher teacher = teacherDao.findById(id);
 		if(teacher==null) return -1;
 		Date beRegularTeacherDate = teacher.getEntryDate();
 		if(beRegularTeacherDate==null) return -1;
 		Calendar cal = Calendar.getInstance();
 		Date nowDate = cal.getTime();
 		long days = (nowDate.getTime() - beRegularTeacherDate.getTime()) / (1000 * 60 * 60 * 24);
 		return days;
 	}
 	
 	/**
      * 通过teacherId获取此教师推荐的老师数量，用于三周年庆教师数据活动页
      * @param id
      * @return int
      * @date 2016年9月21日
      */
 	public int getNumOfTeachersByReferee(Long id){
 		if(id==null) return 0;
 		return teacherActivityDao.getNumOfTeachersByReferee(id);
 	}
 	
 	/**
 	 * 通过teacherId获取此教师第一次上课的日期，用于三周年庆教师数据活动页
     * @Author:zhangbole
     * @param id
     * @return  String
     * @date 2016年9月27日
     */
 	public String getFirstClassDateofOneTeacher(Long id){
 		if(id==null) return null;
 		Timestamp time = teacherActivityDao.getFirstClassDateofOneTeacher(id);
 		if(time==null) return null;
 		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
 		return df.format(time);
 	}
 	
 	/**
 	 * 通过teacherId获取此教师推荐的老师的头像，用于三周年庆教师数据活动页
     * @Author:zhangbole
     * @param id
     * @return  List<String>
     * @date 2016年9月27日
     */
 	public List<String> getAvatarListOfTeachersByReferee(Long id){
 		if(id == null) return null;
 		return teacherActivityDao.getAvatarListOfTeachersByReferee(id);
 	}
 	
 	
 	/**
 	 * 通过teacherId获取此教师的活动页的所有数据，用于三周年庆教师数据活动页
     * @Author:zhangbole
     * @param teacherId
     * @return  ThirdYearAnniversaryData
     * @date 2016年9月23日
     */
 	public ThirdYearAnniversaryData getThirdYearAnniversaryData(long teacherId){
		ThirdYearAnniversaryData data = new ThirdYearAnniversaryData();
		if(teacherId<=0) return  null;
		long days = this.getDaysSinceBeRegularTeacher(teacherId);
		if(days < 0){//如果这个id不是一个正式老师的id，直接返回null
			return null;
		}
		data.setLengthOfTime(days);
		Teacher t = teacherService.get(teacherId);
		data.setTeacher(handleTeacherInfo(t));
		User u = userDao.findById(teacherId);
		data.setUser(hideUserInfo(u));
		data.setNumberOfReferrals(this.getNumOfTeachersByReferee(teacherId));
		int totalFinishedClasses = this.getClassNumOfOneTeacher(teacherId);
		data.setTotalFinishedClasses(totalFinishedClasses);
		data.setTotalFinishedClassesMin(totalFinishedClasses*30);
		Map<String, Object> map = this.getTheMaxStuOfOneTeacher(teacherId);
		if(map!=null){
			data.setNumberOfClasses((long)map.get("count"));
			data.setStuNumber((int)map.get("stuNum"));
			data.setStuName((String)map.get("name"));
			data.setStuAvatar((String)map.get("avatar"));
		}
		data.setFirstClassDate(getFirstClassDateofOneTeacher(teacherId));
		List<String> avatarListOrigin = getAvatarListOfTeachersByReferee(teacherId);
		List<String> avatarList = Lists.newArrayList();
		for(String eachAvatar : avatarListOrigin){
			avatarList.add(completeResouceUrl(eachAvatar));
		}
		data.setReferralsAvatarList(avatarList);
		String token = encode(teacherId);
		data.setToken(token);
		return data;
 	}
 	
 	private String completeResouceUrl(String urlOrigin){
 		if(StringUtils.isEmpty(urlOrigin)) return urlOrigin;//如果参数非法，就不加前缀
		String urlPreffix = PropertyConfigurer.stringValue("oss.url_preffix");
		if(StringUtils.isEmpty(urlPreffix)) return urlOrigin;//如果配置文件没有这个属性，就不加前缀
 		if(urlOrigin.startsWith("/")){    //数据库里的avatar字段格式不统一，有的不是“/”打头
			return urlPreffix+urlOrigin;
		}
		else{
			return urlPreffix+"/"+urlOrigin;
		}
 	}
 	
 	/**
 	 * 隐藏部分老师用不到的属性,同时把头像链接补全
     * @Author:zhangbole
     * @param teacher
     * @return  Teacher
     * @date 2016年10月8日
     */
 	private Teacher handleTeacherInfo(Teacher teacher){
 		Teacher ret = new Teacher();
 		ret.setRealName(teacher.getRealName());
 		ret.setAvatar(teacher.getAvatar());
 		return ret;
 	}
 	/**
 	 * 隐藏部分老师用不到的属性
     * @Author:zhangbole
     * @param user
     * @return  User
     * @date 2016年10月8日
     */
 	private User hideUserInfo(User user){
 		User ret = new User();
 		ret.setName(user.getName());
 		ret.setGender(user.getGender());
 		return ret;
 	}
 	
 	/**
 	 * 查看一个老师是不是第一次登陆(如不在活动期间，返回false)
     * @Author:zhangbole
     * @param teacherId
     * @return  boolean
     * @date 2016年9月28日
     */
 	public boolean isFirstTimeSignInDuringThirdYearAnniversary(long teacherId){
 		boolean ret = false;
 		if(!isDuringThirdYeayAnniversary()){//不在活动期间，返回false
 			return ret;
 		}
 		String rediskey = "zhangbole"+teacherId;//key值应该怎么规范？
 		String redisValue = "hava signed in";
 		String sign = redisProxy.get(rediskey);
 		if(StringUtils.isNotEmpty(sign)&&equals(redisValue)){
 		}
 		else{
 			redisProxy.set(rediskey, redisValue);//set进redis后会保存多久？
 			ret = true;
 		}
 		
 		
 		return ret;
 	}
 	
 	/**
 	 * 当前时间是不是在三周年活动期间
     * @Author:zhangbole
     * @return  boolean
     * @date 2016年9月28日
     */
 	public boolean isDuringThirdYeayAnniversary(){//活动页与对应接口的开关
 		boolean ret = false;
 		String strStart = PropertyConfigurer.stringValue("third_year_anniversary_start");
 		String strEnd = PropertyConfigurer.stringValue("third_year_anniversary_end");
 		if(StringUtils.isEmpty(strEnd)||StringUtils.isEmpty(strStart)){//如果配置文件中的两个属性有一个消失，就ren
 			return false;
 		}
 		long now = 0;
 		long start = 0;
 		long end = 0;
 		try {
 			now = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
 	 		start = Long.parseLong(strStart);
 	 		end = Long.parseLong(strEnd);
		} catch (NumberFormatException e) {
			logger.warn("配置文件中third_year_anniversary_start或third_year_anniversary_end的值有误");
			return false;
		}
 		
 		if(now>=start && now<=end){
 			ret = true;
 		}
 		return ret;
 	}
 	
 	/**
 	 * teachenId加密，用于facebook分享链接
     * @Author:zhangbole
     * @return  boolean
     * @date 2016年10月8日
     */
 	private String encode(long teacherId){
 		return AES.encrypt(String.valueOf(teacherId), AES.getKey(AES.KEY_LENGTH_128,ApplicationConstant.AES_128_KEY));
 	}
 	/**
 	 * teachenId解密，用于facebook分享链接
     * @Author:zhangbole
     * @return  boolean
     * @date 2016年10月8日
     */
 	private long decode(String token){
 		String teacherId =  AES.decrypt(token, AES.getKey(AES.KEY_LENGTH_128,ApplicationConstant.AES_128_KEY));
 		if(teacherId.matches("[1-9]\\d+")){
 			return Long.parseLong(teacherId);
 		}
 		return -1;
 	}
    
}
