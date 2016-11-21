package com.vipkid.trpm.service.passport;

import static com.vipkid.trpm.constant.ApplicationConstant.NEW_TEACHER_NAME;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
import com.vipkid.email.templete.TempleteUtils;
import com.vipkid.enums.Role;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.RecruitmentChannel;
import com.vipkid.enums.UserEnum;
import com.vipkid.recruitment.utils.ResponseUtils;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.dao.AppRestfulDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.security.SHA256PasswordEncoder;
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
    public Map<String, Object> saveSignUp(String email, String password, Object reid, Object partnerId) {
        Map<String, Object> resultMap = Maps.newHashMap();
        User user = this.userDao.findByUsername(email);
        SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();

        // 1.是否存在
        if (user == null) {
            // 2.创建User
            user = new User();
            user.setUsername(email);
            if (StringUtils.isBlank(password)) {
                password = UserEnum.DEFAULT_TEACHER_PASSWORD;
            }
            String strPwd = new String(Base64.getDecoder().decode(password));
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
            user.setRoles(Role.TEACHER.toString());
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
            teacher.setHide(TeacherEnum.Hide.NONE.toString());
            // 设置推荐人保存字段
            teacher = this.prerefereeId(teacher, reid, partnerId);
            teacherDao.save(teacher);
            logger.info(" Sign up teacher: " + teacher.getSerialNumber());
            if(PropertyConfigurer.booleanValue("signup.send.mail.switch")){
                // 4.发送邮件(带着Recruitment ID)
                Map<String, String> map = Maps.newHashMap();
                map.put("teacherName", NEW_TEACHER_NAME);
                map.put("link", PropertyConfigurer.stringValue("teacher.www") + "activation.shtml?uuid="
                        + teacher.getRecruitmentId());
                TempleteUtils templete = new TempleteUtils();
                Map<String, String> sendMap = templete.readTemplete("VIPKIDAccountActivationLink.html", map,
                        "VIPKIDAccountActivationLink-Title.html");
                new EmailEngine().addMailPool(email, sendMap, EmailFormEnum.TEACHVIP);
                resultMap.put("uuid", AES.encrypt(teacher.getRecruitmentId(),
                        AES.getKey(AES.KEY_LENGTH_128, ApplicationConstant.AES_128_KEY)));
            }
            resultMap.put("user", user);
            return ResponseUtils.responseSuccess(resultMap);
        } else {
            return ResponseUtils.responseFail(ApplicationConstant.AjaxCode.USER_EXITS, this);
        }
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
            int i = this.userDao.update(user);
            if (i > 0) {
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
     * 1.更新token 2.发送邮件
     * 
     * @Author:ALong (ZengWeiLong)
     * @param user
     * @return String
     * @date 2016年3月3日
     */
    public Map<String, Object> senEmailForPassword(User user) {
        Teacher teacher = this.findTeacherById(user.getId());
        teacher.setRecruitmentId(this.updateRecruitmentId(teacher));
        TempleteUtils templete = new TempleteUtils();
        Map<String, String> map = Maps.newHashMap();
        if (StringUtils.isEmpty(teacher.getRealName())) {
            map.put("teacherName", NEW_TEACHER_NAME);
        } else {
            map.put("teacherName", teacher.getRealName());
        }
        map.put("link", PropertyConfigurer.stringValue("teacher.www") + "modifyPassword.shtml?validate_token="
                + teacher.getRecruitmentId());
        Map<String, String> sendMap = templete.readTemplete("VIPKIDPasswordResetLink.html", map,
                "VIPKIDPasswordResetLink-Title.html");
        new EmailEngine().addMailPool(teacher.getEmail(), sendMap, EmailFormEnum.TEACHVIP);
        return ResponseUtils.responseSuccess();
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
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
        uuid = encoder.encode(teacher.getSerialNumber() + uuid + teacher.getEmail());
        uuid = System.currentTimeMillis() + "-" + uuid;
        teacher.setRecruitmentId(uuid);
        this.teacherDao.update(teacher);
        return uuid;
    }

    /**
     * 更新用户密码
     * 
     * @Author:ALong (ZengWeiLong)
     * @param password
     * @return int
     * @date 2016年3月3日
     */
    public String updatePassword(Teacher teacher, String password) {
        User user = this.userDao.findById(teacher.getId());
        if (user == null)
            return null;
        String strPwd = new String(Base64.getDecoder().decode(password));
        if (StringUtils.isEmpty(strPwd)) {
            return null;
        }
        SHA256PasswordEncoder encoder = new SHA256PasswordEncoder();
        user.setPassword(encoder.encode(strPwd));
        if (StringUtils.isEmpty(user.getToken())) {
            user.setToken(UUID.randomUUID().toString());
        }
        // 更新手机端appToken
        Map<String, Object> tokenMap = this.appRestfulDao.findAppTokenByTeacherId(teacher.getId());
        if (tokenMap != null && !tokenMap.isEmpty()) {
            this.appRestfulDao.updateTeacherToken(Long.valueOf(tokenMap.get("id") + ""), user.getToken());
        }
        int i = this.userDao.update(user);
        if (i > 0) {
            this.updateRecruitmentId(teacher);
            return AES.encrypt(user.getToken(), AES.getKey(AES.KEY_LENGTH_128, ApplicationConstant.AES_128_KEY));
        }
        return null;
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
    public Teacher prerefereeId(Teacher teacher, Object refereeId, Object partnerId) {
        if (refereeId == null &&partnerId == null) {
            return teacher;
        }
        
        if(refereeId != null){//三周年庆时有改动，这里的if兼容之前的逻辑
        String rfid = String.valueOf(refereeId);
        if (!StringUtils.isNumeric(rfid)) {
            return teacher;
        }
        long userId = Long.valueOf(rfid);
        User user = this.findUserById(userId);

            if (user != null) {
                if (UserEnum.Dtype.PARTNER.toString().equals(user.getDtype())) {
                    teacher.setRecruitmentChannel(RecruitmentChannel.PARTNER.toString());
                    teacher.setPartnerId(user.getId());
                } else if (UserEnum.Dtype.TEACHER.toString().equals(user.getDtype())) {
                	Teacher t = teacherDao.findById(userId);
                	if(t!=null){
                	    teacher.setRecruitmentChannel(RecruitmentChannel.TEACHER.toString());
                		teacher.setReferee(user.getId() + "," + t.getRealName());//Referee存老师的realName
                	}else{
                	    teacher.setRecruitmentChannel(RecruitmentChannel.OTHER.toString());
                	    teacher.setReferee(user.getId() + ",");
                		logger.warn("找不到id为"+userId+"老师");
                	}
                }
            }
        }
        if(partnerId != null){//三周年庆的需求添加
        	String ptid = String.valueOf(partnerId);
            if (!ptid.matches("[^0][\\d]+")) {
                return teacher;
            }
            long userId = Long.valueOf(ptid);
            User user = this.findUserById(userId);

            if (user != null) {
                if (UserEnum.Dtype.PARTNER.toString().equals(user.getDtype())) {
                    teacher.setPartnerId(user.getId());
                    teacher.setRecruitmentChannel(UserEnum.Dtype.PARTNER.toString());
                } 
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
            if (value != null && ttl >= 0) {
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

}
