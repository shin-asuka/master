package com.vipkid.recruitment.event.analysis;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.amazonaws.util.json.Jackson;
import com.vipkid.email.template.TemplateUtils;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.Teacher;
import org.apache.commons.lang.StringUtils;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Created by zhangzhaojun on 2016/12/8.
 */

public class EmailTemplateTools {
    
    private static Logger logger = LoggerFactory.getLogger(EmailTemplateTools.class);

    private static JsonNode JSON = null;

    private static String TEMPLETE = null;

    static{
        try {
            String json = TemplateUtils.readTemplatePath("data/interview.json").toString();
            JSON = Jackson.fromJsonString(json,JsonNode.class);
        }catch(Exception e){
            logger.error("请检查文件：1.data/interview.json 是否存在  2.文件内容是否符合json规范 "+e.getMessage(),e);
        }
    }

    /**
     * 读取Json节点
     * @Author:ALong (ZengWeiLong)
     */
    public static JsonNode readJsonContent(String firstName,Object value){
        try{
            if(JSON == null){
                String json = TemplateUtils.readTemplatePath("data/interview.json").toString();
                JSON = Jackson.fromJsonString(json,JsonNode.class);
            }
            return JSON.get(firstName).get(value.toString());
        }catch(Exception e){
            logger.error("请检查文件：1.data/interview.json 是否存在  2.文件内容是否符合json规范 3.所查询的节点是否存在:"+e.getMessage());
        }
        return null;
    }

    /**
     *  替换模板<br/>
     */
    public static String replaceTemplate(String content, Map<String, String> map) {
        if(StringUtils.isBlank(content)){
            return "";
        }
        if(map != null && map.size() > 0){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                content = content.replace("{{" + entry.getKey().trim() + "}}", entry.getValue() == null ? "":entry.getValue());
            }
        }
        return content;
    }

    /**
     * 读取模板内容
     */
    public static String readTemplate() {
        try{
            if(TEMPLETE == null){
                InputStream is = EmailTemplateTools.class.getClassLoader().getResourceAsStream("template/interviewPass.html");
                StringBuilder result = new StringBuilder("");
                BufferedReader br = new BufferedReader(new InputStreamReader(is, Charsets.UTF_8));// 构造一个BufferedReader类来读取文件
                String s = null;
                while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
                    result.append(s);
                }
                br.close();
                TEMPLETE = result.toString();
            }
            return TEMPLETE;
        }catch(Exception e){
            logger.error("请检查文件：1.templete/interviewPass.html 是否存在:"+e.getMessage(),e);
        }
        return null;
    }

    public  static String readyContent(TeacherApplication teacherApplication,Teacher teacher){
        if(teacherApplication == null){
            return null;
        }


        Map<String,String> map = Maps.newHashMap();
        map.put("contractURL", teacherApplication.getContractUrl());
        map.put("teacherName", teacher.getRealName());
        int preScore = 0;
        JsonNode json = readJsonContent("accent",teacherApplication.getAccent());
        if(json != null){
            map.put("accent-label",json.get("label").asText());
            map.put("accent-score",json.get("score").asText());
            preScore += json.get("score").asInt();
        }
        json = readJsonContent("phonics",teacherApplication.getPhonics());
        if(json != null){
            map.put("phonics-label",json.get("label").asText());
            map.put("phonics-score",json.get("score").asText());
            preScore += json.get("score").asInt();
        }
        json = readJsonContent("positive",teacherApplication.getPositive());
        if(json != null){
            map.put("positive-label",json.get("label").asText());
            map.put("positive-score",json.get("score").asText());
            preScore += json.get("score").asInt();
        }
        map.put("pre-score",preScore+"");

        int demoScore = 0;
        json = readJsonContent("timeManagementScore", teacherApplication.getTimeManagementScore());
        if(json != null){
            map.put("timeManagementScore-comments", json.get("comments").asText());
            map.put("timeManagementScore-feedback", json.get("feedback").asText());
            map.put("timeManagementScore-score", json.get("score").asText());
            demoScore += json.get("score").asInt();
        }
        json = readJsonContent("lessonObjectivesScore", teacherApplication.getLessonObjectivesScore());
        if(json != null){
            map.put("lessonObjectivesScore-comments", json.get("comments").asText());
            map.put("lessonObjectivesScore-feedback", json.get("feedback").asText());
            map.put("lessonObjectivesScore-score", json.get("score").asText());
            demoScore += json.get("score").asInt();
        }
        json = readJsonContent("teachingMethod", teacherApplication.getTeachingMethodScore());
        if(json != null){
            map.put("teachingMethod-comments", json.get("comments").asText());
            map.put("teachingMethod-feedback", json.get("feedback").asText());
            map.put("teachingMethod-score", json.get("score").asText());
            demoScore += json.get("score").asInt();
        }
        json = readJsonContent("preparationPlanningScore", teacherApplication.getPreparationPlanningScore());
        if(json != null){
            map.put("preparationPlanningScore-comments", json.get("comments").asText());
            map.put("preparationPlanningScore-feedback", json.get("feedback").asText());
            map.put("preparationPlanningScore-score", json.get("score").asText());
            demoScore += json.get("score").asInt();
        }
        json = readJsonContent("englishLanguageScore", teacherApplication.getEnglishLanguageScore());
        if(json != null){
            map.put("englishLanguageScore-comments", json.get("comments").asText());
            map.put("englishLanguageScore-feedback", json.get("feedback").asText());
            map.put("englishLanguageScore-score", json.get("score").asText());
            demoScore += json.get("score").asInt();
        }
        json = readJsonContent("interactionRapportScore", teacherApplication.getInteractionRapportScore());
        if(json != null){
            map.put("interactionRapportScore-comments", json.get("comments").asText());
            map.put("interactionRapportScore-feedback", json.get("feedback").asText());
            map.put("interactionRapportScore-score", json.get("score").asText());
            demoScore += json.get("score").asInt();
        }
        json = readJsonContent("studentOutputScore", teacherApplication.getStudentOutputScore());
        if(json != null){
            map.put("studentOutputScore-comments", json.get("comments").asText());
            map.put("studentOutputScore-feedback", json.get("feedback").asText());
            map.put("studentOutputScore-score", json.get("score").asText());
            demoScore += json.get("score").asInt();
        }
        json = readJsonContent("appearanceScore", teacherApplication.getAppearanceScore());
        if(json != null){
            map.put("appearanceScore-comments", json.get("comments").asText());
            map.put("appearanceScore-feedback", json.get("feedback").asText());
            map.put("appearanceScore-score", json.get("score").asText());
            demoScore += json.get("score").asInt();
        }
        json = readJsonContent("engaged", teacherApplication.getEngaged());
        if(json != null){
            map.put("engaged-comments", json.get("comments").asText());
            map.put("engaged-feedback", json.get("feedback").asText());
            map.put("engaged-score", json.get("score").asText());
            demoScore += json.get("score").asInt();
        }
        map.put("demo-score",demoScore+"");
        map.put("all-score", (preScore+demoScore)+"");
        map.put("basePay", teacherApplication.getBasePay()+"");
        return replaceTemplate(readTemplate(), map);
    }
}

