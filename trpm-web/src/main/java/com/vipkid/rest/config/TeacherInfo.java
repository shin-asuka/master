package com.vipkid.rest.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;

import com.vipkid.rest.config.RestfulConfig.RoleClass;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.util.AES;

public class TeacherInfo {

    @SuppressWarnings("serial")
    private Map<String,Object> roles = new HashMap<String, Object>(){{
        put(RoleClass.PE, false);
        put(RoleClass.PES, false);
        put(RoleClass.TE, false);
        put(RoleClass.TES, false);
    }};
    
    private long teacherId;
    
    private String evaluationBio = "";
    
    private String headsrc = "";
    
    private String showName = "";
    
    private String lifeCycle = "";

    private String action = "";
    
    private boolean haveChannel = false;

    public Map<String, Object> getRoles() {
        return roles;
    }

    public TeacherInfo setRoles(Map<String, Object> roles) {
        this.roles = roles;
        return this;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public TeacherInfo setTeacherId(long teacherId) {
        this.teacherId = teacherId;
        return this;
    }

    public String getEvaluationBio() {
        return evaluationBio;
    }

    public TeacherInfo setEvaluationBio(String evaluationBio) {
        this.evaluationBio = evaluationBio;
        return this;
    }

    public String getHeadsrc() {
        return headsrc;
    }

    public TeacherInfo setHeadsrc(String headsrc) {
        this.headsrc = headsrc;
        if(StringUtils.isNotBlank(headsrc)){
            this.headsrc = PropertyConfigurer.stringValue("oss.url_preffix") + (headsrc.startsWith("/") ? headsrc:"/"+headsrc);
        }
        return this;
    }

    public String getShowName() {
        return showName;
    }

    public TeacherInfo setShowName(String showName) {
        this.showName = showName;
        return this;
    }

    public String getLifeCycle() {
        return lifeCycle;
    }

    public TeacherInfo setLifeCycle(String lifeCycle) {
        this.lifeCycle = lifeCycle;
        return this;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    
    public boolean isHaveChannel() {
        return haveChannel;
    }

    public void setHaveChannel(boolean haveChannel) {
        this.haveChannel = haveChannel;
    }
    /**
     * 其他信息，头像,bio,lifeCycle,name
     * @Author:ALong (ZengWeiLong)
     * @param teacher
     * @param user    
     * void
     * @date 2016年10月12日
     */
    public void setInfo(Teacher teacher,User user){
        this.setHeadsrc(teacher.getAvatar());
        this.setEvaluationBio(teacher.getEvaluationBio());
        this.setLifeCycle(teacher.getLifeCycle());
        this.setShowName(user.getName());
        Set<String> portSet = RestfulConfig.TEACHERPORTSET;
        portSet.addAll(RestfulConfig.NEWRECRUITMENTSET);
        //如果进入招聘端了,需要获取招聘端登陆link
        if(!portSet.contains(teacher.getLifeCycle())){
            this.setAction("signlogin.shtml?token="+ AES.encrypt(user.getToken(), AES.getKey(AES.KEY_LENGTH_128, ApplicationConstant.AES_128_KEY)));
        }
        this.setHaveChannel(StringUtils.isNoneBlank(teacher.getReferee()) || teacher.getPartnerId() > 0 || StringUtils.isNotBlank(teacher.getOtherChannel()));
    }
   
}
