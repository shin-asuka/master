package com.vipkid.rest.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;

import com.vipkid.enums.TeacherModuleEnum.RoleClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;

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
    //quanxian
    private boolean evaluation = false;
    //quanxian
    private boolean evaluationClick = false;

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
        if (StringUtils.isNotBlank(headsrc) && !StringUtils.startsWithIgnoreCase(headsrc,"http")) {
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
    
    public boolean isEvaluation() {
        return evaluation;
    }

    public void setEvaluation(boolean evaluation) {
        this.evaluation = evaluation;
    }

    public boolean isEvaluationClick() {
        return evaluationClick;
    }

    public void setEvaluationClick(boolean evaluationClick) {
        this.evaluationClick = evaluationClick;
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
        this.setHaveChannel(StringUtils.isNotBlank(teacher.getReferee()) || teacher.getPartnerId() > 0 || StringUtils.isNotBlank(teacher.getOtherChannel()));

    }
   
}
