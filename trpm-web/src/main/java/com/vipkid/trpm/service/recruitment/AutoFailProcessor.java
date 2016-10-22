package com.vipkid.trpm.service.recruitment;

import java.util.List;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherAddress;
import com.vipkid.trpm.entity.TeachingExperience;

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

        private static final List<String> BAD_LOCATIONS = Lists.newArrayList(
                "Africa",
                "Middle-East Asia",
                "Russia",
                "Mongolia",
                "Myanmar",
                "Nepal",
                "Oceania",
                "South America",
                "Cambodia"
        );

        private static final List<Integer> BAD_LOCATION_IDS = Lists.newArrayList(

        );

        private static final List<String> PREFERRED_NATIONALITY = Lists.newArrayList(
                "American",
                "Canadian");

        public AutoFailProcessor(Teacher teacher, List<TeachingExperience> experiences, TeacherAddress teacherAddress) {
            this.teacher = teacher;
            this.experiences = experiences;
            this.teacherAddress = teacherAddress;
        }

        public AutoFailProcessor process() {
            float totalWorkHours = getTotalWorkHours(experiences);
            /**
             * check work hours
             */
            if(totalWorkHours < WORK_HOUR_LIMIT) {
                failTypes.add(AutoFailType.WORK_HOUR_LIMIT);
            }

            /**
             * check nationality
             */
            String nationality = teacher.getCountry();
            if(!PREFERRED_NATIONALITY.contains(nationality)) {
                failTypes.add(AutoFailType.NATIONALITY_LIMIT);
            }

            /**
             * check highest degree
             */
            String highestDegree = teacher.getHighestLevelOfEdu();
            if(!QUALIFIED_DEGREES.contains(highestDegree.toUpperCase())) {
                failTypes.add(AutoFailType.DEGREE_LIMIT);
            }

            /**
             * check current location
             */
            int countryId = teacherAddress.getCountryId();
            if(BAD_LOCATION_IDS.contains(countryId)) {
                failTypes.add(AutoFailType.LOCATION_LIMIT);
            }

            failReasons = Lists.transform(failTypes, input -> input.getTip());

            if(failTypes.size() > 0) {
                isFailed = true;
            }

            return this;
        }

        public static float getTotalWorkHours(List<TeachingExperience> experiences) {
            float totalWorkHours = 0;
            if(CollectionUtils.isNotEmpty(experiences)) {
                for(TeachingExperience experience : experiences) {
                    float workHours = experience.getTotalHours();
                    if(workHours > 0) {
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