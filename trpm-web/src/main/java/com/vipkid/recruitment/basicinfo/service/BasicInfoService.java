package com.vipkid.recruitment.basicinfo.service;

import java.sql.Timestamp;
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
import com.vipkid.enums.TeacherApplicationEnum.AuditStatus;
import com.vipkid.enums.TeacherApplicationEnum.Result;
import com.vipkid.enums.TeacherApplicationEnum.Status;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.enums.TeacherEnum.RecruitmentChannel;
import com.vipkid.enums.UserEnum;
import com.vipkid.recruitment.common.service.RecruitmentService;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.dao.TeachingExperienceDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.entity.TeachingExperience;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.dto.TeacherDto;
import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherNationalityCodeDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.proxy.RedisProxy;

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
    private RecruitmentService recruitmentService;
    
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
        return ReturnMapUtils.returnSuccess(result);
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
    public Map<String,Object> submitInfo(TeacherDto bean,Teacher teacher,User user,String token){ 
        Map<String,Object> result = Maps.newHashMap();
        
        bean.setFirstName(upperStr(bean.getFirstName()));
        bean.setMiddleName(upperStr(bean.getMiddleName()));
        bean.setLastName(upperStr(bean.getLastName()));

        if(recruitmentService.teacherIsApplicationFinished(teacher)){
            return ReturnMapUtils.returnFail("Your recruitment process is over already, Please refresh your page !","INTERVIEW:"+teacher.getId());
        }
        
        List<TeacherApplication> applicationList = teacherApplicationDao.findApplictionForStatus(teacher.getId(),LifeCycle.BASIC_INFO.toString());
        if(CollectionUtils.isNotEmpty(applicationList)){
            logger.error("已经提交基本信息的老师{}，重复提交被拦截:提交状态{},审核结果{},用户状态:{}",teacher.getId(),applicationList.get(0).getStatus(),applicationList.get(0).getResult(),teacher.getLifeCycle());
            return ReturnMapUtils.returnFail("You have already submitted data!");
        }
        //3.更新Teacher
        teacher = this.initTeacher(teacher, bean);   
        //提交后为BASIC_INFO
        teacher.setLifeCycle(LifeCycle.BASIC_INFO.toString());
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
        //更新缓存中的用户信息
        redisProxy.set(token, JsonTools.getJson(user), 12 * 60 * 60);
        //2.更新Address
        TeacherAddress teacherAddress = this.teacherAddressDao.updateOrSaveCurrentAddressId(teacher, bean.getCountryId(), bean.getStateId(), bean.getCityId(), bean.getStreetAddress(), bean.getZipCode());
        if(teacherAddress == null || teacherAddress.getId() <= 0){
            logger.error("老师:{},地址信息:{},保存有问题.",teacher.getId(),JsonTools.getJson(teacherAddress));
            return ReturnMapUtils.returnFail("You address save error data!");
        }
   
        //4.新增 TeacherApplication
        TeacherApplication application = new TeacherApplication();
        application = teacherApplicationDao.initApplicationData(application);
        application.setTeacherId(teacher.getId());//  步骤关联的教师
        application.setApplyDateTime(new Timestamp(System.currentTimeMillis()));
        application.setStatus(LifeCycle.BASIC_INFO.toString());
        application.setVersion(3);
        //5.AutoFail Pass TeacherApplication
        AutoFailProcessor processor = this.autoFail(teacher,teacherAddress);
        //自动审核通过
        if(processor.isFailed()){
            //Basic审核为FAIIL
            application.setAuditDateTime(new Timestamp(System.currentTimeMillis()));
            application.setAuditorId(RestfulConfig.SYSTEM_USER_ID);
            application.setResult(Result.FAIL.toString());
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
            result.put("result",AuditStatus.TO_AUDIT.toString());
        }else{
            //Basic审核为PASS
            application.setAuditDateTime(new Timestamp(System.currentTimeMillis()));
            application.setAuditorId(RestfulConfig.SYSTEM_USER_ID);
            application.setResult(Result.PASS.toString());
            //发送邮件
            logger.info("调用发送邮件程序发送给:{}",user.getUsername());
            EmailUtils.sendEmail4BasicInfoPass(teacher);
            result.put("result", Result.PASS);            
        }
        this.teacherApplicationDao.save(application);
        this.teacherDao.update(teacher);
        result.put("id", user.getId());
        return ReturnMapUtils.returnSuccess(result);
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
     * 进入下一步骤
     * @param teacher
     * @return
     * Map&lt;String,Object&gt;
     */
    public Map<String,Object> toInterview(Teacher teacher){
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isEmpty(listEntity)){
            return ReturnMapUtils.returnFail("You have no legal power into the next phase !");
        }
        //执行逻辑 只有在INTERVIEW的PASS状态才能进入
        if(Status.BASIC_INFO.toString().equals(listEntity.get(0).getStatus())
                && Result.PASS.toString().equals(listEntity.get(0).getResult())){
            //按照新流程 该步骤将老师的LifeCycle改变为basicinfo -> Interview
            teacher.setLifeCycle(LifeCycle.INTERVIEW.toString());
            this.teacherDao.insertLifeCycleLog(teacher.getId(),LifeCycle.BASIC_INFO,LifeCycle.INTERVIEW, teacher.getId());
            this.teacherDao.update(teacher);
            return ReturnMapUtils.returnSuccess();
        }
        return ReturnMapUtils.returnFail("You have no legal power into the next phase !");
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
        str = StringUtils.trim(str);
        if(StringUtils.isNotBlank(str)){
            return str.replaceFirst(str.substring(0, 1),str.substring(0, 1).toUpperCase()) ;
        }
        return str;
    }
}
