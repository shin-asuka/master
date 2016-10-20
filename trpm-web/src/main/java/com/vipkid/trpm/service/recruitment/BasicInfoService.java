package com.vipkid.trpm.service.recruitment;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.vipkid.enums.UserEnum;
import com.vipkid.rest.app.BasicInfoBean;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.TeacherLifeCycle;
import com.vipkid.trpm.dao.TeacherAddressDao;
import com.vipkid.trpm.dao.TeacherApplicationDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeachingExperienceDao;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeacherApplication;
import com.vipkid.trpm.entity.TeachingExperience;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.util.AES;
import com.vipkid.trpm.util.EmailUtils;

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
    public Map<String,Object> submitInfo(BasicInfoBean bean,User user){ 
        Map<String,Object> result = Maps.newHashMap();        
        Teacher teacher = this.teacherDao.findById(user.getId());
        List<TeacherApplication> applicationList = teacherApplicationDao.findApplictionForStatus(user.getId(),TeacherApplicationDao.Status.BASIC_INFO.toString());
        if(CollectionUtils.isNotEmpty(applicationList)){
            result.put("status", false);
            result.put("info", "You have already submitted data!");
            logger.warn("已经提交基本信息的用户{}，重复提交被拦截:提交状态{},审核结果{},用户状态:{}",teacher.getId(),applicationList.get(0).getStatus(),applicationList.get(0).getResult(),teacher.getLifeCycle());
            return result;
        }
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
        //3.更新Teacher
        teacher.setCurrentAddressId(teacherAddress.getId());
        teacher.setTimezone(bean.getTimezone());
        teacher.setCountry(bean.getNationality());
        teacher.setPhoneNationCode(bean.getPhoneNationCode());
        teacher.setPhoneNationId(bean.getPhoneNationId());
        teacher.setMobile(bean.getMobile());
        teacher.setPhoneType(bean.getPhoneType());
        teacher.setHighestLevelOfEdu(bean.getHighestLevelOfEdu());
        //4.新增 TeacherApplication
        TeacherApplication application = new TeacherApplication();
        application = teacherApplicationDao.initApplicationData(application);
        application.setTeacherId(teacher.getId());//  步骤关联的教师
        application.setApplyDateTime(new Timestamp(System.currentTimeMillis()));
        application.setStatus(TeacherApplicationDao.Status.BASIC_INFO.toString());
        //5.AutoFail Pass TeacherApplication
        Map<String,String>  checkResult = this.autoFail(teacher);
        //自动审核通过
        if(StringUtils.equals(checkResult.get("result"), TeacherApplicationDao.Result.PASS.toString())){
            //自动审核通过Basic则自动变LifeCycle为Interview
            teacher.setLifeCycle(TeacherLifeCycle.INTERVIEW);
            //Basic审核为PASS
            application.setAuditDateTime(new Timestamp(System.currentTimeMillis()));
            application.setAuditorId(RestfulConfig.SYSTEM_USER_ID);
            application.setResult(TeacherApplicationDao.Result.PASS.toString());
            this.teacherDao.insertLifeCycleLog(teacher.getId(), TeacherLifeCycle.BASIC_INFO, TeacherLifeCycle.INTERVIEW, RestfulConfig.SYSTEM_USER_ID);
            //发送邮件
            EmailUtils.sendEmail4UndoFail(teacher);
            result.put("result", TeacherApplicationDao.Result.PASS);
            result.put("action", "signlogin.shtml?token="+ AES.encrypt(user.getToken(), AES.getKey(AES.KEY_LENGTH_128, ApplicationConstant.AES_128_KEY)));
        }else{
            //自动审核失败
            teacher.setLifeCycle(TeacherLifeCycle.BASIC_INFO);
            //Basic审核为FAIIL
            application.setAuditDateTime(new Timestamp(System.currentTimeMillis()));
            application.setAuditorId(RestfulConfig.SYSTEM_USER_ID);
            application.setResult(TeacherApplicationDao.Result.FAIL.toString());
            //需要写入Fail原因管理端需要展示
            application.setFailedReason(checkResult.get("failReason"));
            result.put("result", TeacherApplicationDao.Result.FAIL);
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
     * 1.更新教育经验表
     * 2.Auto Fail 逻辑
     * @Author:ALong (ZengWeiLong)
     * @param teacher 
     * @return  Map<String,String> 
     * 结果,result:PASS,FAIL,
     * 失败原因,failReason:List<String>
     * @date 2016年10月18日
     */
    private <T> T autoFail(Teacher teacher){
        List<TeachingExperience> list = teachingExperienceDao.findTeachingList(teacher.getId());
        list.stream().parallel().forEach(bean -> {bean.setStatus(TeachingExperienceDao.Status.SUBMIT.val());this.teachingExperienceDao.update(bean);});
        boolean isPass = false;
        //A.合计授课小时 500以上
        double totalHours = list.stream().parallel().mapToDouble(bean -> bean.getTotalHours()).sum();
        //B.国籍(Nationality)判断American or Canadian
        //C.Location
        /*  1. Africa
            2. Middle-East Asia
            3. Russia
            4. Mongolia
            5. Myanmar
            6. Nepal
            7. Oceania
            8. South America
            9. Cambodia
            */
        //D.Highest level of education:Bachelors or higher
        
        return null;
    }
}
