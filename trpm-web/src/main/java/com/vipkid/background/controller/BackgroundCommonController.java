package com.vipkid.background.controller;

import com.alibaba.druid.util.StringUtils;
import com.vipkid.background.api.sterling.dto.BackgroundFileStatusDto;
import com.vipkid.background.api.sterling.dto.BackgroundStatusDto;
import com.vipkid.background.service.BackgroundCommonService;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.rest.RestfulController;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.rest.interceptor.annotation.RestInterface;
import com.vipkid.rest.utils.ApiResponseUtils;
import com.vipkid.trpm.entity.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RestInterface(lifeCycle = {TeacherEnum.LifeCycle.REGULAR})
@RequestMapping("/background")
public class BackgroundCommonController extends RestfulController{

    @Autowired
    private BackgroundCommonService backgroundCommonService;
    
    private static Logger logger = LoggerFactory.getLogger(BackgroundCommonController.class);
    @RequestMapping(value = "/queryBackgroundStatus", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Object getBackgroundStatus(HttpServletRequest request, HttpServletResponse response){
        Teacher teacher = getTeacher(request);
        try{
            BackgroundStatusDto backgroundStatusDto = new BackgroundStatusDto();
            String nationality = teacher.getCountry();

            if (StringUtils.equalsIgnoreCase(nationality,"United States")){
                 backgroundStatusDto = backgroundCommonService.getUsaBackgroundStatus(teacher);
                 backgroundStatusDto.setNationality("USA");
            }else if (StringUtils.equalsIgnoreCase(nationality,"CANADA")){
                backgroundStatusDto = backgroundCommonService.getCanadaBackgroundStatus(teacher);
                backgroundStatusDto.setNationality("CANADA");
            }else{
                backgroundStatusDto.setContractEndWithInOneMonth(false);
                backgroundStatusDto.setNeedBackgroundCheck(false);
                backgroundStatusDto.setNationality("OTHERS");
                backgroundStatusDto.setPhase("");
                backgroundStatusDto.setResult("");
            }
            response.setStatus(HttpStatus.OK.value());
            return ApiResponseUtils.buildSuccessDataResp(backgroundStatusDto);
        } catch (Exception e) {
            logger.error("get teacher {} background check status  occur Exception",teacher.getId(),e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(500,e.getMessage());
        }

    }
    @RequestMapping(value = "/queryBackgroundFileStatus", method = RequestMethod.GET, produces = RestfulConfig.JSON_UTF_8)
    public Map<String,Object> getBackgroundFileStatus(HttpServletRequest request, HttpServletResponse response){
        Teacher teacher = getTeacher(request);
        try{
            long teacherId = teacher.getId();
            String nationality = teacher.getCountry();
            BackgroundFileStatusDto backgroundFileStatusDto  = backgroundCommonService.getBackgroundFileStatus(teacherId,nationality);
            return ApiResponseUtils.buildSuccessDataResp(backgroundFileStatusDto);
        } catch (Exception e) {
            logger.error("get teacher {} background check file info occur Exception",teacher.getId(),e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ApiResponseUtils.buildErrorResp(500,e.getMessage());
        }

    }

}
