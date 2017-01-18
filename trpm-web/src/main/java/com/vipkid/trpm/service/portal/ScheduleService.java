package com.vipkid.trpm.service.portal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vipkid.enums.OnlineClassEnum.ClassStatus;
import com.vipkid.enums.OnlineClassEnum.ClassType;
import com.vipkid.enums.OnlineClassEnum.CourseType;
import com.vipkid.trpm.constant.ApplicationConstant;
import com.vipkid.trpm.constant.ApplicationConstant.CookieKey;
import com.vipkid.trpm.constant.ApplicationConstant.FinishType;
import com.vipkid.trpm.constant.ApplicationConstant.PeakTimeType;
import com.vipkid.trpm.constant.ApplicationConstant.SlotStyle;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.PeakTime;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.schedule.TimePoint;
import com.vipkid.trpm.entity.schedule.TimeSlot;
import com.vipkid.trpm.entity.schedule.ZoneTime;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.util.CookieUtils;
import com.vipkid.trpm.util.DateUtils;
import com.vipkid.trpm.util.FilesUtils;
import com.vipkid.trpm.util.IpUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.vipkid.trpm.util.DateUtils.*;

@Service
public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    /* 老师默认PeakTime的TimeSolt数量 */
    private static final int PEAKTIME_TIMESLOT_DEFAULT_COUNT = 15;

    private static final int LOCK_TIMESLOT_EXPIRED = 30;

    /* 早九点的索引数 */
    private static final int NINE_OF_AM = 17;
    /* 晚九点半的索引数 */
    private static final int NINE_AND_HALF_OF_PM = 44;
    /* 早九点的索引数 */
    private static final int PRACTICUM_OF_AM = 0;
    /* 晚九点半的索引数 */
    private static final int PRACTICUM_AND_HALF_OF_PM = 47;

    /* 上课时段集合 */
    private static final List<String> schoolTime = getSchooltime(false);
    /* 每天的时间点集合 */
    private static final List<TimePoint> timePointsOfDay = getTimePointsOfDay();

    private static final int DEFAULT_TIMEOUT = 15 * 1000;

    private static final RequestConfig defaultRequestConfig =
                    RequestConfig.custom().setConnectionRequestTimeout(DEFAULT_TIMEOUT)
                                    .setConnectTimeout(DEFAULT_TIMEOUT).setSocketTimeout(DEFAULT_TIMEOUT).build();

    @Autowired
    private OnlineClassDao onlineClassDao;

    @Autowired
    private PeakTimeDao peakTimeDao;

    @Autowired
    private LessonDao lessonDao;

    @Autowired
    private AuditDao auditDao;

    @Autowired
    private TeacherPageLoginDao teacherLoginTypeDao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisProxy redisProxy;

    /**
     * 计算一天中的所有TimePoint，每半小时为一个单位
     * 
     * @return List<TimePoint>
     */
    private static List<TimePoint> getTimePointsOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        List<TimePoint> resultList = Lists.newLinkedList();

        IntStream.range(0, HALFHOUR_OF_DAY).forEach(index -> {
            resultList.add(new TimePoint(calendar.getTime()));
            calendar.add(Calendar.MINUTE, MINUTE_OF_HALFHOUR);
        });

        return resultList;
    }

    /**
     * 获取指定日期所在星期的天数（从星期一开始；为了兼容夏令时，这里的取值为这个星期一到下个星期一）
     * 
     * @param date 指定日期
     * @return List<Date>
     */
    public List<Date> daysOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1);
        }

        List<Date> daysOfWeek = Lists.newLinkedList();

        IntStream.rangeClosed(0, DAY_OF_WEEK).forEach(index -> {
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
     * @return List<TimeSlot>
     */
    public List<TimeSlot> timeSlotsOfWeek(List<Date> daysOfWeek, List<TimePoint> timePoints) {
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
     * 计算整个星期的ZoneTime
     * 
     * @param timeSlots
     * @param timezone
     * @return List<ZoneTime>
     */
    public List<ZoneTime> zoneTimesOfWeek(List<TimeSlot> timeSlots, String timezone) {
        List<ZoneTime> zoneTimesOfWeek = Lists.newLinkedList();

        timeSlots.stream().forEach((timeSlot) -> {
            zoneTimesOfWeek.add(new ZoneTime(timeSlot, timezone));
        });

        return zoneTimesOfWeek;
    }

    /**
     * 计算Schedule表格
     * 
     * @param daysOfWeek
     * @param timezone
     * @param peakTimeMap
     * @param courseType
     * @return Map<TimePoint, List<TimeSlot>>
     */
    public Map<TimePoint, List<TimeSlot>> scheduleTable(List<Date> daysOfWeek, String timezone,
                    Map<String, String> peakTimeMap, String courseType) {
        Comparator<TimePoint> comparator =
                        (first, second) -> Long.valueOf(first.getMillis() - second.getMillis()).intValue();

        Map<TimePoint, List<TimeSlot>> scheduleTable = Maps.newTreeMap(comparator);

        List<TimeSlot> timeSlotsOfWeek = timeSlotsOfWeek(daysOfWeek, timePointsOfDay);
        List<ZoneTime> zoneTimesOfWeek = zoneTimesOfWeek(timeSlotsOfWeek, timezone);

        // 更新每个TimeSlot的ZoneTime列表
        timeSlotsOfWeek.stream().forEach((timeSlot) -> {

            zoneTimesOfWeek.stream().forEach((zoneTime) -> {
                if (zoneTime.getLocalTime().equals(timeSlot.getLocalTime())) {
                    timeSlot.getZoneTime().add(zoneTime);

                    /* 设置当前timeSlot是否显示 */
                    timeSlot.setShow(isShow(zoneTime.getFormatToBeiJing(), courseType));

                    /* 设置当前timeSlot是否已过期 */
                    timeSlot.setExpired(zoneTime.getDateFromBeiJing().before(new Date()));

                    /* 设置当前timeSlot样式 */
                    String peakType = peakTimeMap.get(zoneTime.getFormatToBeiJing());
                    setTimeSlotStyle(timeSlot, peakType);
                }
            });

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
            long t = groupTimeSlots.stream().filter(timeSlot -> timeSlot.isShow()).count();
            if (0 != t) {
                scheduleTable.put(timePoint, groupTimeSlots);
            }

        });

        return scheduleTable;
    }

    /**
     * 设置TimeSlot样式
     * 
     * @param timeSlot
     * @param peakType
     */
    public void setTimeSlotStyle(TimeSlot timeSlot, String peakType) {
        if (timeSlot.isExpired()) {
            timeSlot.setStyle(SlotStyle.EXPIRED_TIME_SLOT);
        } else if (timeSlot.isShow()) {
            if (!StringUtils.isEmpty(peakType) && !StringUtils.contains(peakType, PeakTimeType.NORMALTIME)) {
                timeSlot.setStyle(SlotStyle.PEAK_TIME);
            } else {
                timeSlot.setStyle(SlotStyle.EMPTY);
            }
        } else {
            timeSlot.setStyle(SlotStyle.EXPIRED_TIME_SLOT);
        }
    }

    /**
     * 北京的上课时间为早9点到晚9点半
     * 
     * @return List<String>
     */
    private static List<String> getSchooltime(boolean isPracticum) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        List<String> resultList = Lists.newLinkedList();

        IntStream.range(0, HALFHOUR_OF_DAY).forEach(index -> {
            if (isPracticum) {
                if (index > PRACTICUM_OF_AM && index < PRACTICUM_AND_HALF_OF_PM) {
                    resultList.add(formatTo(calendar.toInstant(), FMT_HMS));
                }
            } else {
                if (index > NINE_OF_AM && index < NINE_AND_HALF_OF_PM) {
                    resultList.add(formatTo(calendar.toInstant(), FMT_HMS));
                }
            }

            calendar.add(Calendar.MINUTE, MINUTE_OF_HALFHOUR);
        });

        return resultList;
    }

    /**
     * 判断是否在上课时间段之内，是则显示
     * 
     * @param formatToBeiJing
     * @param courseType
     * @return boolean
     */
    public boolean isShow(String formatToBeiJing, String courseType) {
        long count = 0;
        if (!CourseType.isPracticum(courseType)) {
            count = schoolTime.stream().filter(time -> -1 != formatToBeiJing.indexOf(time)).count();
            return (0 != count) ? true : false;
        }
        return true;
    }

    /**
     * 获取PeakTime映射集合
     * 
     * @param fromTime
     * @param toTime
     * @return Map<String, String>
     */
    public Map<String, String> getPeakTimeMap(Date fromTime, Date toTime) {
        Map<String, String> peakTimeMap = Maps.newHashMap();
        List<PeakTime> peakTimeList = peakTimeDao.findByFromAndToTime(fromTime, toTime);

        Consumer<PeakTime> consumer = (peakTime) -> {
            Instant instant = peakTime.getTimePoint().toInstant();
            peakTimeMap.put(formatTo(instant, FMT_YMD_HMS), peakTime.getType());
        };

        peakTimeList.stream().forEach(consumer);
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

        /* 根据北京时间构造Map对象 */
        Map<String, Map<String, Object>> teacherScheduleMap = Maps.newHashMap();

        /* 查询需要显示的INVALID课程列表 */
        List<Map<String, Object>> invalidScheduleList =
                        onlineClassDao.findInvalidBy(teacherId, fromTime, toTime, timezone);

        /* 查询24小时的课程列表 */
        List<String> onlineClassIds = teacherScheduleList.stream().map(map -> {
            return String.valueOf(map.get("id"));
        }).collect(Collectors.toList());
        List<String> idsFor24Hour = get24HourClass(teacherId, onlineClassIds);
        boolean is24Hour;

        for (Map<String, Object> teacherSchedule : teacherScheduleList) {
            long onlineClassId = (Long) teacherSchedule.get("id");

            /**
             * 由于查询的列表中包含除了REMOVED之外所有的课程，因此需要过滤不用于显示的INVALID记录； 业务场景：换课时，只显示第一个被换老师的INVALID记录。
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

            /* 设置是否是24小时的课程 */
            if (idsFor24Hour.stream().anyMatch(id -> id.equals(String.valueOf(onlineClassId)))) {
                teacherSchedule.put("is24Hour", true);

            } else {
                teacherSchedule.put("is24Hour", false);

            }

            /* 设置OPEN课程的学生数量 */
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
        }
    }

    /**
     * 设置TeacherSchedule显示的优先级
     * 
     * @param teacherScheduleMap
     * @param scheduleKey
     * @param teacherSchedule
     */
    public void setSchedulePriority(Map<String, Map<String, Object>> teacherScheduleMap, String scheduleKey,
                    Map<String, Object> teacherSchedule) {
        boolean isReplaced = false;

        /* 获取新的TeacherSchedule的状态 */
        String newStatus = (String) teacherSchedule.get("status");
        String newFinishType = (String) teacherSchedule.get("finishType");

        if (teacherScheduleMap.containsKey(scheduleKey)) {
            /* 获取已有的TeacherSchedule的状态 */
            Map<String, Object> exsitTeacherSchedule = teacherScheduleMap.get(scheduleKey);
            String oldStatus = (String) exsitTeacherSchedule.get("status");
            String oldFinishType = (String) exsitTeacherSchedule.get("finishType");

            /* 增加新类型替换 */
            /* 如果老状态为FINISHED */
            if (ClassStatus.isFinished(oldStatus)) {
                /* 新状态为BOOKED，则替换 */
                if (ClassStatus.isBooked(newStatus)) {
                    isReplaced = true;
                }
                /* 新状态为AVAILABLE，老的FinishType为StudentNoShow24则替换 */
                if (ClassStatus.isAvailable(newStatus) && FinishType.isStudentNoShow24(oldFinishType)) {
                    isReplaced = true;
                }
                /* 新状态为FINISHED，老的FinishType为StudentNoShow则替换 */
                if (ClassStatus.isFinished(newStatus) && FinishType.isStudentNoShow(oldFinishType)) {
                    isReplaced = true;
                }
                /*学生24小时取消课，老状态为StudentNoShow，ClassStatus为isAvailable，is24Hour为true，将状态改为BOOKED*/
                boolean is24Hour = (boolean)teacherSchedule.get("is24Hour");
                Timestamp timestamp = (Timestamp) teacherSchedule.get("scheduledDateTime");
                Date scheduledTime = new Date(timestamp.getTime() + 30*60*1000);
                Date nowTime = new Date();
                boolean isCurrent = scheduledTime.after(nowTime);
                if (is24Hour && FinishType.isStudentNoShow(oldFinishType) && ClassStatus.isAvailable(newStatus) && isCurrent){
                    teacherSchedule.put("status",ClassStatus.BOOKED);
                }
            }

            /* 如果老状态为EXPIRED，新状态为FINISHED，或新状态为BOOKED，则替换 */
            if (ClassStatus.isExpired(oldStatus)) {
                if (ClassStatus.isFinished(newStatus) || ClassStatus.isBooked(newStatus)) {
                    isReplaced = true;
                }
            }

            /* 如果老状态为CANCELED，或REMOVED，则替换 */
            if (ClassStatus.isCanceled(oldStatus) || ClassStatus.isRemoved(oldStatus)) {
                isReplaced = true;
            }

            /* 如果老状态为AVAILABLE */
            if (ClassStatus.isAvailable(oldStatus)) {
                /* 新状态为BOOKED，则替换 */
                if (ClassStatus.isBooked(newStatus)) {
                    isReplaced = true;
                }
                /* 新FinishType为StudentNoShow，则替换 */
                if (FinishType.isStudentNoShow(newFinishType)) {
                    isReplaced = true;
                }
            }

            /* 如果老状态为INVALID，新状态不为INVALID，则替换 */
            if (ClassStatus.isInvalid(oldStatus) && !ClassStatus.isInvalid(newStatus)) {
                isReplaced = true;
            }
        } else {
            /* 如果新状态不为REMOVED，则替换 */
            if (!ClassStatus.isRemoved(newStatus)) {
                isReplaced = true;
            }
        }

        if (isReplaced) {
            teacherScheduleMap.put(scheduleKey, teacherSchedule);
        }
    }

    /**
     * 处理Schedule逻辑
     * 
     * @param offsetOfWeek
     * @param teacherId
     * @param timezone
     * @param courseType
     * @return Map<String, Object>
     */
    public Map<String, Object> doSchedule(int offsetOfWeek, long teacherId, String timezone, String courseType) {
        Map<String, Object> modelMap = Maps.newHashMap();
        Calendar calendar = Calendar.getInstance();

        /* 分页处理 */
        if (0 != offsetOfWeek) {
            calendar.add(Calendar.DATE, offsetOfWeek * DAY_OF_WEEK);
        }

        /* 获取日期所在星期 */
        List<Date> daysOfWeek = daysOfWeek(calendar.getTime());
        modelMap.put("daysOfWeek", daysOfWeek);

        /* 查询的开始时间和结束时间 */
        Date fromTime = daysOfWeek.get(0), toTime = daysOfWeek.get(DAY_OF_WEEK);

        /* 计算Schedule表格 */
        Map<String, String> peakTimeMap = getPeakTimeMap(fromTime, toTime);
        modelMap.put("scheduleTable", scheduleTable(daysOfWeek, timezone, peakTimeMap, courseType));

        /* 查询老师的Schedule记录 */
        modelMap.put("teacherScheduleMap", getTeacherScheduleMap(teacherId, fromTime, toTime, timezone));

        /* 设置页面显示日期 */
        modelMap.put("startDate", daysOfWeek.get(0));
        modelMap.put("endDate", daysOfWeek.get(DAY_OF_WEEK - 1));

        return modelMap;
    }

    /**
     * 处理TimeSlot创建逻辑
     * 
     * @param teacher
     * @param scheduleTime
     * @param courseType
     * @return Map<String,Object>
     */
    public Map<String, Object> doCreateTimeSlot(Teacher teacher, String scheduleTime, String courseType) {
        Map<String, Object> modelMap = Maps.newHashMap();

        /* 验证当前时间是否已存在OnlineClass */
        Timestamp scheduleDateTime = parseFrom(scheduleTime, FMT_YMD_HMS);

        if (canSetSchedule(teacher.getId(), scheduleDateTime)) {
            OnlineClass onlineClass = new OnlineClass();

            /* 设置课程的属性 */
            onlineClass.setTeacherId(teacher.getId());
            onlineClass.setScheduledDateTime(scheduleDateTime);

            onlineClass.setStatus(ClassStatus.AVAILABLE.toString());
            onlineClass.setSerialNumber(Long.toString(scheduleDateTime.getTime()));

            /* 设置为课程开始前1小时 */
            long oneHour = 60 * 60 * 1000;
            onlineClass.setAbleToEnterClassroomDateTime(new Timestamp(scheduleDateTime.getTime() - oneHour));
            onlineClass.setLastEditDateTime(new Timestamp(System.currentTimeMillis()));

            /* 变量半小时，用来参与验证时间点是否可以放课时 */
            long halfHour = 30 * 60 * 1000;

            /* 如果是PRACTICUM的课程，则需要指定ClassType */
            if (CourseType.isPracticum(courseType)) {
                onlineClass.setClassType(ClassType.PRACTICUM.val());

                /* 需要加锁，一次只处理一个请求 */
                synchronized (teacher) {
                    /* 验证PRACTICUM课程的这个时间点是否与MAJOR课时冲突 */
                    Timestamp plusHour = new Timestamp(scheduleDateTime.getTime() + halfHour);

                    /* 验证往后半小时的课程时间有没有跨天 */
                    LocalDateTime localDateTimeBeiJing = LocalDateTime.ofInstant(plusHour.toInstant(), SHANGHAI);
                    String formatToBeiJing = localDateTimeBeiJing.format(FMT_YMD_HMS);

                    if (!isShow(formatToBeiJing, courseType)) {
                        modelMap.put("action", false);
                        modelMap.put("disabledPlaceErr", true);
                        return modelMap;
                    }

                    /* 往前半小时只验证PRACTICUM的课程时间 */
                    Timestamp minusHour = new Timestamp(scheduleDateTime.getTime() - halfHour);
                    List<OnlineClass> tList =
                                    onlineClassDao.findByTeacherIdAndScheduleDateTime(teacher.getId(), minusHour);

                    long count = tList.stream().filter((o) -> o.getClassType() == ClassType.PRACTICUM.val())
                                    .filter((o) -> ClassStatus.isBooked(o.getStatus())
                                                    || ClassStatus.isAvailable(o.getStatus()))
                                    .count();

                    if (canSetSchedule(teacher.getId(), plusHour) && 0 == count) {
                        onlineClassDao.save(onlineClass);
                    } else {
                        modelMap.put("action", false);
                        modelMap.put("disabledPlaceErr", true);
                        return modelMap;
                    }
                }
            } else {
                /* 验证MAJOR课程的这个时间点是否与PRACTICUM课时冲突 */
                Timestamp minusHour = new Timestamp(scheduleDateTime.getTime() - halfHour);
                List<OnlineClass> tList = onlineClassDao.findByTeacherIdAndScheduleDateTime(teacher.getId(), minusHour);

                /* 往前半小时只验证PRACTICUM的课程时间 */
                long count = tList.stream().filter((o) -> o.getClassType() == ClassType.PRACTICUM.val()).filter(
                                (o) -> ClassStatus.isBooked(o.getStatus()) || ClassStatus.isAvailable(o.getStatus()))
                                .count();

                if (0 == count) {
                    onlineClassDao.save(onlineClass);
                } else {
                    modelMap.put("action", false);
                    modelMap.put("disabledPlaceErr", true);
                    return modelMap;
                }
            }

            /* 记录操作日志 */
            Map<String, Object> replaceMap = Maps.newHashMap();
            replaceMap.put("teacherId", teacher.getId());
            replaceMap.put("onlineClassId", onlineClass.getId());

            Instant instant = Calendar.getInstance().toInstant();
            replaceMap.put("createTime", DateUtils.formatTo(instant, DateUtils.FMT_YMD_HMS));
            replaceMap.put("scheduleDatetime", onlineClass.getScheduledDateTime());

            String content = FilesUtils.readLogTemplate(ApplicationConstant.AuditCategory.ONLINE_CLASS_CREATE,
                    replaceMap);
            auditDao.saveAudit(ApplicationConstant.AuditCategory.ONLINE_CLASS_CREATE, "INFO", content,
                            teacher.getRealName(), onlineClassDao, IpUtils.getRemoteIP());

            /* 返回结果 */
            String timePoint = formatTo(scheduleDateTime.toInstant(), teacher.getTimezone(), FMT_HMA_US);

            modelMap.put("onlineClassId", onlineClass.getId());
            modelMap.put("timePoint", timePoint);
            modelMap.put("action", true);
        } else {
            modelMap.put("action", false);
            modelMap.put("disabledPlaceErr", true);
        }

        return modelMap;
    }

    public Map<String, Object> doCreateTimeSlotWithLock(Teacher teacher, String scheduleTime, String courseType) {
		Timestamp scheduleDateTime = parseFrom(scheduleTime, FMT_YMD_HMS);
        String key = "TP:LOCK:" + teacher.getId() + ":" + scheduleDateTime.getTime();
        try {
            if (redisProxy.lock(key, LOCK_TIMESLOT_EXPIRED)) {
                return doCreateTimeSlot(teacher, scheduleTime, courseType);
            } else {
				Map<String, Object> modelMap = Maps.newHashMap();
                modelMap.put("action", false);
                modelMap.put("disabledPlaceErr", true);
                return modelMap;
            }
        } finally {
            redisProxy.del(key);
        }
    }

    /**
     * 
     * 验证这个时间点能否放课时
     * 
     * @author John
     *
     * @param teacherId
     * @param t
     * @return boolean
     */
    public boolean canSetSchedule(long teacherId, Timestamp t) {
        List<OnlineClass> tList = onlineClassDao.findByTeacherIdAndScheduleDateTime(teacherId, t);
        long count = tList.stream()
                        .filter((o) -> ClassStatus.isBooked(o.getStatus()) || ClassStatus.isAvailable(o.getStatus()))
                        .count();

        return (0 != count) ? false : true;
    }

    /**
     * 处理TimeSlot取消逻辑
     * 
     * @param teacher
     * @param onlineClassId
     * @param scheduleTime
     * @param courseType
     * @return Map<String, Object>
     */
    public Map<String, Object> doCancelTimeSlot(Teacher teacher, long onlineClassId, String scheduleTime,
                    String courseType) {
        Map<String, Object> modelMap = Maps.newHashMap();

        /* 如果当前取消时间为PeakTime，则不能少于15节课时 */
        Timestamp scheduleDateTime = parseFrom(scheduleTime, FMT_YMD_HMS);
        PeakTime peakTime = peakTimeDao.findByTimePoint(scheduleDateTime);

        if (CourseType.isMajor(courseType) && null != peakTime && PeakTimeType.isPeakTime(peakTime.getType())) {
            int totalPeakTime = totalPeakTime(scheduleDateTime, teacher.getId());
            logger.info("The teacher id: {}, total PeakTime: {}", teacher.getId(), totalPeakTime);

            if (totalPeakTime <= PEAKTIME_TIMESLOT_DEFAULT_COUNT) {
                modelMap.put("action", false);
                modelMap.put("peakTimeErr", true);
                return modelMap;
            }
        }

        /* 更新OnlineClass状态 */
        OnlineClass onlineClass = onlineClassDao.findById(onlineClassId);

        if (ClassStatus.isAvailable(onlineClass.getStatus())) {
            onlineClassDao.updateStatus(onlineClassId, ClassStatus.REMOVED.toString());

            /* 记录操作日志 */
            Map<String, Object> replaceMap = Maps.newHashMap();
            replaceMap.put("teacherId", teacher.getId());
            replaceMap.put("onlineClassId", onlineClass.getId());

            Instant instant = Calendar.getInstance().toInstant();
            replaceMap.put("createTime", DateUtils.formatTo(instant, DateUtils.FMT_YMD_HMS));
            replaceMap.put("scheduleDatetime", onlineClass.getScheduledDateTime());

            String content = FilesUtils.readLogTemplate(ApplicationConstant.AuditCategory.ONLINE_CLASS_DELETE,
                    replaceMap);
            auditDao.saveAudit(ApplicationConstant.AuditCategory.ONLINE_CLASS_DELETE, "INFO", content,
                            teacher.getRealName(), onlineClassDao, IpUtils.getRemoteIP());

            modelMap.put("action", true);
        } else {
            modelMap.put("action", false);
            modelMap.put("statusErr", true);
        }

        return modelMap;
    }

    /**
     * 查询老师的某个日期所在星期的PeakTime总数
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

    public boolean isShow24HourInfo(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = CookieUtils.getCookie(request, CookieKey.TRPM_HOURS_24);
        if (null != cookie) {
            CookieUtils.removeCookie(response, CookieKey.TRPM_HOURS_24, null, null);
            return true;
        }
        return false;
    }

    public boolean set24HourClass(long teacherId, long onlineClassId) {
        Map<String, String> requestHeader = get24HourRequestHeader(teacherId);
        logger.info("Set 24Hour Request Header: {}", requestHeader.get("Authorization"));

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        try {
            String requestUrl = ApplicationConstant.TEACHER_24HOUR_URL + "/api/service/public/24HourClass?classIds="
                            + onlineClassId;
            logger.info("Set 24Hour Request Url: {}", requestUrl);

            HttpPut httpPut = new HttpPut(requestUrl);
            httpPut.setConfig(defaultRequestConfig);

            if (null != requestHeader) {
                for (String headerName : requestHeader.keySet()) {
                    httpPut.setHeader(headerName, requestHeader.get(headerName));
                }
            }

            httpClient = HttpClients.createDefault();
            httpResponse = httpClient.execute(httpPut);
            logger.info("Set 24Hour Response: {}", httpResponse);

            HttpEntity responseEntity = httpResponse.getEntity();
            if (null != responseEntity && HttpURLConnection.HTTP_OK == httpResponse.getStatusLine().getStatusCode()) {
                return true;
            }
        } catch (Exception e) {
            logger.error("HttpClient err: {}", e.getMessage());
        } finally {
            try {
                if (null != httpResponse) {
                    httpResponse.close();
                }
                if (null != httpClient) {
                    httpClient.close();
                }
            } catch (IOException e) {
                logger.error("HttpClient err: {}", e.getMessage());
            }
        }

        return false;
    }

    public boolean delete24HourClass(long teacherId, long onlineClassId) {
        Map<String, String> requestHeader = get24HourRequestHeader(teacherId);
        logger.info("Delete 24Hour Request Header: {}", requestHeader.get("Authorization"));

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;

        try {
            String requestUrl = ApplicationConstant.TEACHER_24HOUR_URL + "/api/service/public/24HourClass?classIds="
                            + onlineClassId;
            logger.info("Delete 24Hour Request Url: {}", requestUrl);

            HttpDelete httpDelete = new HttpDelete(requestUrl);
            httpDelete.setConfig(defaultRequestConfig);

            if (null != requestHeader) {
                for (String headerName : requestHeader.keySet()) {
                    httpDelete.setHeader(headerName, requestHeader.get(headerName));
                }
            }

            httpClient = HttpClients.createDefault();
            httpResponse = httpClient.execute(httpDelete);
            logger.info("Delete 24Hour Response: {}", httpResponse);

            HttpEntity responseEntity = httpResponse.getEntity();
            if (null != responseEntity && HttpURLConnection.HTTP_OK == httpResponse.getStatusLine().getStatusCode()) {
                return true;
            }
        } catch (Exception e) {
            logger.error("HttpClient err: {}", e.getMessage());
        } finally {
            try {
                if (null != httpResponse) {
                    httpResponse.close();
                }
                if (null != httpClient) {
                    httpClient.close();
                }
            } catch (IOException e) {
                logger.error("HttpClient err: {}", e.getMessage());
            }
        }

        return false;
    }

    public List<String> get24HourClass(long teacherId, List<String> onlineClassIds) {
        Map<String, String> requestHeader = get24HourRequestHeader(teacherId);
        logger.info("Get 24Hour Request Header: {}", requestHeader.get("Authorization"));

        try {
            Map<String, String> requestParams = Maps.newHashMap();
            String value = onlineClassIds.stream().collect(Collectors.joining(","));
            requestParams.put("classIds", value);

            String requestUrl =
                            ApplicationConstant.TEACHER_24HOUR_URL + "/api/service/public/24HourClass/filterByClass";
            logger.info("Get 24Hour Request Url: {}", requestUrl);

            String responseBody = HttpClientProxy.get(requestUrl, requestParams, requestHeader);
            responseBody = StringTools.matchString(responseBody, "\\[(.*?)\\]", Pattern.CASE_INSENSITIVE, 1);
            return Arrays.asList(StringUtils.split(responseBody, ","));
        } catch (Exception e) {
            logger.error("HttpClientProxy err: {}", e.getMessage());
            return Lists.newArrayList();
        }
    }

    private Map<String, String> get24HourRequestHeader(long teacherId) {
        String t = "TEACHER " + teacherId;
        Map<String, String> requestHeader = new HashMap<String, String>();
        requestHeader.put("Authorization", t + " " + Base64.encodeBase64String(DigestUtils.md5(t)));
        return requestHeader;
    }

    public boolean checkTimeSlots(long teacherId, String timezone, int offsetOfWeek) {
        Calendar calendar = Calendar.getInstance();
        if (0 != offsetOfWeek) {
            calendar.add(Calendar.DATE, offsetOfWeek * DAY_OF_WEEK);
        }

        /* 获取日期所在星期 */
        List<Date> daysOfWeek = daysOfWeek(calendar.getTime());

        /* 查询的开始时间和结束时间 */
        Date fromTime = daysOfWeek.get(0), toTime = daysOfWeek.get(DAY_OF_WEEK);

        int count = onlineClassDao.countByTeacherIdWithFromAndToTime(teacherId, fromTime, toTime, timezone);
        return (count >= PEAKTIME_TIMESLOT_DEFAULT_COUNT) ? true : false;
    }

    public boolean checkInOneHour(OnlineClass onlineClass) {
        return !(null != onlineClass && (onlineClass.getScheduledDateTime().getTime() - System.currentTimeMillis()) > 1 * 60
                * 60 * 1000);
    }

}
