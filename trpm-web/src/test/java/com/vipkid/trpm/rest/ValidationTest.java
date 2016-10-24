package com.vipkid.trpm.rest;

import java.util.List;

import org.community.tools.JsonTools;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.vipkid.rest.dto.TeachingExperienceDto;
import com.vipkid.rest.validation.ValidationUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.trpm.entity.app.AppEnum;
import com.vipkid.trpm.util.AppUtils;

public class ValidationTest {

    @Test
    public void test1() {
     
        TeachingExperienceDto dto = new TeachingExperienceDto();
        dto.setOrganisationName("伪类");
        dto.setJobDescription("WERTYUIOFGHJKLFGHJKLFGHJ");
        List<Result> list = ValidationUtils.checkForClass(dto, true);
        list.stream().parallel().forEach(bean ->{System.out.println("check:"+JsonTools.getJson(bean));});
        
        
        Result result = ValidationUtils.checkForField(dto,Lists.newArrayList("id","organisationName","jobDescription"));
        if(result != null){
            System.out.println(JsonTools.getJson(result));
        }else{
            System.out.println("OK");
        }
        
        System.out.println(AppUtils.containsName(AppEnum.Gender.class, "MALE1"));
        
    }
}
