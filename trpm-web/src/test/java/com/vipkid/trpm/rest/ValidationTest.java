package com.vipkid.trpm.rest;

import com.vipkid.trpm.entity.app.AppEnum;
import com.vipkid.trpm.util.AppUtils;

public class ValidationTest {

    public static void main(String[] args) {
        /*
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
        */
        System.out.println(AppUtils.containsName(AppEnum.Gender.class, "MALE1"));
        
    }
}
