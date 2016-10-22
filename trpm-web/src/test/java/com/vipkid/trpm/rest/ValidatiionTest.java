package com.vipkid.trpm.rest;

import org.community.tools.JsonTools;

import com.vipkid.rest.dto.TeachingExperienceDto;
import com.vipkid.rest.validation.ValidationUtils;
import com.vipkid.rest.validation.tools.Result;

public class ValidatiionTest {

    public static void main(String[] args) {
        TeachingExperienceDto dto = new TeachingExperienceDto();
        dto.setOrganisationName("");
        Result result = ValidationUtils.checkForField(dto);
        if(result != null){
            System.out.println(JsonTools.getJson(result));
        }
    }
}
