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
            "Canada");

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

        /**
         * check current location
         */
        int countryId = teacherAddress.getCountryId();
        if (BAD_LOCATION_IDS.contains(countryId)) {
            failTypes.add(AutoFailType.LOCATION_LIMIT);
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

    /**
     * 将这些国家和地区的解析成系统中对应的 id
     * Africa,
     * Middle-East Asia,
     * Russia,
     * Mongolia,
     * Myanmar,
     * Nepal,
     * Oceania,
     * South America,
     * Cambodia
     */
    private static final List<Integer> BAD_LOCATION_IDS = Lists.newArrayList(
            707747,
            34053,
            158276,
            209496,
            136619,
            152327,
            294733,
            267024,
            2313695,
            1347289,
            244582,
            268462,
            693929,
            729704,
            879946,
            739118,
            767478,
            846798,
            871967,
            870884,
            873838,
            898999,
            1332904,
            1449737,
            1443397,
            1455313,
            1473805,
            1522942,
            1475864,
            1520984,
            1522354,
            2707848,
            1455406,
            1680211,
            1698631,
            1700606,
            1712301,
            2049567,
            2219828,
            158786,
            2300436,
            2281974,
            2228241,
            2274352,
            2290865,
            2707885,
            2295626,
            2228292,
            2313645,
            2456155,
            2324127,
            2395056,
            2490751,
            739113,
            2718218,
            2729344,
            152224,
            612533,
            729704,
            1187303,
            1165986,
            1123434,
            1306895,
            1407781,
            1422563,
            1823823,
            2049400,
            2226578,
            2304105,
            2396873,
            65,
            2676834,
            54757,
            68753,
            612527,
            244579,
            788794,
            1865698,
            898939,
            937657,
            1347088,
            1474051,
            1821554,
            1700561,
            1821611,
            1821593,
            1712298,
            1520467,
            2047324,
            1865736,
            2674166,
            2228184,
            2393882,
            2396725,
            2444171,
            2672914,
            2674140,
            47708,
            158873,
            170771,
            290997,
            579390,
            716442,
            789722,
            870792,
            899680,
            2047430,
            1831905,
            892427,
            2295175,
            2609590,
            2614218,
            2067063,
            1518605,
            1487893,
            1765628,
            1340015
    );

}