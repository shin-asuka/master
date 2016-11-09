package com.vipkid.trpm.service.recruitment;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.vipkid.email.EmailUtils;
import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.UserEnum;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.dto.TeacherDto;
import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.dao.TeacherApplicationDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherNationalityCodeDao;
import com.vipkid.trpm.dao.TeachingExperienceDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherApplication;
import com.vipkid.trpm.entity.TeacherNationalityCode;
import com.vipkid.trpm.entity.TeachingExperience;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.entity.app.AppEnum;
import com.vipkid.trpm.entity.app.AppEnum.RecruitmentChannel;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.util.DateUtils;

@Service
public class BasicInfoService {
    
    private static Logger logger = LoggerFactory.getLogger(BasicInfoService.class);
    
    @Autowired
    private TeachingExperienceDao teachingExperienceDao;
    
    @Autowired
    private TeacherDao teacherDao;
    
    @Autowired    
    private UserDao userDao;
    
    @Autowired
    private TeacherAddressDao teacherAddressDao;
    
    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    
    @Autowired
    private TeacherNationalityCodeDao teacherNationalityCode;
    
    @Autowired
    private RedisProxy redisProxy;

    /**
     * 查询可用的招聘渠道
     *  
     * @Author:ALong (ZengWeiLong)
     * @return    
     * List<Map<String,Object>>
     * @date 2016年10月18日
     */
    public List<Map<String,Object>> getRecruitmentChannelList(){
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("type", "TEACHER_RECRUITMENT");
        paramMap.put("status", UserEnum.Status.NORMAL);
        paramMap.put("dtype", "Partner");
        return this.teachingExperienceDao.findRecruitingChannel(paramMap);
    }
    
    
    /**
     * 1.保存地址表
     * 4.更新Teacher表
     * 5.更新User表
     * @Author:ALong (ZengWeiLong)
     * @param bean
     * @param user
     * @param teacher
     * @return    
     * Map<String,Object>
     * @date 2016年10月17日
     */
    public Map<String,Object> saveInfo(TeacherDto bean,User user){ 
        Map<String,Object> result = Maps.newHashMap();        
        Teacher teacher = this.teacherDao.findById(user.getId());
        //1.更新User
        user.setGender(bean.getGender());
        this.userDao.update(user);
        //2.更新Address
        TeacherAddress teacherAddress = new TeacherAddress();
        teacherAddress.setCountryId(bean.getCountryId());
        teacherAddress.setStateId(bean.getStateId());
        teacherAddress.setCity(bean.getCityId());
        teacherAddress.setStreetAddress(bean.getStreetAddress());
        teacherAddress.setZipCode(bean.getZipCode());
        this.teacherAddressDao.updateOrSave(teacherAddress);
        teacher.setCurrentAddressId(teacherAddress.getId());
        //3.更新Teacher
        this.initTeacher(teacher, bean);
        this.teacherDao.update(teacher);
        result.put("id", user.getId());
        result.put("status", true);
        return result;
    }
    
