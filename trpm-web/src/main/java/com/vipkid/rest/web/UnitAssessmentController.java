package com.vipkid.rest.web;

import com.google.api.client.util.Maps;
import com.vipkid.http.service.AssessmentHttpService;
import com.vipkid.http.vo.OnlineClassVo;
import com.vipkid.http.vo.StudentUnitAssessment;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.task.utils.UADateUtils;
import com.vipkid.trpm.entity.Student;
import com.vipkid.trpm.service.portal.OnlineClassService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.community.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;

/**
 * Created by LP-813 on 2016/10/26.
 */
@RestController
public class UnitAssessmentController {

    @Autowired
    private OnlineClassService onlineClassService;
    @Autowired
    private AssessmentHttpService assessmentHttpService;

    @RequestMapping(value = "/unfinishedUA", method = RequestMethod.GET,produces = RestfulConfig.JSON_UTF_8)
    @ResponseBody
    public Object getUnfinishedUA(OnlineClassVo onlineClassVoCond ,@RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize) {
        HashMap<String,Object> cond = new HashMap<String,Object>();
        cond.put("lessonSn",onlineClassVoCond.getLessonSn());
        cond.put("courseId",onlineClassVoCond.getCourseId());
        cond.put("from",onlineClassVoCond.getFrom());
        cond.put("to",onlineClassVoCond.getTo());
        cond.put("teacherName",onlineClassVoCond.getTeacherName());
        cond.put("studentName",onlineClassVoCond.getStudentName());

        List<OnlineClassVo> onlineClassVos = onlineClassService.getUnfinishUA(cond,pageNo, pageSize);
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

        //跨库join
        for(OnlineClassVo oc :onlineClassVos){
            for(StudentUnitAssessment stuUa : stuUaList){
                if(oc.getId().equals(stuUa.getOnlineClassId().longValue())){
                    oc.setAuditorId(stuUa.getAuditorId().intValue());
                    oc.setAuditorName(stuUa.getAuditorName());
                }
            }
        }
        result.put("status", HttpStatus.OK.value());
        result.put("info",onlineClassVos);
        return JsonTools.getJson(result);
    }
}
