package com.vipkid.portal.bookings.service;


import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vipkid.dataSource.annotation.Slave;
import com.vipkid.enums.OnlineClassEnum.ClassStatus;
import com.vipkid.enums.OnlineClassEnum.ClassType;
import com.vipkid.enums.OnlineClassEnum.CourseType;
import com.vipkid.enums.TeacherPageLoginEnum.LoginType;
import com.vipkid.http.service.ScalperService;
import com.vipkid.portal.bookings.constant.BookingsResult;
import com.vipkid.portal.bookings.entity.*;
import com.vipkid.rest.portal.model.ClassroomDetail;
import com.vipkid.rest.service.TeacherPageLoginService;
import com.vipkid.rest.utils.ClassroomUtils;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.AuditCategory;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.constant.ApplicationConstant.PeakTimeType;
import com.vipkid.trpm.constant.ApplicationConstant.RedisConstants;
import com.vipkid.trpm.dao.AuditDao;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.PeakTimeDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.PeakTime;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.service.portal.TeacherService;
import com.vipkid.trpm.util.CookieUtils;
import com.vipkid.trpm.util.DateUtils;
import com.vipkid.trpm.util.FilesUtils;
import com.vipkid.trpm.util.IpUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.community.config.PropertyConfigurer;
import org.community.http.client.HttpClientProxy;
import org.community.tools.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.vipkid.trpm.util.DateUtils.*;

/**
 * Created by liuguowen on 2016/12/15.
 */
@Service
public class BookingsService {

    private static final Logger logger = LoggerFactory.getLogger(BookingsService.class);

    /* 老师默认必须放置的 PeakTime TimeSolt 数量 */
    private static final int PEAKTIME_TIMESLOT_DEFAULT_COUNT = 15;
    /* TimeSolt 创建时的默认锁定时间，单位：秒 */
    private static final int LOCK_TIMESLOT_EXPIRED = 65;
    /* 1 小时的毫秒 */
    private static final long ONE_HOUR_MILLIS = 1 * 60 * 60 * 1000;
    /* 半小时的毫秒 */
    private static final long HALF_HOUR_MILLIS = 30 * 60 * 1000;
    /* 早九点的索引数 */
    private static final int NINE_OF_AM = 17;
    /* 晚九点半的索引数 */
    private static final int NINE_AND_HALF_OF_PM = 44;
    /* 请求默认超时时间 */
    private static final int DEFAULT_TIMEOUT = 15 * 1000;

    private static final RequestConfig DEFAULT_REQUEST_CONFIG =
            RequestConfig.custom().setConnectionRequestTimeout(DEFAULT_TIMEOUT)
                    .setConnectTimeout(DEFAULT_TIMEOUT).setSocketTimeout(DEFAULT_TIMEOUT).build();

    private static final int SCALPER_SUCCESS_CODE = 0;

    private static final int SCALPER_REFUSED_CODE = 4000;

    @Autowired
    private OnlineClassDao onlineClassDao;

    @Autowired
    private PeakTimeDao peakTimeDao;

    @Autowired
    private AuditDao auditDao;

    @Autowired
    private RedisProxy redisProxy;

    @Autowired
    private TeacherPageLoginService teacherPageLoginService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ScalperService scalperService;

    private static final String scalperServerAddress =
            PropertyConfigurer.stringValue("scalper.serverAddress");

    private static  List<String> courseCancel = Lists.newArrayList();
    static{
        courseCancel.add("GA");
        courseCancel.add("Major Course");
        courseCancel.add("Major Course 2016");
        courseCancel.add("Assessment");
        courseCancel.add("Assessment2");
    }

    /**
     * 计算一天中的所有 TimePoint，每半小时为一个单位
     *
     * @return
     */
    public List<TimePoint> getTimePointsOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        List<TimePoint> resultList = Lists.newLinkedList();
        IntStream.range(0, HALFHOUR_OF_DAY).forEach(n -> {
            resultList.add(new TimePoint(calendar.getTime()));
            calendar.add(Calendar.MINUTE, MINUTE_OF_HALFHOUR);
        });

