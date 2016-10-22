package com.vipkid.trpm.rest;

import org.community.tools.JsonTools;

import com.google.common.collect.Lists;
import com.vipkid.rest.dto.TeachingExperienceDto;
import com.vipkid.rest.validation.ValidationUtils;
import com.vipkid.rest.validation.tools.Result;
import com.vipkid.trpm.entity.app.AppEnum;
import com.vipkid.trpm.util.AppUtils;

public class ValidationTest {

    public static void main(String[] args) {
        TeachingExperienceDto dto = new TeachingExperienceDto();
        dto.setId(123L);
        dto.setOrganisationName("");
        Result result = ValidationUtils.checkForField(dto,Lists.newArrayList("id","organisationName","name"));
        if(result != null){
            System.out.println(JsonTools.getJson(result));
        }else{
            System.out.println("OK");
        }
        System.out.println(AppUtils.containsName(AppEnum.Gender.class, "MALE"));
    }
}