    /**
     * 1.插入地址表
     * 2.招聘渠道逻辑(实体里面)
     * 3.国籍替换
     * 4.修改Teacher表
     * 5.修改User表
     * @Author:ALong (ZengWeiLong)
     * @param bean
     * @param user
     * @param teacher
     * @return    
     * Map<String,Object>
     * @date 2016年10月17日
     */
    public Map<String,Object> submitInfo(TeacherDto bean,User user,String token){ 
        Map<String,Object> result = Maps.newHashMap();
        
        bean.setFirstName(upperStr(bean.getFirstName()));
        bean.setMiddleName(upperStr(bean.getMiddleName()));
        bean.setLastName(upperStr(bean.getLastName()));
        
        Teacher teacher = this.teacherDao.findById(user.getId());
        List<TeacherApplication> applicationList = teacherApplicationDao.findApplictionForStatus(user.getId(),AppEnum.LifeCycle.BASIC_INFO.toString());
        if(CollectionUtils.isNotEmpty(applicationList)){
            result.put("status", false);
            result.put("info", "You have already submitted data!");
            logger.error("已经提交基本信息的老师{}，重复提交被拦截:提交状态{},审核结果{},用户状态:{}",teacher.getId(),applicationList.get(0).getStatus(),applicationList.get(0).getResult(),teacher.getLifeCycle());
            return result;
        }
        //3.更新Teacher
        teacher = this.initTeacher(teacher, bean);     
        String name = teacher.getRealName();
        //  如果名字里面含有空格，则取到空格后的第一个字符作为User的Name
        if(name.indexOf(" ") > -1){
            name = name.substring(0,name.indexOf(" ")+2);
        }
        user.setName(name);
        user.setGender(bean.getGender());
        user.setLastEditorId(user.getId());
        user.setLastEditDateTime(new Timestamp(System.currentTimeMillis()));
        this.userDao.update(user);
        redisProxy.set(token, JsonTools.getJson(user), 12 * 60 * 60);
        //2.更新Address
        TeacherAddress teacherAddress = this.teacherAddressDao.updateOrSaveCurrentAddressId(teacher, bean.getCountryId(), bean.getStateId(), bean.getCityId(), bean.getStreetAddress(), bean.getZipCode());
        if(teacherAddress == null || teacherAddress.getId() <= 0){
            result.put("status", false);
            result.put("info", "You have already submitted data!");
            logger.error("老师:{},地址信息:{},保存有问题.",teacher.getId(),JsonTools.getJson(teacherAddress));
            return result;
        }
   
        //4.新增 TeacherApplication
        TeacherApplication application = new TeacherApplication();
        application = teacherApplicationDao.initApplicationData(application);
        application.setTeacherId(teacher.getId());//  步骤关联的教师
        application.setApplyDateTime(new Timestamp(System.currentTimeMillis()));
        application.setStatus(AppEnum.LifeCycle.BASIC_INFO.toString());
        //5.AutoFail Pass TeacherApplication
        AutoFailProcessor processor = this.autoFail(teacher,teacherAddress);
        //自动审核通过
        if(processor.isFailed()){
            //自动审核失败
            teacher.setLifeCycle(AppEnum.LifeCycle.BASIC_INFO.toString());
            //Basic审核为FAIIL
            application.setAuditDateTime(new Timestamp(System.currentTimeMillis()));
            application.setAuditorId(RestfulConfig.SYSTEM_USER_ID);
            application.setResult(TeacherApplicationDao.Result.FAIL.toString());
            //需要写入Fail原因管理端需要展示
            List<String> list = processor.getFailReasons();
            List<Map<String,Object>> _list = Lists.newArrayList();
            if(CollectionUtils.isNotEmpty(list)){
                list.stream().parallel().forEach(reasons -> {
                    Map<String,Object> maps = Maps.newHashMap();
                    maps.put("text", reasons);
                    _list.add(maps);
                });
            }
            application.setFailedReason(JsonTools.getJson(_list));
            result.put("result",TeacherApplicationDao.AuditStatus.ToAudit.toString());
        }else{
            //自动审核通过Basic则自动变LifeCycle为Interview
            teacher.setLifeCycle(AppEnum.LifeCycle.INTERVIEW.toString());
            //Basic审核为PASS
            application.setAuditDateTime(new Timestamp(System.currentTimeMillis()));
            application.setAuditorId(RestfulConfig.SYSTEM_USER_ID);
            application.setResult(TeacherApplicationDao.Result.PASS.toString());
            this.teacherDao.insertLifeCycleLog(teacher.getId(), AppEnum.LifeCycle.BASIC_INFO.toString(),AppEnum.LifeCycle.INTERVIEW.toString(), RestfulConfig.SYSTEM_USER_ID);
            //发送邮件
            logger.info("调用发送邮件程序发送给:{}",user.getUsername());
            EmailUtils.sendEmail4BasicInfoPass(teacher);
            result.put("result", TeacherApplicationDao.Result.PASS);
        }
        application.setVersion(3);
        this.teacherApplicationDao.save(application);
        this.teacherDao.update(teacher);
        result.put("id", user.getId());
        result.put("status", true);
        return result;
    }
    
    /**
     * 获取老师当前LifeCycle状态下的流程结果 
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @return    
     * Map<String,Object>
     * @date 2016年10月19日
     */
    public Map<String,Object> getStatus(long teacherId){
        Map<String,Object> resultMap = Maps.newHashMap();
        Teacher teacher = this.teacherDao.findById(teacherId);
        resultMap.put("lifeCycle",teacher.getLifeCycle());
        resultMap.put("result",TeacherApplicationDao.AuditStatus.ToSubmit.toString());
        List<TeacherApplication> list = this.teacherApplicationDao.findCurrentApplication(teacher.getId());
        
        //没有流程则视为待提交
        if(CollectionUtils.isEmpty(list)){
            resultMap.put("result",TeacherApplicationDao.AuditStatus.ToSubmit.toString());
            return resultMap;
        }
        TeacherApplication teacherApplication = list.get(0);
        
        //当前状态与流程状态不一样,以lifeCycle为准，为待提交
        if(!StringUtils.equalsIgnoreCase(teacherApplication.getStatus(), teacher.getLifeCycle())){
            resultMap.put("result",TeacherApplicationDao.AuditStatus.ToSubmit.toString());
            return resultMap;
        }
        
        //BASIC_INFO 11.5小时之内如果状态是FAIL 为待审核
        if(StringUtils.equalsIgnoreCase(TeacherApplicationEnum.Status.BASIC_INFO.toString(),teacherApplication.getStatus())){
            if(StringUtils.equalsIgnoreCase(TeacherApplicationEnum.Result.FAIL.toString(),teacherApplication.getResult())){
                Date auditDate = teacherApplication.getAuditDateTime(); 
                if(!DateUtils.count11hrlf(auditDate.getTime())){
                    resultMap.put("result",TeacherApplicationDao.AuditStatus.ToAudit.toString());
                    return resultMap;
                }
            }
        }
        
        //待审核
        if(StringUtils.isBlank(teacherApplication.getResult())){
            resultMap.put("result",TeacherApplicationDao.AuditStatus.ToAudit.toString());
            return resultMap;
        }
        //已经审核
        if(StringUtils.isNotBlank(teacherApplication.getResult())){
            resultMap.put("result",teacherApplication.getResult());
            return resultMap;
        }
        
        return resultMap;
    }
    
