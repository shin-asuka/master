package com.vipkid.trpm.service.passport;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.vipkid.email.EmailUtils;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.RecruitmentChannel;
import com.vipkid.enums.UserEnum;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.teacher.tools.security.SHA256PasswordEncoder;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.RedisConstants;
import com.vipkid.trpm.dao.AppRestfulDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.util.AES;

/**
 * 用于passport的主要业务 1.包含Teacher的token更新，SignUp实现
 * 
 * @author Along(ZengWeiLong)
 * @ClassName: PassportService
 * @date 2016年3月3日 上午11:57:39
 *
 */
@Service
public class PassportService {

    private static Logger logger = LoggerFactory.getLogger(PassportService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private AppRestfulDao appRestfulDao;

    @Autowired
    private RedisProxy redisProxy;

    @Autowired
    private VerifyCodeService verifyCodeService;

    /**
     * 通过id查询Teacher
     * 
     * @Author:ALong (ZengWeiLong)
     * @param id
     * @return Teacher
     * @date 2016年3月3日
     */
    public Teacher findTeacherById(long id) {
        if (id == 0) {
            return null;
        }
        return this.teacherDao.findById(id);
    }

    /**
     * 通过username 查询User
     * 
     * @Author:ALong (ZengWeiLong)
     * @param username
     * @return User
     * @date 2016年3月3日
     */
    public User findUserByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            logger.warn("用户名为空：{}",username);
            return null;
        }
        return this.userDao.findByUsername(username);
    }

    /**
     * 通过id 查询User
     * 
     * @Author:ALong (ZengWeiLong)
     * @param id
     * @return User
     * @date 2016年3月3日
     */
    public User findUserById(long id) {
        if (id == 0) {
            return null;
        }
        return this.userDao.findById(id);
    }

    /**
     * 判断Email对应的账户是否存在<br/>
     * 1.是否存在<br/>
     * 2.创建User<br/>
     * 3.创建Teacher<br/>
     * 4.发送邮件 邮件中带有激活用户邮件的链接
     * 
     * @Author:VIPKID-ZengWeiLong
     * @return 2016年3月3日
     */
    public Map<String, Object> saveSignUp(String email, String password, Long reid, Long partnerId) {
        Map<String, Object> resultMap = Maps.newHashMap();
        User user = this.userDao.findByUsername(email);
        // 1.是否存在
        if (user != null) {
            return ReturnMapUtils.returnFail(ApplicationConstant.AjaxCode.USER_EXITS);
        }
        // 2.创建User
        user = new User();
        user.setUsername(email);
        if (StringUtils.isBlank(password)) {
            password = UserEnum.DEFAULT_TEACHER_PASSWORD;
        }
        String strPwd = new String(Base64.getDecoder().decode(password));
        SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
        user.setPassword(encoder.encode(strPwd));
        
        if(PropertyConfigurer.booleanValue("signup.send.mail.switch")){
            user.setStatus(UserEnum.Status.LOCKED.toString());
        }else{
            user.setStatus(UserEnum.Status.NORMAL.toString());
        }
        user.setToken(UUID.randomUUID().toString());
        user.setCreateDateTime(new Timestamp(System.currentTimeMillis()));
        user.setLastEditDateTime(new Timestamp(System.currentTimeMillis()));
        user.setRegisterDateTime(new Timestamp(System.currentTimeMillis()));
        user.setRoles(UserEnum.Role.TEACHER.toString());
        user.setDtype(UserEnum.Dtype.TEACHER.val());
        userDao.save(user);
        user.setLastEditorId(user.getId());
        user.setCreaterId(user.getId());
        userDao.update(user);

        // 3.创建 Teacher
        Teacher teacher = new Teacher();
        teacher.setId(user.getId());
        teacher.setEmail(email);
        teacher.setLifeCycle(TeacherEnum.LifeCycle.SIGNUP.toString());
        String serialNumber = teacherDao.getSerialNumber();
        teacher.setSerialNumber(serialNumber);
        teacher.setRecruitmentId(System.currentTimeMillis() + "-"+ encoder.encode(teacher.getSerialNumber() + "kxoucywejl" + teacher.getEmail()));
        teacher.setCurrency(TeacherEnum.Currency.US_DOLLAR.toString());
        teacher.setContractType(TeacherEnum.ContractType.FOUR_A.getVal());
        teacher.setHide(TeacherEnum.Hide.NONE.toString());
        // 设置推荐人保存字段
        teacher = this.prerefereeId(teacher, reid, partnerId);
        teacherDao.save(teacher);
        logger.info(" Sign up teacher: " + teacher.getSerialNumber());
        if(PropertyConfigurer.booleanValue("signup.send.mail.switch")){
            // 4.发送邮件(带着Recruitment ID)
            EmailUtils.sendActivationEmail(teacher);
            //用于注册后跳转的参数(前后端分离后，不需要uuid该参数)
            resultMap.put("uuid", AES.encrypt(teacher.getRecruitmentId(),AES.getKey(AES.KEY_LENGTH_128, ApplicationConstant.AES_128_KEY)));
        }
        resultMap.put("user", user);
        return ReturnMapUtils.returnSuccess(resultMap);
    }

    /**
     * 1.激活用户 2.更新token
     * 
     * @Author:ALong (ZengWeiLong)
     * @param privateCode
     * @return String
     * @date 2016年3月4日
     */
    public String userActivation(String privateCode) {
        Teacher teacher = this.findByRecruitmentId(privateCode);
        if (teacher != null) {
            User user = this.userDao.findById(teacher.getId());
            user.setStatus(UserEnum.Status.NORMAL.toString());
            logger.info(" 激活用户："+user.getUsername());
            int i = this.userDao.update(user);
            if (i > 0) {
                logger.info(" 激活用户成功更新recruitmentId："+user.getUsername());
                return this.updateRecruitmentId(teacher);
            }
        }
        return null;
    }
    
    public void updateUserStatus(User user){
        user.setStatus(UserEnum.Status.NORMAL.toString());
        this.userDao.update(user);
    }

    /**
     * 1.更新token(RecruitmentId) 2.发送邮件
     * 
     * @Author:ALong (ZengWeiLong)
     * @param user
     * @return String
     * @date 2016年3月3日
     */
    public Map<String, Object> senEmailForPassword(User user) {
        Teacher teacher = this.findTeacherById(user.getId());
        if(teacher == null){
            return ReturnMapUtils.returnFail("The is a not exits teacher!");
        }
        teacher.setRecruitmentId(this.updateRecruitmentId(teacher));
        EmailUtils.sendRestPasswordEmail(teacher);
        return ReturnMapUtils.returnSuccess();
    }

    /**
     * 根据 recruitmentId 查找 Teacher 更新recruitmentId
     * 
     * @Author:ALong (ZengWeiLong)
     * @param recruitmentId
     * @return Teacher
     * @date 2016年3月2日
     */
    public Teacher findByRecruitmentIdAndUpdate(String recruitmentId) {
        Teacher resultEntity = this.teacherDao.findByRecruitToken(recruitmentId);
        if (resultEntity != null) {
            resultEntity.setRecruitmentId(this.updateRecruitmentId(resultEntity));
        }
        return resultEntity;
    }

    /**
     * 根据 recruitmentId 查找 Teacher 不更新recruitmentId
     * 
     * @Author:ALong (ZengWeiLong)
     * @param recruitmentId
     * @return Teacher
     * @date 2016年3月2日
     */
    public Teacher findByRecruitmentId(String recruitmentId) {
        Teacher resultEntity = this.teacherDao.findByRecruitToken(recruitmentId);
        return resultEntity;
    }

    /**
     * 单独更新 Teacher 的recruitmentId
     * 
     * @Author:ALong (ZengWeiLong) void
     * @date 2016年3月2日
     */
    public String updateRecruitmentId(Teacher teacher) {
        String recruitmentId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
        recruitmentId = encoder.encode(teacher.getSerialNumber() + recruitmentId + teacher.getEmail());
        recruitmentId = System.currentTimeMillis() + "-" + recruitmentId;
        teacher.setRecruitmentId(recruitmentId);
        this.teacherDao.update(teacher);
        return recruitmentId;
    }

    /**
     * 更新用户密码
     * 
     * @Author:ALong (ZengWeiLong)
     * @param password
     * @return int
     * @date 2016年3月3日
     */
    public Map<String,Object> updatePassword(Teacher teacher, String newpassword) {
        User user = this.userDao.findById(teacher.getId());
        if (user == null){
            return ReturnMapUtils.returnFail("User is null,Id:"+teacher.getId());
        }
        //解码64
        String strPwd = new String(Base64.getDecoder().decode(newpassword));
        if (StringUtils.isBlank(strPwd)) {
            return ReturnMapUtils.returnFail("new password base64 is error ! "+newpassword);
        }
        //SHA256加密
        SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
        user.setPassword(encoder.encode(strPwd));
        
        //如果用户没有token 则生成一个
        if (StringUtils.isBlank(user.getToken())) {
            user.setToken(UUID.randomUUID().toString());
        }
        //更新user的密码
        int i = this.userDao.update(user);
        if (i <= 0) {
            return ReturnMapUtils.returnFail("update user result is "+i+" , id:"+user.getId());
        }
        //更新Teacher修改密码的验证token，使原来的密码修改token失效
        this.updateRecruitmentId(teacher);
        //更新手机端appToken
        Map<String, Object> tokenMap = this.appRestfulDao.findAppTokenByTeacherId(teacher.getId());
        if (MapUtils.isNotEmpty(tokenMap)) {
            this.appRestfulDao.updateTeacherToken(Long.valueOf(tokenMap.get("id") + ""), user.getToken());
        }
        //返回成功
        return ReturnMapUtils.returnSuccess();
        
    }

    /**
     * 检查token是否在最大时间内，如果已经超时返回false，没有超时返回true 24小时
     * 
     * @Author:ALong (ZengWeiLong)
     * @param strToken
     * @return boolean
     * @date 2016年3月4日
     */
    public boolean checkTokenTimeout(String strToken) {
        try {
            long curr = System.currentTimeMillis();
            long tokentime = Long.valueOf(strToken.split("-")[0]);
            long count = curr - tokentime;
            long timeout = PropertyConfigurer.intValue("token.timeout") * 3600 * 1000;
            return count <= timeout ? true : false;
        } catch (Exception e) {
            logger.warn("token :'" + strToken + "' of no avail");
            return false;
        }
    }

    /**
     * 以前：查询用户 根据用户Dtype判断如果是 Teacher 则保存id,name 到referee字段 Partner 则保存id 到parenerId字段
     * 三周年庆更改：
     * refereeId对应用户Dtype判断如果是 Teacher 则保存id,name 到referee字段 Partner 则保存id 到parenerId字段
     * partnerId对应用户Dtype判断如果是 Partner 则保存id 到parenerId字段，优先级高于refereeId是Partner的情况
     * @Author:ALong (ZengWeiLong)
     * @param teacher
     * @param refereeId
     * @return Teacher
     * @date 2016年3月18日
     */
    public Teacher prerefereeId(Teacher teacher, Long refereeId, Long partnerId) {
        
        logger.info("注册时候添加推荐信息参数:{},refereeId:{},parenerId:{}",teacher.getEmail(),refereeId,partnerId);
        
        if (refereeId == null && partnerId == null) {
            logger.warn("参数为空 返回Teacher:{},refereeId:{},parenerId:{}",teacher.getEmail(),refereeId,partnerId);
            return teacher;
        }
        
        logger.info("参数不为空:{},refereeId:{},parenerId:{}",teacher.getEmail(),refereeId,partnerId);
        
        if(refereeId != null){//三周年庆时有改动，这里的if兼容之前的逻辑
            logger.info("开始refereeId判断:{},refereeId:{},parenerId:{}",teacher.getEmail(),refereeId,partnerId);
            User user = this.findUserById(refereeId);
            if (user != null) {
                if (UserEnum.Dtype.PARTNER.val().equals(user.getDtype())) {
                    teacher.setRecruitmentChannel(RecruitmentChannel.PARTNER.toString());
                    teacher.setPartnerId(user.getId());
                    logger.info("PARTNER设置: teacher-refereeId:{},refereeId:{},parenerId:{}",teacher.getEmail(),refereeId,partnerId);
                } else if (UserEnum.Dtype.TEACHER.val().equals(user.getDtype())) {
                    Teacher t = teacherDao.findById(refereeId);
                    if(t!=null){
                        teacher.setRecruitmentChannel(RecruitmentChannel.TEACHER.toString());
                        teacher.setReferee(user.getId() + "," + t.getRealName());//Referee存老师的realName
                        logger.info("TEACHER设置: teacher-refereeId:{},refereeId:{},parenerId:{}",teacher.getEmail(),refereeId,partnerId);
                    }else{
                        teacher.setRecruitmentChannel(RecruitmentChannel.OTHER.toString());
                        teacher.setReferee(user.getId() + ",");
                        logger.warn("没有找到teacher-refereeId:{},refereeId:{},parenerId:{}",teacher.getEmail(),refereeId,partnerId);
                    }
                }
            }else{
                logger.warn("没有找到user-refereeId:{},refereeId:{},parenerId:{}",teacher.getEmail(),refereeId,partnerId);
            }
        }
        
        if(partnerId != null){//三周年庆的需求添加
            User user = this.findUserById(partnerId);
            if (user != null) {
                if (UserEnum.Dtype.PARTNER.val().equals(user.getDtype())) {
                    teacher.setPartnerId(user.getId());
                    teacher.setRecruitmentChannel(RecruitmentChannel.PARTNER.toString());
                    logger.info("PARTNER 三周年设置: teacher-refereeId:{},refereeId:{},parenerId:{}",teacher.getEmail(),refereeId,partnerId);
                }else{
                    logger.warn("没有找到三周年PARENER:{},refereeId:{},parenerId:{}",teacher.getEmail(),refereeId,partnerId);
                }
            }else{
                logger.warn("没有找到三周年User-PARENER:{},refereeId:{},parenerId:{}",teacher.getEmail(),refereeId,partnerId);
            }
        }
        
        return teacher;
    }

    public User findByToken(String token) {
        User user = this.userDao.findByToken(token);
        if (user != null) {
            User upUser = user;
            upUser.setToken(UUID.randomUUID().toString());
            this.userDao.update(upUser);
        }
        return user;
    }

    public User updateUserToken(User user) {
        user.setToken(UUID.randomUUID().toString());
        this.userDao.update(user);
        return user;
    }

    // 同一IP登录次数超过5次
    public boolean isExceedMaxLoginPerIP(String ip) {
        String key = String.format(ApplicationConstant.RedisConstants.LOGIN_IP_MAX_NUM_EXCEED_KEY, ip);
        Integer loginCount = 0;
        try {
            String value = redisProxy.get(key);
            if (value != null) {
                loginCount = Integer.parseInt(value); // 登录次数
            }
        } catch (Exception e) {
            logger.error("redis get key = {}", key, e);
        }
        return loginCount >= ApplicationConstant.RedisConstants.LOGIN_IP_MAX_NUM_EXCEED_DAY_NUM;
    }

    // 登录失败次数超过5次
    public boolean isExceedMaxLoginFailed(String userName) {
        String key = String.format(ApplicationConstant.RedisConstants.LOGIN_PASSWORD_FAILED_DAY_KEY, userName);
        Integer loginFailNum = 0;
        try {
            String value = redisProxy.get(key);
            if (value != null) {
                loginFailNum = Integer.parseInt(value); // 登录失败次数
            }
        } catch (Exception e) {
            logger.error("redis get key = {}", key, e);
        }
        return loginFailNum >= ApplicationConstant.RedisConstants.LOGIN_PASSWORD_FAILED_DAY_NUM;
    }

    // 24小时内密码输入错误 +1
    public void addLoginFailedCount(String userName) {
        String key = String.format(ApplicationConstant.RedisConstants.LOGIN_PASSWORD_FAILED_DAY_KEY, userName);
        Integer loginFailNum = 1;
        try {
            String value = redisProxy.get(key);
            if (value != null) {
                loginFailNum = Integer.parseInt(value) +1; // 登录失败次数
            }
            redisProxy.setex(key, ApplicationConstant.RedisConstants.LOGIN_PASSWORD_FAILED_DAY_SEC,
                    String.valueOf(loginFailNum));
        } catch (Exception e) {
            logger.error("redis get key = {}", key, e);
        }
    }

    // 记录同一IP的登录次数
    public void addLoginCountPerIP(String ip) {
        if (StringUtils.isEmpty(ip)) {
            logger.warn("同一IP登录计数，but IP为空");
            return;
        }
        String key = String.format(ApplicationConstant.RedisConstants.LOGIN_IP_MAX_NUM_EXCEED_KEY, ip);
        Integer loginCount = 1;
        try {
            String value = redisProxy.get(key);
            Long ttl = redisProxy.ttl(key);
            //modified by luojiaoxia 过期时间不能是0
            if (value != null && ttl > 0) {
                loginCount = Integer.parseInt(value) + 1; // 登录失败次数
                redisProxy.setex(key, Integer.parseInt(ttl.toString()), String.valueOf(loginCount));
            } else {
                redisProxy.setex(key, ApplicationConstant.RedisConstants.LOGIN_IP_MAX_NUM_EXCEED_DAY_SEC,
                        String.valueOf(loginCount));
            }
        } catch (Exception e) {
            logger.error("redis get key = {}", key, e);
        }
    }

    // 校验图片验证码的合法性
    public boolean checkVerifyCode(String key, String imageCode) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(imageCode)) {
            return false;
        }
        return verifyCodeService.checkVerifyCode(key, imageCode);
    }
    
	public String getQuitTeacherExpiredTime(Long teacherId, Long expireTime) {
		String value = null;
		String key = RedisConstants.QUIT_TEACHER_EXPIRED_TIME + teacherId;
		try {
			String existValue = redisProxy.get(key);
			if (StringUtils.isNoneEmpty(existValue)) {
				value = existValue;
			} else {
				if (expireTime != null) {
					redisProxy.set(key, expireTime.toString());
					value = expireTime.toString();
				}
			}
		} catch (Exception e) {
			logger.error("redis get key = {}", key, e);
		}
		return value;
	}
}
