package com.vipkid.recruitment.basicinfo.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
import com.vipkid.recruitment.entity.TeachingExperience;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;

public class AutoFailProcessor {

    private boolean isFailed = false;
    private List<String> failReasons;
    private List<AutoFailType> failTypes;

    private Teacher teacher;
    private TeacherAddress teacherAddress;
    private List<TeachingExperience> experiences;

    private static final float WORK_HOUR_LIMIT = 500;
    private static final List<String> QUALIFIED_DEGREES = Lists.newArrayList(
            "PHD",
            "MASTERS",
            "BACHELORS"
    );

    private static final List<String> PREFERRED_NATIONALITY = Lists.newArrayList(
            "United States",
            "Canada",
            "Puerto Rico",
            "Guam",
            "U.S. Virgin Islands",
            "American Samoa"
    );

    public AutoFailProcessor(Teacher teacher, List<TeachingExperience> experiences, TeacherAddress teacherAddress) {
        this.teacher = teacher;
        this.experiences = experiences;
        this.teacherAddress = teacherAddress;
        failReasons = Lists.newArrayList();
        failTypes = Lists.newArrayList();

    }

    public AutoFailProcessor process() {
        float totalWorkHours = getTotalWorkHours(experiences);
        /**
         * check work hours
         */
        if (totalWorkHours < WORK_HOUR_LIMIT) {
            failTypes.add(AutoFailType.WORK_HOUR_LIMIT);
        }

        /**
         * check nationality
         */
        String nationality = teacher.getCountry();
        if (!PREFERRED_NATIONALITY.contains(nationality)) {
            failTypes.add(AutoFailType.NATIONALITY_LIMIT);
        }

        /**
         * check highest degree
         */
        String highestDegree = teacher.getHighestLevelOfEdu();
        if (!QUALIFIED_DEGREES.contains(highestDegree.toUpperCase())) {
            failTypes.add(AutoFailType.DEGREE_LIMIT);
        }

        failReasons = Lists.transform(failTypes, input -> input.getTip());

        if (failTypes.size() > 0) {
            isFailed = true;
        }

        return this;
    }

    public static float getTotalWorkHours(List<TeachingExperience> experiences) {
        float totalWorkHours = 0;
        if (CollectionUtils.isNotEmpty(experiences)) {
            for (TeachingExperience experience : experiences) {
                float workHours = experience.getTotalHours();
                if (workHours > 0) {
                    totalWorkHours += workHours;
                }
            }
        }
        return totalWorkHours;
    }

    public boolean isFailed() {
        return isFailed;
    }

    public List<AutoFailType> getFailTypes() {
        return failTypes;
    }

    public List<String> getFailReasons() {
        return failReasons;
    }

}