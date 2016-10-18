package com.vipkid.trpm.util;

import java.util.Map;

import com.google.api.client.util.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig;
import com.vipkid.email.templete.TempleteUtils;
import com.vipkid.trpm.entity.Teacher;

public class EmailUtils {

    public static void sendEmail4UndoFail(Teacher teacher) {
        Map<String, String> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherName", teacher.getRealName());
        Map<String, String> emailMap = new TempleteUtils().readTemplete("BasicInfoPass.html", paramsMap, "BasicInfoPassTitle.html");
        new EmailEngine().addMailPool(teacher.getEmail(), emailMap, EmailConfig.EmailFormEnum.TEACHVIP);
    }
}
