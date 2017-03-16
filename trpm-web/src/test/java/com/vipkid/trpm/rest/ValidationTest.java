package com.vipkid.trpm.rest;

import java.util.List;

import org.community.tools.JsonTools;
import org.junit.Test;

import com.vipkid.portal.classroom.model.PeCommentsVo;
import com.vipkid.portal.classroom.util.BeanUtils;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.rest.dto.TeachingExperienceDto;
import com.vipkid.rest.validation.ValidateUtils;
import com.vipkid.rest.validation.tools.Result;

public class ValidationTest {

    @Test
    public void test1() {
     
        TeachingExperienceDto dto = new TeachingExperienceDto();
        dto.setOrganisationName("伪类");
        dto.setJobDescription("WERTYUIOFGHJKLFGHJKLFGHJ");
        
        List<Result> list = ValidateUtils.checkBean(dto, false);
        list.stream().parallel().forEach(bean ->{System.out.println("check:"+JsonTools.getJson(bean));});
        
        /*
        Result result1 = ValidationUtils.checkForField(dto); 
        if(result1 != null){
            System.out.println(JsonTools.getJson(result1));
        }else{
            System.out.println("OK");
        }
        
        
        Result result = ValidationUtils.checkForField(dto,Lists.newArrayList("id","organisationName","jobDescription"));
        if(result != null){
            System.out.println(JsonTools.getJson(result));
        }else{
            System.out.println("OK");
        }
        */
        //System.out.println(AppUtils.containsName(AppEnum.Gender.class, "MALE1"));
        
    }
    
    
    public static void main(String[] args) {
		
    	TeacherApplication teacherApplication = new TeacherApplication();
    	teacherApplication.setDelayDays(1);
    	
    	PeCommentsVo bean = new PeCommentsVo();
    	bean.setDelayDays(2);
    	    	
    	BeanUtils.copyPropertys(bean,teacherApplication);
    	
    	System.out.println("===="+bean.getDelayDays());
    	System.out.println("===="+teacherApplication.getDelayDays());
    	
    	

	}
}
