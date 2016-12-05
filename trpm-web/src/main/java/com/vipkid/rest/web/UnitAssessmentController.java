package com.vipkid.rest.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.community.tools.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.google.api.client.util.Maps;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.http.service.AssessmentHttpService;
import com.vipkid.http.vo.OnlineClassVo;
import com.vipkid.http.vo.StudentUnitAssessment;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.service.LoginService;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.portal.OnlineClassService;
import com.vipkid.trpm.vo.ResponseVo;

/**
 * Created by LP-813 on 2016/10/26.
 */
@RestController
public class UnitAssessmentController {

    private static final Logger logger = LoggerFactory.getLogger(UnitAssessmentController.class);


    @Autowired
    private LoginService loginService;
    @Autowired
    private OnlineClassService onlineClassService;
    @Autowired
    private AssessmentHttpService assessmentHttpService;



    @RequestMapping(value = "/unfinishedUA", method = RequestMethod.GET,produces = RestfulConfig.JSON_UTF_8)
    @ResponseBody
    public Object getUnfinishedUA(OnlineClassVo onlineClassVoCond ,@RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize) {
        logger.info("[/unfinishedUA] ====,{}", JSONObject.toJSONString(onlineClassVoCond));
        try{
        HashMap<String,Object> cond = new HashMap<String,Object>();
        cond.put("lessonSn",onlineClassVoCond.getLessonSn());
        cond.put("course",StringUtils.trim(onlineClassVoCond.getCourseName()));
        Long from = 0l;
        Long to = 0l;
        if(onlineClassVoCond.getFrom()!=null){
            from = org.apache.commons.lang3.math.NumberUtils.toLong(onlineClassVoCond.getFrom());
        }else{
            from = new Date().getTime() - 7*86400*1000l;
        }
        if(onlineClassVoCond.getTo()!=null){
            to = org.apache.commons.lang3.math.NumberUtils.toLong(onlineClassVoCond.getTo());
        }else{
            to = new Date().getTime();
        }
        cond.put("from",from);
        cond.put("to",to);
        cond.put("teacherName",onlineClassVoCond.getTeacherName());
        cond.put("studentName",onlineClassVoCond.getStudentName());

        HashMap<String,Object> onlineClassPage = onlineClassService.getUnfinishUA(cond,pageNo, pageSize);
        List<OnlineClassVo> onlineClassVos = (List<OnlineClassVo>) onlineClassPage.get("onlineClassVos");
        Integer total = (Integer) onlineClassPage.get("total");
        Map<String, Object> result = Maps.newHashMap();

        //获取分页ID
        List<Long> ids = new ArrayList<Long>();
        for(OnlineClassVo onlineClass:onlineClassVos){
            ids.add(onlineClass.getId());
        }

        //获取UA审核结果
        OnlineClassVo onlineClassVo = new OnlineClassVo();
        onlineClassVo.setIdList(ids);
        List<StudentUnitAssessment> stuUaList = assessmentHttpService.findOnlineClassVo(onlineClassVo);
        for(StudentUnitAssessment studentUnitAssessment : stuUaList){
            studentUnitAssessment.setUpdateTime(studentUnitAssessment.getUpdateTime1());
        }
        //跨库join
        if(onlineClassVos == null){
            onlineClassVos = new ArrayList<OnlineClassVo>();
        }
        if(stuUaList == null){
            stuUaList = new ArrayList<StudentUnitAssessment>();
        }
        for(OnlineClassVo oc :onlineClassVos){
            for(StudentUnitAssessment stuUa : stuUaList){
                if(oc.getId().equals(stuUa.getOnlineClassId().longValue())){
                    oc.setHasAudited(stuUa.getIsRefillin());
                    oc.setSubmitDateTime(stuUa.getSubmitDateTime());
                    oc.setAuditorId(stuUa.getAuditorId().intValue());
                    oc.setAuditorName(stuUa.getAuditorName());
                    oc.setRefillinOpId(stuUa.getRefillinOpId());
                    oc.setRefillinOpName(stuUa.getRefillinOpName());
                    oc.setUpdateTime(stuUa.getUpdateTime());
                }
            }
        }
        result.put("status", HttpStatus.OK.value());
        result.put("info",onlineClassVos);
        result.put("now",new Date().getTime());
        result.put("from",onlineClassVoCond.getFrom());
        result.put("to",onlineClassVoCond.getTo());
        result.put("total",total);
        return JsonTools.getJson(result);
        }catch (Exception e){
            e.printStackTrace();
            return new Object();
        }
    }

    /**
     * 教师端UA入口，为yoda提供调用
     * @param id
     * @return
     */
    @RequestMapping(value = "/onlineClass/fetchById/{id}", method = RequestMethod.GET)
    @RestInterface(lifeCycle = LifeCycle.REGULAR)
    public Object findUaBasicInfo(@PathVariable Long id,HttpServletResponse response){
        logger.info("[/onlineClass/fetchById/{id}]========={}",JSONObject.toJSONString(id));
        Teacher loginTeacher = loginService.getTeacher();
        Map<String, Object> result = Maps.newHashMap();
        User user = loginService.getUser();
        if (null == loginTeacher || null == user || null == id) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return ResponseVo.getReponseVo(HttpStatus.NOT_FOUND.value(),"没有权限",null);
        }
        Map<String,Object> onlineClass = onlineClassService.findOnlineClassUaInfoById(id);
        if(onlineClass == null || onlineClass.size()==0){
            return ResponseVo.getReponseVo(1,"没有数据",onlineClass);
        }
        if (null == onlineClass.get("teacherId") || null == onlineClass.get("studentId")
                || !String.valueOf(onlineClass.get("teacherId")).equals(String.valueOf(loginTeacher.getId()))) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return ResponseVo.getReponseVo(HttpStatus.FORBIDDEN.value(), "没有权限", null);
        }
        return ResponseVo.getReponseVo(0,"查询成功",onlineClass);
    }


}