    /**
     * 渠道为当前老师的数据初始化<br/>
     * @Author:VIPKID-ZengWeiLong
     * @param paramMap
     * @return 2015年10月12日
     */
    public List<Map<String, Object>> findTeacher(){
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("type", "PART_TIME");
        paramMap.put("lifeCycle", TeacherEnum.LifeCycle.REGULAR);
        return teacherDao.findTeacher(paramMap);
    }
    
    public List<TeacherNationalityCode> getTeacherNationalityCodes() {
        return teacherNationalityCode.getTeacherNationalityCodes();
    }

    
    private Teacher initTeacher(Teacher teacher,TeacherDto bean){
        teacher.setExtraClassSalary(0);
        
        String realName = bean.getFirstName() + " " + bean.getLastName();
        if(StringUtils.isNotBlank(bean.getMiddleName())){
            realName = bean.getFirstName() +" " + bean.getMiddleName() + " " + bean.getLastName();
        }
        
        teacher.setFirstName(bean.getFirstName());
        teacher.setMiddleName(bean.getMiddleName());
        teacher.setLastName(bean.getLastName());
        teacher.setRealName(realName);
        teacher.setTimezone(bean.getTimezone());
        teacher.setCountry(bean.getNationality());
        teacher.setPhoneNationCode(bean.getPhoneNationCode());
        teacher.setPhoneNationId(bean.getPhoneNationId());
        teacher.setMobile(bean.getMobile());
        teacher.setPhoneType(bean.getPhoneType());
        teacher.setSkype(bean.getSkype());
        teacher.setHighestLevelOfEdu(bean.getHighestLevelOfEdu());
        //已经设置过招聘渠道将不再设置招聘渠道
        if(StringUtils.isNotBlank(teacher.getReferee()) || teacher.getPartnerId() > 0 || StringUtils.isNotBlank(teacher.getOtherChannel())){
            return teacher;
        }
        //  设置教师招聘渠道
        if(StringUtils.isNotBlank((bean.getRecruitmentChannel()))){
            if(RecruitmentChannel.TEACHER.toString().equalsIgnoreCase(bean.getRecruitmentChannel())){
                teacher.setRecruitmentChannel(RecruitmentChannel.TEACHER.toString());
                teacher.setReferee(bean.getChannel());
            }else if(RecruitmentChannel.PARTNER.toString().equalsIgnoreCase(bean.getRecruitmentChannel())){
                if(StringUtils.isNumeric(bean.getChannel())){
                    teacher.setRecruitmentChannel(RecruitmentChannel.PARTNER.toString());
                    teacher.setPartnerId(Long.valueOf(bean.getChannel()));
                }
            }else if(RecruitmentChannel.OTHER.toString().equalsIgnoreCase(bean.getRecruitmentChannel())){
                teacher.setRecruitmentChannel(RecruitmentChannel.PARTNER.toString());
                //历史遗留问题将Other归属为PARTNER，其ID为：1060753
                teacher.setPartnerId(1060753);
                teacher.setOtherChannel(bean.getChannel());
            }else{
                teacher.setRecruitmentChannel(RecruitmentChannel.OTHER.toString());
                teacher.setOtherChannel(bean.getChannel());
            }
        }
        return teacher;
    }
    
    
    /**
     * 1.更新教育经验表
     * 2.Auto Fail 逻辑
     * @Author:ALong (ZengWeiLong)
     * @param teacher 
     * @return  Map<String,String> 
     * 结果,result:PASS,FAIL,
     * 失败原因,failReason:List<String>
     * @date 2016年10月18日
     */
    private AutoFailProcessor autoFail(Teacher teacher,TeacherAddress teacherAddress){
        List<TeachingExperience> experiences = teachingExperienceDao.findTeachingList(teacher.getId());
        experiences.stream().parallel().forEach(bean -> {bean.setStatus(TeachingExperienceDao.Status.SUBMIT.val());this.teachingExperienceDao.update(bean);});
        AutoFailProcessor processor = new AutoFailProcessor(teacher, experiences,teacherAddress).process();
        if(processor.isFailed()){
            logger.info("Fail Teacher:{},Name:{},Fail:{},FailType:{},FailReasons:{}",teacher.getId(),teacher.getRealName(),processor.isFailed(),processor.getFailTypes(),processor.getFailReasons());
        }else{
            logger.info("Pass Teacher:{},Name:{}",teacher.getId(),teacher.getRealName());
        }
        return processor;
    }
    
    public static String upperStr(String str){
        if(StringUtils.isNotBlank(str)){
            return str.replaceFirst(str.substring(0, 1),str.substring(0, 1).toUpperCase()) ;
        }
        return str;
    }
}