        return resultList;
    }

    /**
     * 根据星期偏移量计算当前礼拜包含的日期
     *
     * @param offsetOfWeek
     * @return
     */
    public List<Date> getDaysOfWeek(int offsetOfWeek) {
        Calendar calendar = Calendar.getInstance();
        if (0 != offsetOfWeek) {
            calendar.add(Calendar.DATE, offsetOfWeek * DAY_OF_WEEK);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1);
        }

        List<Date> daysOfWeek = Lists.newLinkedList();
        IntStream.rangeClosed(0, DAY_OF_WEEK).forEach(n -> {
            daysOfWeek.add(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        });

        return daysOfWeek;
    }

    /**
     * 计算整个星期的TimeSlot
     *
     * @param daysOfWeek
     * @param timePoints
     * @return
     */
    public List<TimeSlot> getTimeSlotsOfWeek(List<Date> daysOfWeek, List<TimePoint> timePoints) {
        List<TimeSlot> timeSlotsOfWeek = Lists.newLinkedList();

        timePoints.stream().forEach((timePoint) -> {
            daysOfWeek.stream().forEach((day) -> {
                Calendar calendarDay = Calendar.getInstance();
                calendarDay.setTime(day);

                /* 设置小时和分钟 */
                Calendar calendarTime = Calendar.getInstance();
                calendarTime.setTimeInMillis(timePoint.getMillis());

                calendarDay.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
                calendarDay.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
                calendarDay.set(Calendar.SECOND, 0);

                timeSlotsOfWeek.add(new TimeSlot(calendarDay.getTime()));
            });
        });

        return timeSlotsOfWeek;
    }

    /**
     * 计算整个星期的 ZoneTime
     *
     * @param timeSlots
     * @param timezone
     * @return
     */
    public List<ZoneTime> getZoneTimesOfWeek(List<TimeSlot> timeSlots, String timezone) {
        List<ZoneTime> zoneTimesOfWeek = Lists.newLinkedList();
        timeSlots.stream().forEach((timeSlot) -> {
            zoneTimesOfWeek.add(new ZoneTime(timeSlot, timezone));
        });
        return zoneTimesOfWeek;
    }

    /**
     * 计算 schedule 表格
     *
     * @param daysOfWeek
     * @param timezone
     * @param peakTimeMap
     * @param courseType
     * @param onlineClassesMap
     * @return
     */
    public Map<TimePoint, List<TimeSlot>> scheduleTable(List<Date> daysOfWeek, String timezone,
                                                        Map<String, String> peakTimeMap, String courseType,
                                                        Map<String, Map<String, Object>> onlineClassesMap) {
        Comparator<TimePoint> comparator =
                (first, second) -> Long.valueOf(first.getMillis() - second.getMillis()).intValue();
        Map<TimePoint, List<TimeSlot>> scheduleTable = Maps.newTreeMap(comparator);

        List<TimePoint> timePointsOfDay = getTimePointsOfDay();
        List<TimeSlot> timeSlotsOfWeek = getTimeSlotsOfWeek(daysOfWeek, timePointsOfDay);
        List<ZoneTime> zoneTimesOfWeek = getZoneTimesOfWeek(timeSlotsOfWeek, timezone);

        // 更新每个TimeSlot的ZoneTime列表
        timeSlotsOfWeek.stream().forEach((timeSlot) -> {

            zoneTimesOfWeek.stream().forEach((zoneTime) -> {
                if (zoneTime.getLocalTime().equals(timeSlot.getLocalTime())) {
                    zoneTime.setOnlineClassMap(onlineClassesMap.get(zoneTime.getFormatToBeiJing()));
                    timeSlot.getZoneTime().add(zoneTime);

                    /* 设置当前timeSlot是否显示 */
                    timeSlot.setShow(isShow(zoneTime.getFormatToBeiJing(), courseType));

                    /* 设置当前timeSlot是否已过期 */
                    timeSlot.setExpired(zoneTime.getDateFromBeiJing().before(new Date()));

                    /* 设置当前timeSlot属性 */
                    String peakType = peakTimeMap.get(zoneTime.getFormatToBeiJing());
                    setTimeSlotProperties(timeSlot, peakType);
                }
            });

//            if (CollectionUtils.isEmpty(timeSlot.getZoneTime())) {
//                ZoneTime zoneTime = new ZoneTime();
//                zoneTime.setLocalTime(timeSlot.getLocalTime());
//
//                LocalDateTime localDateTimeBeiJing = LocalDateTime.parse(zoneTime.getLocalTime(), FMT_YMD_HMA_US)
//                        .atZone(ZoneId.of(timezone)).withZoneSameInstant(SHANGHAI).toLocalDateTime();
//                zoneTime.setFormatToBeiJing(localDateTimeBeiJing.format(FMT_YMD_HMS));
//                zoneTime.setBeijingTime(localDateTimeBeiJing.format(FMT_YMD_HMA_US));
//                zoneTime.setDateFromBeiJing(Date.from(localDateTimeBeiJing.atZone(SHANGHAI).toInstant()));
//
//                timeSlot.getZoneTime().add(zoneTime);
//                timeSlot.setShow(isShow(zoneTime.getFormatToBeiJing(), courseType));
//
//                /* 设置当前timeSlot是否已过期 */
//                timeSlot.setExpired(zoneTime.getDateFromBeiJing().before(new Date()));
//
//                /* 设置当前timeSlot属性 */
//                String peakType = peakTimeMap.get(zoneTime.getFormatToBeiJing());
//                setTimeSlotProperties(timeSlot, peakType);
//            }

        });

        // 设置TimePoint和TimeSlot列表的映射关系
        timePointsOfDay.stream().forEach((timePoint) -> {

            List<TimeSlot> groupTimeSlots = Lists.newLinkedList();

            timeSlotsOfWeek.stream().forEach((timeSlot) -> {
                if (StringUtils.contains(timeSlot.getLocalTime(), timePoint.getName())) {
                    groupTimeSlots.add(timeSlot);
                }
            });

            /* 过滤不在上课时间段的数据 */
            if (0 != groupTimeSlots.stream().filter(timeSlot -> timeSlot.isShow()).count()) {
                scheduleTable.put(timePoint, groupTimeSlots);
            }

        });

        return scheduleTable;
    }

    /**
     * 设置 TimeSlot 属性
     *
     * @param timeSlot
     * @param peakType
     */
    public void setTimeSlotProperties(TimeSlot timeSlot, String peakType) {
        if (timeSlot.isShow() && !StringUtils.isEmpty(peakType)
                && !StringUtils.contains(peakType, PeakTimeType.NORMALTIME)) {
            timeSlot.setPeakTime(true);
        }
    }

    /**
     * 北京的上课时间为早9点到晚9点半
     *
     * @return List<String>
     */
    public List<String> getClassTimes() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        List<String> resultList = Lists.newLinkedList();
        IntStream.range(0, HALFHOUR_OF_DAY).forEach(n -> {
            if (n > NINE_OF_AM && n < NINE_AND_HALF_OF_PM) {
                resultList.add(formatTo(calendar.toInstant(), FMT_HMS));
            }
            calendar.add(Calendar.MINUTE, MINUTE_OF_HALFHOUR);
        });

        return resultList;
    }

    /**
     * 判断是否在上课时间段之内，是则显示；Practicum 课默认全部展示。
     *
     * @param formatToBeiJing
     * @param courseType
     * @return boolean
     */
    public boolean isShow(String formatToBeiJing, String courseType) {
        if (CourseType.isMajor(courseType)) {
            long count = getClassTimes().stream().filter(time -> -1 != formatToBeiJing.indexOf(time)).count();
            return (0 != count) ? true : false;
        }
        return true;
    }

    /**
     * 查询开始时间到结束时间之前的 PeakTime 列表
     *
     * @param fromTime
     * @param toTime
     * @return
     */
    public Map<String, String> getPeakTimeMap(Date fromTime, Date toTime) {
        Map<String, String> peakTimeMap = Maps.newHashMap();
        List<PeakTime> peakTimeList = peakTimeDao.findByFromAndToTime(fromTime, toTime);

        if (CollectionUtils.isNotEmpty(peakTimeList)) {
            peakTimeList.stream().forEach((peakTime) -> {
                Instant instant = peakTime.getTimePoint().toInstant();
                peakTimeMap.put(formatTo(instant, FMT_YMD_HMS), peakTime.getType());
            });
        }
        return peakTimeMap;
    }

    /**
     * 查询老师的Schedule列表
     *
     * @param teacherId
     * @param fromTime
     * @param toTime
     * @param timezone
     * @return List<Map<String, Object>>
     */
    public Map<String, Map<String, Object>> getTeacherScheduleMap(long teacherId, Date fromTime, Date toTime,
                    String timezone) {
        List<Map<String, Object>> teacherScheduleList =
                        onlineClassDao.findByTeacherIdWithFromAndToTime(teacherId, fromTime, toTime, timezone);
        logger.info("Teacher id: {}, Schedule size: {}", teacherId, teacherScheduleList.size());

        /* 根据北京时间构造 Map 对象 */
        Map<String, Map<String, Object>> teacherScheduleMap = Maps.newHashMap();

        if (org.springframework.util.CollectionUtils.isEmpty(teacherScheduleList)) {
            return teacherScheduleMap;
        }

        /* 查询需要显示的 INVALID 课程列表 */
        List<Map<String, Object>> invalidScheduleList = onlineClassDao.findInvalidBy(teacherId, fromTime, toTime, timezone);


        /* 查询 24 小时的课程列表 */
        List<String> onlineClassIds = teacherScheduleList.stream().map(map -> String.valueOf(map.get("id"))).collect(Collectors.toList());


        List<String> idsFor24Hour = get24Hours(teacherId, onlineClassIds);


        for (Map<String, Object> teacherSchedule : teacherScheduleList) {
            long onlineClassId = (Long) teacherSchedule.get("id");

            /**
             * 由于查询的列表中包含除了 REMOVED 之外所有的课程，因此需要过滤不用于显示的 INVALID 记录；
             *
             * 业务场景：换课时，只显示第一个被换老师的 INVALID 记录。
             */
            String status = (String) teacherSchedule.get("status");
            if (ClassStatus.isInvalid(status)) {
                boolean isFilter = true;

                for (Map<String, Object> invalidSchedule : invalidScheduleList) {
                    if (onlineClassId == (Long) invalidSchedule.get("id")) {
                        isFilter = false;
                        break;
                    }
                }

                if (isFilter) {
                    continue;
                }
            }

            /* 设置课程类型 */
            int classType = (Integer) teacherSchedule.get("classType");
            if (ClassType.PRACTICUM.val() == classType) {
                teacherSchedule.put("isPracticum", true);
            } else {
                teacherSchedule.put("isPracticum", false);
            }

            /* 设置是否是 24 小时的课程 */
            if (idsFor24Hour.stream().anyMatch(id -> id.equals(String.valueOf(onlineClassId)))) {
                teacherSchedule.put("is24Hour", true);
            } else {
                teacherSchedule.put("is24Hour", false);
            }

            /* 设置 OPEN 课程的学生数量 */
            setStudentCount(teacherSchedule);

            /* 按优先级过滤课程 */
            Date scheduledDateTime = (Date) teacherSchedule.get("scheduledDateTime");
            String scheduleKey = formatTo(scheduledDateTime.toInstant(), FMT_YMD_HMS);
            setSchedulePriority(teacherScheduleMap, scheduleKey, teacherSchedule);
        }

        return teacherScheduleMap;
    }

    /**
     * 设置OPEN课的学生数量
     *
     * @param teacherSchedule
     */
    public void setStudentCount(Map<String, Object> teacherSchedule) {
        /* 获取TeacherSchedule的状态 */
        String status = (String) teacherSchedule.get("status");
        if (ClassStatus.isOpen(status)) {
            long onlineClassId = (Long) teacherSchedule.get("id");
            teacherSchedule.put("studentCount", onlineClassDao.countStudentByOnlineClassId(onlineClassId));
            //open课随机拿一个学生的ID
            int studentId = onlineClassDao.getRandomStudentFromOpenCourse(onlineClassId);
            teacherSchedule.put("studentId", studentId);
        }
    }

    /**
     * 设置 TeacherSchedule 显示的优先级
     *
     * @param teacherScheduleMap
     * @param scheduleKey
     * @param teacherSchedule
     */
    public void setSchedulePriority(Map<String, Map<String, Object>> teacherScheduleMap, String scheduleKey,
                                    Map<String, Object> teacherSchedule) {
        boolean isReplaced = false;
        Timestamp bookDateTimestamp = (Timestamp) teacherSchedule.get("bookDateTime");
        if(bookDateTimestamp != null) {
            Calendar bookDateTime = Calendar.getInstance();
            bookDateTime.setTimeInMillis(bookDateTimestamp.getTime());
            teacherSchedule.put("bookDateTime",bookDateTime);
        }

        ClassroomUtils.buildAsyncLessonSN(teacherSchedule);

        /* 获取新的 TeacherSchedule 的状态 */
        String newStatus = (String) teacherSchedule.get("status");
        String newFinishType = (String) teacherSchedule.get("finishType");

        if (teacherScheduleMap.containsKey(scheduleKey)) {
            /* 获取已有的 TeacherSchedule 的状态 */
            Map<String, Object> exsitTeacherSchedule = teacherScheduleMap.get(scheduleKey);
            String oldStatus = (String) exsitTeacherSchedule.get("status");
            String oldFinishType = (String) exsitTeacherSchedule.get("finishType");

            /* 增加新类型替换 */
            /* 如果老状态为 FINISHED */
            if (ClassStatus.isFinished(oldStatus)) {
                /* 新状态为 BOOKED，则替换 */
                if (ClassStatus.isBooked(newStatus)) {
                    isReplaced = true;
                }
                /* 新状态为 AVAILABLE，老的 FinishType 为 StudentNoShow24 则替换 */
                if (ClassStatus.isAvailable(newStatus)
                        && ApplicationConstant.FinishType.isStudentNoShow24(oldFinishType)) {
                    isReplaced = true;
                }
                /* 新状态为 FINISHED，老的 FinishType 为 StudentNoShow 则替换 */
                if (ClassStatus.isFinished(newStatus)
                        && ApplicationConstant.FinishType.isStudentNoShow(oldFinishType)) {
                    isReplaced = true;
                }

            }

            /* 如果老状态为 EXPIRED，新状态为 FINISHED，或新状态为 BOOKED，则替换 */
            if (ClassStatus.isExpired(oldStatus)) {
                if (ClassStatus.isFinished(newStatus) || ClassStatus.isBooked(newStatus)) {
                    isReplaced = true;
                }
            }

            /* 如果老状态为 CANCELED，或 REMOVED，则替换 */
            if (ClassStatus.isCanceled(oldStatus) || ClassStatus.isRemoved(oldStatus)) {
                isReplaced = true;
            }

            /* 如果老状态为 AVAILABLE */
            if (ClassStatus.isAvailable(oldStatus)) {
                /* 新状态为 BOOKED，则替换 */
                if (ClassStatus.isBooked(newStatus)) {
                    isReplaced = true;
                }
                /* 新 FinishType 为 StudentNoShow，则替换 */
                if (ApplicationConstant.FinishType.isStudentNoShow(newFinishType)) {
                    isReplaced = true;
                }
            }

            /* 如果老状态为 INVALID，新状态不为 INVALID，则替换 */
            if (ClassStatus.isInvalid(oldStatus) && !ClassStatus.isInvalid(newStatus)) {
                isReplaced = true;
            }
        } else {
            /* 如果新状态不为 REMOVED，则替换 */
            if (!ClassStatus.isRemoved(newStatus)) {
                isReplaced = true;
            }
        }

        if (isReplaced) {
            String finishType= (String) teacherSchedule.get("finishType");
            if(ApplicationConstant.FinishType.TEACHER_CANCELLATION.toString().equalsIgnoreCase(finishType)
                    ||ApplicationConstant.FinishType.TEACHER_CANCELLATION_24H.toString().equalsIgnoreCase(finishType)
                    ||ApplicationConstant.FinishType.TEACHER_NO_SHOW_2H.equalsIgnoreCase(finishType)){
                teacherSchedule.put("status", ClassStatus.CANCELED.toString());
            }
            teacherScheduleMap.put(scheduleKey, teacherSchedule);
        }
    }

    /**
     * 处理 schedule 请求
     *
     * @param scheduledRequest
     * @param teacher
     * @return
     */
    @Slave
    public Map<String, Object> doSchedule(ScheduledRequest scheduledRequest, Teacher teacher) {
        String timezone = teacher.getTimezone();
        long teacherId = teacher.getId();

        Map<String, Object> modelMap = Maps.newHashMap();
        modelMap.put("timezone", timezone);

        int weekOffset = scheduledRequest.getWeekOffset();
        String courseType = scheduledRequest.getType();
        /* 获取日期所在星期 */
        List<Date> daysOfWeek = getDaysOfWeek(weekOffset);
        List<String> daysOfWeekString = Lists.newArrayList();
        for (Date date : daysOfWeek) {
            daysOfWeekString.add(DateUtils.formatDate(date, DateUtils.YYYY_MM_DD));
        }
        modelMap.put("daysOfWeek", daysOfWeek);
        modelMap.put("daysOfWeekString", daysOfWeekString);
        /* 查询的开始时间和结束时间 */
        Date fromTime = daysOfWeek.get(0), toTime = daysOfWeek.get(DAY_OF_WEEK);
        

        /* 计算Schedule表格 */
        Map<String, String> peakTimeMap = getPeakTimeMap(fromTime, toTime);
        Map<String, Map<String, Object>> onlineClassesMap =
                getTeacherScheduleMap(teacherId, fromTime, toTime, timezone);
        modelMap.put("scheduleTable", scheduleTable(daysOfWeek, timezone, peakTimeMap, courseType, onlineClassesMap));

        /* 设置页面显示日期 */
        modelMap.put("startDate", DateUtils.formatDate(daysOfWeek.get(0), DateUtils.YYYY_MM_DD));
        modelMap.put("endDate", DateUtils.formatDate(daysOfWeek.get(DAY_OF_WEEK - 1), DateUtils.YYYY_MM_DD));

        return modelMap;
    }

    /**
     * 查询 24 小时的课程
     *
     * @param teacherId
     * @param onlineClassIds
     * @return
     */
    public List<String> get24Hours(long teacherId, List<String> onlineClassIds) {
        try {
            Map<String, String> requestParams = Maps.newHashMap();
            requestParams.put("classIds", onlineClassIds.stream().collect(Collectors.joining(",")));

            Map<String, String> requestHeader = get24HoursRequestHeader(teacherId);

            String requestUrl =
                    ApplicationConstant.TEACHER_24HOUR_URL + "/api/service/public/24HourClass/filterByClass";
            logger.info("Get 24Hours Request Url: {}", requestUrl);

            String responseBody = HttpClientProxy.get(requestUrl, requestParams, requestHeader);
            logger.info("Get 24Hours Response:{}", responseBody);
            if (StringUtils.isBlank(responseBody)) {
                return Lists.newArrayList();
            }
            responseBody = StringTools.matchString(responseBody, "\\[(.*?)\\]", Pattern.CASE_INSENSITIVE, 1);
            return Arrays.asList(StringUtils.split(responseBody, ","));
        } catch (Exception e) {
            logger.error("HttpClientProxy err:", e);
            return Lists.newArrayList();
        }
    }

    /**
     * 处理 TimeSlot 创建逻辑
     *
     * @param teacher
     * @param scheduleTime
     * @param courseType
     * @return Map<String,Object>
     */
    public Map<String, Object> doCreateTimeSlot(Teacher teacher, String scheduleTime, String courseType) {
        Map<String, Object> modelMap = Maps.newHashMap();

        Timestamp scheduleDateTime = parseFrom(scheduleTime, FMT_YMD_HMS);

        String url = scalperServerAddress + "/createTimeSlot";
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("teacherId", teacher.getId());
        requestMap.put("isPracticum", CourseType.isPracticum(courseType));
        requestMap.put("scheduleTime", scheduleDateTime.toString());
        requestMap.put("fromIP", IpUtils.getRemoteIP());
        String returnData = scalperService.createTimeSlot(requestMap);
        if (returnData != null) {
            Map<String, Object> returnModel = (Map<String, Object>) JSONObject.parse(returnData);
            if (SCALPER_SUCCESS_CODE == (int) returnModel.get("code")) {
                Map<String, Object> data = (Map<String, Object>) returnModel.get("data");
                modelMap.put("onlineClassId", data.get("onlineClassId"));
                modelMap.put("classType", data.get("classType"));
                String timePoint = formatTo(scheduleDateTime.toInstant(), teacher.getTimezone(), FMT_HMA_US);
                modelMap.put("timePoint", timePoint);
                logger.info("create timeslot by scalper success, params:{}", requestMap.toString());
                return modelMap;
            } else if (returnModel.get("code")!=null && SCALPER_REFUSED_CODE == (int) returnModel.get("code")) {
                logger.info("create timeslot by scalper refused! ");
                modelMap.put("error", String.valueOf(returnModel.get("code")));
            } else {
                logger.info("create timeslot by scalper failed! code = {}", String.valueOf(returnModel.get("code")));
                modelMap.put("error", String.valueOf(returnModel.get("code")));
            }
        } else {
            logger.error("post url={} result = null", url);
            modelMap.put("error", BookingsResult.TIMESLOT_NOT_AVAILABLE);
        }
/*
        //验证当前时间是否已存在 OnlineClass
        if (canSetSchedule(teacher.getId(), scheduleDateTime)) {
            OnlineClass onlineClass = new OnlineClass();
            onlineClass.setClassType(ClassType.MAJOR.val());

            //设置课程的属性
            onlineClass.setTeacherId(teacher.getId());
            onlineClass.setScheduledDateTime(scheduleDateTime);

            onlineClass.setStatus(ClassStatus.AVAILABLE.name());
            onlineClass.setSerialNumber(Long.toString(scheduleDateTime.getTime()));

            //设置为课程开始前 1 小时
            onlineClass.setAbleToEnterClassroomDateTime(new Timestamp(scheduleDateTime.getTime() - ONE_HOUR_MILLIS));
            onlineClass.setLastEditDateTime(new Timestamp(System.currentTimeMillis()));

            //如果是 PRACTICUM 的课程，则需要指定 ClassType
            if (CourseType.isPracticum(courseType)) {
                onlineClass.setClassType(ClassType.PRACTICUM.val());

                //需要加锁，一次只处理一个请求
                final String key = "TP:LOCK:PRACTICUM:" + teacher.getId();
                try {
                    if (redisProxy.lock(key, LOCK_TIMESLOT_EXPIRED)) {
                        //验证 PRACTICUM 课程的这个时间点是否与MAJOR课时冲突
                        Timestamp plusHour = new Timestamp(scheduleDateTime.getTime() + HALF_HOUR_MILLIS);

                        //验证往后半小时的课程时间有没有跨天
                        LocalDateTime localDateTimeBeiJing = LocalDateTime.ofInstant(plusHour.toInstant(), SHANGHAI);
                        String formatToBeiJing = localDateTimeBeiJing.format(FMT_YMD_HMS);

                        if (!isShow(formatToBeiJing, courseType)) {
                            modelMap.put("error", BookingsResult.DISABLED_PLACE);
                            return modelMap;
                        }

                        //往前半小时只验证PRACTICUM的课程时间
                        Timestamp minusHour = new Timestamp(scheduleDateTime.getTime() - HALF_HOUR_MILLIS);
                        List<OnlineClass> tList =
                                onlineClassDao.findByTeacherIdAndScheduleDateTime(teacher.getId(), minusHour);

                        long count = tList.stream().filter((o) -> o.getClassType() == ClassType.PRACTICUM.val())
                                .filter((o) -> ClassStatus.isBooked(o.getStatus())
                                        || ClassStatus.isAvailable(o.getStatus()))
                                .count();

                        if (canSetSchedule(teacher.getId(), plusHour) && 0 == count) {
                            onlineClassDao.save(onlineClass);
                        } else {
                            modelMap.put("error", BookingsResult.DISABLED_PLACE);
                            return modelMap;
                        }
                    } else {
                        modelMap.put("error", BookingsResult.SYNC_DISABLED_PLACE);
                        return modelMap;
                    }
                } finally {
                    redisProxy.del(key);
                }
            } else {
                //验证 MAJOR 课程的这个时间点是否与 PRACTICUM 课时冲突
                Timestamp minusHour = new Timestamp(scheduleDateTime.getTime() - HALF_HOUR_MILLIS);
                List<OnlineClass> tList = onlineClassDao.findByTeacherIdAndScheduleDateTime(teacher.getId(), minusHour);

                //往前半小时只验证 PRACTICUM 的课程时间
                long count = tList.stream().filter((o) -> o.getClassType() == ClassType.PRACTICUM.val()).filter(
                        (o) -> ClassStatus.isBooked(o.getStatus()) || ClassStatus.isAvailable(o.getStatus()))
                        .count();

                if (0 == count) {
                    onlineClassDao.save(onlineClass);
                } else {
                    modelMap.put("error", BookingsResult.DISABLED_PLACE);
                    return modelMap;
                }
            }

            //记录操作日志
            Map<String, Object> replaceMap = Maps.newHashMap();
            replaceMap.put("teacherId", teacher.getId());
            replaceMap.put("onlineClassId", onlineClass.getId());

            Instant instant = Calendar.getInstance().toInstant();
            replaceMap.put("createTime", DateUtils.formatTo(instant, DateUtils.FMT_YMD_HMS));
            replaceMap.put("scheduleDatetime", onlineClass.getScheduledDateTime());

            String content = FilesUtils.readLogTemplate(AuditCategory.ONLINE_CLASS_CREATE, replaceMap);
            auditDao.saveAudit(AuditCategory.ONLINE_CLASS_CREATE, "INFO", content, teacher.getRealName(),
                    onlineClassDao, IpUtils.getRemoteIP());

            //返回结果
            String timePoint = formatTo(scheduleDateTime.toInstant(), teacher.getTimezone(), FMT_HMA_US);

            modelMap.put("onlineClassId", onlineClass.getId());
            modelMap.put("classType", onlineClass.getClassType());
            modelMap.put("timePoint", timePoint);
        } else {
            modelMap.put("error", BookingsResult.DISABLED_PLACE);
        }
*/
        return modelMap;
    }

    /**
     * 以加锁的方式创建 TimeSlot
     *
     * @param timeSlotCreateRequest
     * @param teacher
     * @return
     */
    public Map<String, Object> doCreateTimeSlotWithLock(TimeSlotCreateRequest timeSlotCreateRequest, Teacher teacher) {
        String scheduleTime = timeSlotCreateRequest.getScheduledDateTime();
        String courseType = timeSlotCreateRequest.getType();

        Timestamp scheduleDateTime = parseFrom(scheduleTime, FMT_YMD_HMS);
        final String key = "TP:LOCK:MAJOR:" + teacher.getId() + ":" + scheduleDateTime.getTime();

        if (redisProxy.lock(key, LOCK_TIMESLOT_EXPIRED)) {
            try {
                return doCreateTimeSlot(teacher, scheduleTime, courseType);
            } finally {
                redisProxy.del(key);
            }
        } else {
            Map<String, Object> modelMap = Maps.newHashMap();
            modelMap.put("error", BookingsResult.DISABLED_PLACE);
            return modelMap;
        }

    }

    /**
     * 判断老师的这个时间点能否设置 TimeSlot
     *
     * @param teacherId
     * @param t
     * @return
     */
    public boolean canSetSchedule(long teacherId, Timestamp t) {
        List<OnlineClass> tList = onlineClassDao.findByTeacherIdAndScheduleDateTime(teacherId, t);
        long count = tList.stream()
                .filter((o) -> ClassStatus.isBooked(o.getStatus()) || ClassStatus.isAvailable(o.getStatus()))
                .count();
        return (0 != count) ? false : true;
    }

    /**
     * 处理 TimeSlot 取消逻辑
     *
     * @return Map<String, Object>
     */
    public Map<String, Object> doCancelTimeSlot(TimeSlotCancelRequest timeSlotCancelRequest, Teacher teacher) {
        Map<String, Object> modelMap = Maps.newHashMap();

        String url = scalperServerAddress + "/cancelTimeSlot";
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("teacherId", teacher.getId());
        requestMap.put("onlineClassId", timeSlotCancelRequest.getOnlineClassId());
        requestMap.put("fromIP", IpUtils.getRemoteIP());
        // String returnData = WebUtils.postNameValuePair(url, requestMap);
        String returnData = scalperService.cancelTimeSlot(requestMap);
        if (returnData != null) {
            Map<String, Object> returnModel = (Map<String, Object>) JSONObject.parse(returnData);
            if (SCALPER_SUCCESS_CODE == (int) returnModel.get("code")) {
                Map<String, Object> data = (Map<String, Object>) returnModel.get("data");
                modelMap.put("onlineClassId", data.get("onlineClassId"));
                modelMap.put("status", ClassStatus.REMOVED.name());
                logger.info("cancel timeslot by scalper success, params:{}", requestMap.toString());
                return modelMap;
            } else if (returnModel.get("code")!=null && SCALPER_REFUSED_CODE == (int) returnModel.get("code")) {
                logger.info("cancel timeslot by scalper refused! ");
                modelMap.put("error", String.valueOf(returnModel.get("code")));
            } else {
                logger.info("cancel timeslot by scalper failed! code = {}", String.valueOf(returnModel.get("code")));
                modelMap.put("error", String.valueOf(returnModel.get("code")));
            }
        } else {
            logger.error("post url={} result = null", url);
            modelMap.put("error", BookingsResult.TIMESLOT_NOT_AVAILABLE);
        }
/*
        OnlineClass onlineClass = onlineClassDao.findById(timeSlotCancelRequest.getOnlineClassId());
        if (null == onlineClass) {
            modelMap.put("error", BookingsResult.ILLEGAL_ONLINECLASS);
        }

        //更新 OnlineClass 状态
        if (ClassStatus.isAvailable(onlineClass.getStatus())) {
            onlineClassDao.updateStatus(onlineClass.getId(), ClassStatus.REMOVED.name());

            //记录操作日志
            Map<String, Object> replaceMap = Maps.newHashMap();
            replaceMap.put("teacherId", teacher.getId());
            replaceMap.put("onlineClassId", onlineClass.getId());

            Instant instant = Calendar.getInstance().toInstant();
            replaceMap.put("createTime", DateUtils.formatTo(instant, DateUtils.FMT_YMD_HMS));
            replaceMap.put("scheduleDatetime", onlineClass.getScheduledDateTime());

            String content = FilesUtils.readLogTemplate(AuditCategory.ONLINE_CLASS_DELETE, replaceMap);
            auditDao.saveAudit(AuditCategory.ONLINE_CLASS_DELETE, "INFO", content, teacher.getRealName(),
                    onlineClassDao, IpUtils.getRemoteIP());

            modelMap.put("onlineClassId", onlineClass.getId());
            modelMap.put("status", ClassStatus.REMOVED.name());
        } else {
            modelMap.put("error", BookingsResult.TIMESLOT_NOT_AVAILABLE);
        }
*/
        return modelMap;
    }

    /**
     * 查询老师的某个日期所在星期的 PeakTime 总数
     *
     * @param scheduleDateTime
     * @param teacherId
     * @return int
     */
    public int totalPeakTime(Timestamp scheduleDateTime, long teacherId) {
        Date date = new Date(scheduleDateTime.getTime());

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(date);
        startCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        startCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(date);
        endCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        endCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        endCalendar.set(Calendar.MILLISECOND, 0);

        return peakTimeDao.countDaoByTeacherIdAndFromWithToTime(startCalendar.getTime(), endCalendar.getTime(),
                teacherId);
    }

    /**
     * 处理设置 24 小时
     *
     * @param set24HourRequest
     * @param teacher
     * @return
     */
    public Map<String, Object> doSet24Hours(Set24HourRequest set24HourRequest, Teacher teacher) {
        Map<String, Object> resultMap = Maps.newHashMap();

        List<OnlineClass> onlineClasses = onlineClassDao.findOnlineClasses(set24HourRequest.getOnlineClassIds());
        if (checkAnyInOneHour(onlineClasses)) {
            resultMap.put("error", BookingsResult.ONLINECLASS_IN_ONE_HOUR);
            return resultMap;
        }

        if (set24HourRequest.getClassType() == ClassType.MAJOR.val()) {
            final int weekOffset = set24HourRequest.getWeekOffset();

//            if (checkLess15TimeSlots(teacher.getId(), teacher.getTimezone(), weekOffset)) {
//                resultMap.put("error", BookingsResult.TIMESLOT_LESS_15);
//                return resultMap;
//            }
        }

        resultMap.put("result", set24Hours(set24HourRequest, teacher));
        return resultMap;
    }

    /**
     * 检查是否有课程的开始时间在 1 小时之内
     *
     * @return
     */
    public boolean checkAnyInOneHour(List<OnlineClass> onlineClasses) {
        Predicate<OnlineClass> predicate = onlineClass -> onlineClass.getScheduledDateTime().getTime()
                - System.currentTimeMillis() <= ONE_HOUR_MILLIS;
        return onlineClasses.stream().anyMatch(predicate);
    }

    /**
     * 检查 Major 课是否少于 15 个 TimeSlot
     *
     * @param teacherId
     * @param timezone
     * @param offsetOfWeek
     * @return
     */
    private boolean checkLess15TimeSlots(long teacherId, String timezone, int offsetOfWeek) {
        List<Date> daysOfWeek = getDaysOfWeek(offsetOfWeek);
        Date fromTime = daysOfWeek.get(0), toTime = daysOfWeek.get(DAY_OF_WEEK);
        int count = onlineClassDao.countByTeacherIdWithFromAndToTime(teacherId, fromTime, toTime, timezone);
        return (count <= PEAKTIME_TIMESLOT_DEFAULT_COUNT) ? true : false;
    }

    /**
     * 发送设置 24 小时请求
     *
     * @param set24HourRequest
     * @param teacher
     * @return
     */
    public boolean set24Hours(Set24HourRequest set24HourRequest, Teacher teacher) {
        Map<String, String> requestHeader = get24HoursRequestHeader(teacher.getId());
        String onlineClassIds = set24HourRequest.getOnlineClassIds().stream().map(id -> String.valueOf(id))
                .collect(Collectors.joining(","));
        String requestUrl = ApplicationConstant.TEACHER_24HOUR_URL + "/api/service/public/24HourClass?classIds="
                + onlineClassIds;
        logger.info("Set 24Hours Request Url: {}", requestUrl);

        HttpPut httpPut = new HttpPut(requestUrl);
        return send24HoursRequest(requestUrl, httpPut, requestHeader);
    }

    /**
     * 处理删除 24 小时
     *
     * @param delete24HourRequest
     * @param teacher
     * @return
     */
    public Map<String, Object> doDelete24Hours(Delete24HourRequest delete24HourRequest, Teacher teacher) {
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("result", delete24Hours(delete24HourRequest, teacher));
        return resultMap;
    }

    /**
     * 发送删除 24 小时请求
     *
     * @param delete24HourRequest
     * @param teacher
     * @return
     */
    public boolean delete24Hours(Delete24HourRequest delete24HourRequest, Teacher teacher) {
        Map<String, String> requestHeader = get24HoursRequestHeader(teacher.getId());
        String onlineClassIds = delete24HourRequest.getOnlineClassIds().stream().map(id -> String.valueOf(id))
                .collect(Collectors.joining(","));
        String requestUrl = ApplicationConstant.TEACHER_24HOUR_URL + "/api/service/public/24HourClass?classIds="
                + onlineClassIds;
        logger.info("Delete 24Hours Request Url: {}", requestUrl);

        HttpDelete httpDelete = new HttpDelete(requestUrl);
        return send24HoursRequest(requestUrl, httpDelete, requestHeader);
    }

    /**
     * 设置 24 小时的请求头
     *
     * @param teacherId
     * @return
     */
    private Map<String, String> get24HoursRequestHeader(long teacherId) {
        Preconditions.checkArgument(0 != teacherId);

        Map<String, String> requestHeader = new HashMap<>();
        final String sign = "TEACHER " + teacherId;
        requestHeader.put("Authorization", sign + " " + Base64.getEncoder().encodeToString(DigestUtils.md5(sign)));
        return requestHeader;
    }

    /**
     * 发送 24 小时请求
     *
     * @param requestUrl
     * @param httpRequestBase
     * @param requestHeader
     * @return
     */
    private boolean send24HoursRequest(String requestUrl, HttpRequestBase httpRequestBase,
                                       Map<String, String> requestHeader) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        try {
            httpRequestBase.setConfig(DEFAULT_REQUEST_CONFIG);
            if (null != requestHeader) {
                for (String headerName : requestHeader.keySet()) {
                    httpRequestBase.setHeader(headerName, requestHeader.get(headerName));
                }
            }

            httpClient = HttpClients.createDefault();
            httpResponse = httpClient.execute(httpRequestBase);
            logger.info("Send 24Hours response: {}", httpResponse);

            HttpEntity responseEntity = httpResponse.getEntity();
            return (null != responseEntity
                    && HttpURLConnection.HTTP_OK == httpResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            logger.error("Send 24Hours error: {}", e);
            return false;
        } finally {
            try {
                if (null != httpResponse) {
                    httpResponse.close();
                }
                if (null != httpClient) {
                    httpClient.close();
                }
            } catch (IOException e) {
                logger.error("HttpClient err: {}", e);
            }
        }
    }

    /**
     * 获取系统提示
     *
     * @param request
     * @param response
     * @param teacher
     * @return
     */
    public Map<String, Object> getTips(HttpServletRequest request, HttpServletResponse response, Teacher teacher) {
        Map<String, Object> resultMap = Maps.newHashMap();

        // 判断是否显示 AdminQuiz
        resultMap.put("showAdminQuiz", teacherPageLoginService.isType(teacher.getId(), LoginType.ADMINQUIZ));
        // 判断是否显示 Evaluation
        resultMap.put("showEvaluation", teacherPageLoginService.isType(teacher.getId(), LoginType.EVALUATION));
        // 判断是否需要显示 24 小时提示
        resultMap.put("show24HoursInfo", isShow24HoursInfo(request, response));

        return resultMap;
    }

    /**
     * 判断是否要显示 24 小时提醒
     *
     * @param request
     * @param response
     * @return
     */
    private boolean isShow24HoursInfo(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = CookieUtils.getCookie(request, CookieKey.TRPM_HOURS_24);
        if (null != cookie) {
            CookieUtils.removeCookie(response, CookieKey.TRPM_HOURS_24, null, null);
            return true;
        }
        return false;
    }


    /**
     * 自动取消课程
     *
     * @param onlineClassId
     * @param teacherId
     * @return
     */
    public boolean cancelClassSuccess(Long onlineClassId, Long teacherId,String cancelReason) {
        boolean flag = false;
       String finishType =getFinishType(onlineClassId,teacherId);
        if(StringUtils.isBlank(finishType)){
            logger.warn("This online class ：{} does not exist.", onlineClassId);
            return flag;
        }
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("onlineClassId", onlineClassId);
        requestParams.put("finishType", finishType);
        requestParams.put("operatorId", teacherId);
        requestParams.put("finishReason", cancelReason);
        logger.info("scalper param :  onlineClassId:{} , finishType:{},operatorId:{},cancelReason:{}",
                onlineClassId , finishType, teacherId,cancelReason);
        String requestUrl = scalperServerAddress + "/management/finish";
        String returnData = scalperService.cancelClass(requestParams);
        if (returnData != null) {
            Map<String, Object> returnModel = (Map<String, Object>) JSONObject.parse(returnData);
            if (SCALPER_SUCCESS_CODE == (int) returnModel.get("code")) {
                logger.info("cancel course by scalper success, params:{}", requestParams.toString());
                flag = true;
            } else if (returnModel.get("code")!=null && SCALPER_REFUSED_CODE == (int) returnModel.get("code")) {
                logger.info("cancel course by scalper refused! ");
            } else {
                logger.info("cancel course by scalper failed! code = {}", returnModel.get("code"));
            }
        } else {
            logger.error("post url={} result = null", requestUrl);
        }
        return flag;
    }

    /**
     * 获得finishType
     * @param onlineClassId
     * @param teacherId
     * @return
     */
    public String  getFinishType(long onlineClassId,long teacherId){
        OnlineClass onlineClass = this.onlineClassDao.findById(onlineClassId);

        if (null == onlineClass) {
            logger.warn("This online class ：{} does not exist.", onlineClassId);
            return null;
        }

        String courseType =  onlineClassDao.findOnlineClassCourseType(onlineClassId);
        String logpix = "onlineclassId:"+onlineClassId+";teacherId:"+teacherId;
        if(System.currentTimeMillis()> onlineClass.getScheduledDateTime().getTime()){
            logger.warn("Sorry, you can't cancel after the start time has passed.", logpix);
            return null;
        }


        if(!StringUtils.equalsIgnoreCase(onlineClass.getStatus(), ClassStatus.BOOKED.toString())){
            logger.warn("Sorry, you can't cancel this class because it was booked.", logpix);
            return null;
        }

        if(!courseCancel.contains(courseType)){
            logger.warn("Sorry, you can't cancel the class:{} Because courseType is :{} .",onlineClassId, courseType);
            return null;
        }

        String finishType =StringUtils.EMPTY;
        long time  = (onlineClass.getScheduledDateTime().getTime()-System.currentTimeMillis());
        if(time<7200000){
            finishType = ApplicationConstant.FinishType.TEACHER_NO_SHOW_2H;
        }
        if(time>=7200000&&time<=86400000){
            finishType = ApplicationConstant.FinishType.TEACHER_CANCELLATION_24H;
        }
        if(time>86400000){
            finishType = ApplicationConstant.FinishType.TEACHER_CANCELLATION;
        }
        return finishType;
    }

    @Slave
	public List<Map<String, Object>>  findIncentiveClasses(Date from, Date to, long id) {
		 return onlineClassDao.findOnlineClassesByStartTimeAndEndTime(from,to,id);
	}

	public void countOnlineClassesByStartTimeAndEndTime(Date from, Date to, Long id, Long incentiveCount,
			List<Map<String, Object>> resultList) {
		Map<String, Object> resultMap = Maps.newHashMap();
		long resultCount = 0;
		Integer count = onlineClassDao.countOnlineClassesByStartTimeAndEndTime(from, to, id);

		if (incentiveCount != null && incentiveCount != null) {
			resultCount = count - incentiveCount > 0 ? count - incentiveCount : 0;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MMM.dd", Locale.ENGLISH);  
		resultMap.put("fromStr",sdf.format(from));
		resultMap.put("toStr",sdf.format(new Date(to.getTime() - OnlineClassDao.ONE_SECOND)));
		resultMap.put("from",from.getTime());
		resultMap.put("to", to.getTime() - OnlineClassDao.ONE_SECOND);
		resultMap.put("resultCount",resultCount);

		resultList.add(resultMap);
	}

	public Long getIncentiveCount(Long teacherId) {
		String value = null;
		String key = RedisConstants.INCENTIVE_FOR_APRIL + teacherId;
		Long count = null;
		try {
			String existValue = redisProxy.get(key);
			if (StringUtils.isNoneEmpty(existValue)) {
				value = existValue;
				count = Long.parseLong(value);
			} else {
				int co = teacherService.incentivesTeacherInit(teacherId.toString());
				count = new Long(co);
			}

		} catch (Exception e) {
			logger.error("redis get key = {}", key, e);
		}
		return count;
	}



}
