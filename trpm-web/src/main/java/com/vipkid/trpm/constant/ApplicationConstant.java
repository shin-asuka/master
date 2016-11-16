package com.vipkid.trpm.constant;

import java.util.HashMap;
import java.util.Map;

import org.community.config.PropertyConfigurer;

public interface ApplicationConstant {

    public static String AES_128_KEY = "ShareFaceBook";

    public static String NEW_TEACHER_NAME = "new VIPKID teacher";

    public static String REFEREEID = "refereeId";
    
    public static String PARTNERID = "partnerId";

    /** Practicum课程 的finishType更新接口地址 */
    public static final String REQUEST_URL =
            PropertyConfigurer.stringValue("finishPracticumClass.url");

    /** 发送帮助接口地址 */
    public static final String HELP_URL = PropertyConfigurer.stringValue("help.url");

    /** 教师进入教室通知接口地址 */
    public static final String TEACHER_IN_CLASSROOM_URL =
            PropertyConfigurer.stringValue("teacher.in.classroom.url");

    /** 教师24小时放课口地址 */
    public static final String TEACHER_24HOUR_URL =
            PropertyConfigurer.stringValue("http.appServer");

    public static Integer UA_FOR_CLASS_ID = 20160420;

    public static final class AjaxCode {

        public static String ERROR_CODE = "error-account";

        public static String SUCCESS_CODE = "success-pass";

        public static String LOCKED_CODE = "error-locked";

        public static String QUIT_CODE = "quit-locked";

        public static String TYPE_CODE = "user-error";

        public static String EMAIL_CODE = "email-error";

        public static String VERIFY_CODE = "need-verify-code";

        public static String VERIFY_CODE_ERROR = "verify-code-error";

    }

    /* 日志名称定义 */
    public static final class AuditCategory {

        /** 创建UA日志 */
        public static final String REPORT_UA_CREATE = "REPORT_UA_CREATE";
        /** 更新UA日志 */
        public static final String REPORT_UA_UPDATE = "REPORT_UA_UPDATE";
        /** 提交demo */
        public static final String REPORT_DEMO_SUBMIT = "REPORT_DEMO_SUBMIT";
        /** 保存demo */
        public static final String REPORT_DEMO_SAVE = "REPORT_DEMO_SAVE";
        /** 提交feedback */
        public static final String REPORT_FEEDBACK_SAVE = "REPORT_FEEDBACK_SAVE";
        /** 创建onlineClass */
        public static final String ONLINE_CLASS_CREATE = "TP_ONLINE_CLASS_CREATE";
        /** 删除onlineClass */
        public static final String ONLINE_CLASS_DELETE = "TP_ONLINE_CLASS_DELETE";
        /** 发送星星 */
        public static final String STAR_SEND = "STAR_SEND";
        /** 移除星星 */
        public static final String STAR_REMOVE = "STAR_REMOVE";
        /** 进入教室 */
        public static final String CLASSROOM_ENTER = "CLASSROOM_ENTER";
        /** 退出教室 */
        public static final String CLASSROOM_EXIT = "CLASSROOM_EXIT";
        /** 记录Practicum审核日志 */
        public static final String PRACTICUM_AUDIT = "PRACTICUM_AUDIT";
        /** 记录Practicum课程报告创建日志 */
        public static final String PRACTICUM_REPORT_CREATE = "PRACTICUM_REPORT_CREATE";
        /** 记录Practicum课程报告更新日志 */
        public static final String PRACTICUM_REPORT_UPDATE = "PRACTICUM_REPORT_UPDATE";
        // 锁定教师
        public static final String TEACHER_LOCK = "TEACHER_LOCK";
        // 解锁教师
        public static final String TEACHER_UNLOCK = "TEACHER_UNLOCK";
    }

    /* 多媒体类型定义 */
    public static final class MediaType {

        /* 头像 */
        public static final String AVATAR = "AVATAR";
        /* 图片 */
        public static final String IMAGE = "IMAGE";
        /* 视频 */
        public static final String VIDEO = "VIDEO";
        /* 音频 */
        public static final String AUDIO = "AUDIO";
        /* 周报月报 */
        public static final String REPORT = "REPORT";
        /* 文件 -- 老师简历 */
        public static final String FILE = "FILE";
        /* 单元测评 */
        public static final String UNIT_TEST = "UNIT_TEST";

    }

    /* 阿里云存储服务 */
    public static final class OSS {

        public static final String BUCKET = PropertyConfigurer.stringValue("oss.bucket");
        public static final String ENDPOINT = PropertyConfigurer.stringValue("oss.endpoint");
        public static final String KEY_ID = PropertyConfigurer.stringValue("oss.key_id");
        public static final String KEY_SECRET = PropertyConfigurer.stringValue("oss.key_secret");
        public static final String SUFFIX = PropertyConfigurer.stringValue("oss.suffix");

        public static final String SHRINK_URl = PropertyConfigurer.stringValue("oss.shrink_url");
        public static final String URL_PREFFIX = PropertyConfigurer.stringValue("oss.url_preffix");

        public static class Template {
            public static final String PPT =
                    "http://resource.vipkid.com.cn/teaching_resource/ppt/{{lessonSerialNumber}}/index.html";
        }

        public static class Parameter {
            public static final String PPT = "{{lessonSerialNumber}}";
        }

    }

    /* Time Slots 样式类型定义 */
    public static final class SlotStyle {

        public static final String EXPIRED_TIME_SLOT = "expiredTimeSlot";
        public static final String PEAK_TIME = "peakTime";
        public static final String EMPTY = "empty";

    }

    /* Online Class 状态定义 */
    public static final class ClassStatus {

        public static final String AVAILABLE = "AVAILABLE"; // 可排课
        public static final String OPEN = "OPEN"; // 一对多课程，可接受预约
        public static final String BOOKED = "BOOKED"; // 已预约
        public static final String FINISHED = "FINISHED"; // 已结束

        public static final String CANCELED = "CANCELED"; // 已取消
        public static final String EXPIRED = "EXPIRED"; // 已过期
        public static final String REMOVED = "REMOVED"; // 已删除， 用于统计
        public static final String INVALID = "INVALID"; // 换老师操作后，原课程变为INVALID

        public static boolean isAvailable(String status) {
            return AVAILABLE.equals(status);
        }

        public static boolean isOpen(String status) {
            return OPEN.equals(status);
        }

        public static boolean isBooked(String status) {
            return BOOKED.equals(status);
        }

        public static boolean isFinished(String status) {
            return FINISHED.equals(status);
        }

        public static boolean isCanceled(String status) {
            return CANCELED.equals(status);
        }

        public static boolean isExpired(String status) {
            return EXPIRED.equals(status);
        }

        public static boolean isRemoved(String status) {
            return REMOVED.equals(status);
        }

        public static boolean isInvalid(String status) {
            return INVALID.equals(status);
        }

    }

    /* 课程类型定义 */
    public static final class CourseType {

        public static final String PRACTICUM = "PRACTICUM";
        public static final String MAJOR = "MAJOR";

        public static boolean isPracticum(String courseType) {
            return PRACTICUM.equals(courseType);
        }

        public static boolean isMajor(String courseType) {
            return MAJOR.equals(courseType);
        }

    }

    /* Online Class 的完成类型定义 */
    public static final class FinishType {

        public static final String AS_SCHEDULED = "AS_SCHEDULED"; // 正常结束
        public static final String STUDENT_NO_SHOW = "STUDENT_NO_SHOW";
        public static final String STUDENT_NO_SHOW_24H = "STUDENT_NO_SHOW_24H";

        public static final String TEACHER_NO_SHOW = "TEACHER_NO_SHOW"; // 教师未出席或者网络问题（不扣课时）
        public static final String TEACHER_NO_SHOW_WITH_BACKUP = "TEACHER_NO_SHOW_WITH_BACKUP";
        public static final String TEACHER_NO_SHOW_WITH_SHORTNOTICE =
                "TEACHER_NO_SHOW_WITH_SHORTNOTICE"; // 教师未出席或者网络问题（不扣课时）

        public static final String STUDENT_IT_PROBLEM = "STUDENT_IT_PROBLEM"; // 学生IT问题
        public static final String TEACHER_IT_PROBLEM = "TEACHER_IT_PROBLEM"; // 老师IT问题
        public static final String TEACHER_IT_PROBLEM_WITH_BACKUP =
                "TEACHER_IT_PROBLEM_WITH_BACKUP"; // 原老师由于IT问题，由替课老师完成这节课
        public static final String TEACHER_IT_PROBLEM_WITH_SHORTNOTICE =
                "TEACHER_IT_PROBLEM_WITH_SHORTNOTICE";

        public static final String TEACHER_CANCELLATION = "TEACHER_CANCELLATION"; // 老师申请取消
        public static final String SYSTEM_PROBLEM = "SYSTEM_PROBLEM"; // 系统问题
        public static final String TEACHER_CANCELLATION_24H = "TEACHER_CANCELLATION_24H"; // 在2小时之外24小时之内取消
        public static final String TEACHER_NO_SHOW_2H = "TEACHER_NO_SHOW_2H"; // 教师2小时之内取消
        public static final String STUDENT_CANCELLATION = "STUDENT_CANCELLATION"; // 老师申请取消
        public static boolean isStudentNoShow(String finishType) {
            return STUDENT_NO_SHOW.equals(finishType);
        }

        public static boolean isStudentNoShow24(String finishType) {
            return STUDENT_NO_SHOW_24H.equals(finishType);
        }

    }

    /* Peak Time 类型定义 */
    public static final class PeakTimeType {

        public static final String NORMALTIME = "NORMALTIME";

        public static final String PEAKTIME = "PEAKTIME";

        public static boolean isNormalTime(String peakTimeType) {
            return NORMALTIME.equals(peakTimeType);
        }

        public static boolean isPeakTime(String peakTimeType) {
            return PEAKTIME.equals(peakTimeType);
        }

    }

    /* Report 生命周期类型定义 */
    public static final class ReportLifeCycle {

        public static final String UNFINISHED = "UNFINISHED";
        public static final String SUBMITTED = "SUBMITTED";

        public static final String CONFIRMED = "CONFIRMED";
        public static final String PARENTREADED = "PARENTREADED";

    }

    /* 招聘流程状态定义 */
    public static final class RecruitmentStatus {

        public static final String SIGNUP = "SIGNUP";// 新申请
        public static final String BASIC_INFO = "BASIC_INFO"; // 添加basic-info状态，从signup分离
        public static final String INTERVIEW = "INTERVIEW";// 面试
        public static final String SIGN_CONTRACT = "SIGN_CONTRACT"; // 签合同

        public static final String TRAINING = "TRAINING"; // 教师培训
        public static final String PRACTICUM = "PRACTICUM";// 试讲
        public static final String CANCELED = "CANCELED"; // 已取消
        public static final String FINISHED = "FINISHED";// 已结束

    }

    /* 招聘流程结果定义 */
    public static final class RecruitmentResult {

        public static final String AUDITING = "AUDITING";// 审核中 （15-10-8新增
        public static final String PASS = "PASS";// 通过
        public static final String FAIL = "FAIL"; // 失败

        public static final String REAPPLY = "REAPPLY"; // 重新申请="";继续上PRACTICUM1（由于客观原因没能完成面试）
        public static final String PRACTICUM2 = "RACTICUM2"; // 第一次面试没通过，上PRACTICUM2

        public static final String TBD = "TBD";
        public static final String TBD_FAIL = "TBD_FAIL";
    }

    /* 老师生命周期定义 */
    public static final class TeacherLifeCycle {

        public static final String SIGNUP = "SIGNUP"; // 注册 + 收集基本信息
        public static final String BASIC_INFO = "BASIC_INFO"; // 添加basic-info状态，从signup分离
        public static final String INTERVIEW = "INTERVIEW"; // 面试

        public static final String SIGN_CONTRACT = "SIGN_CONTRACT"; // 签合同
        public static final String TRAINING = "TRAINING"; // 教师培训
        public static final String PRACTICUM = "PRACTICUM";// 试讲

        public static final String REGULAR = "REGULAR"; // 成为正式老师Ø
        public static final String QUIT = "QUIT"; // 离职
        public static final String FAIL = "FAIL"; // 被剔除的老师，永不录用的那种

    }

    /* 老师类型定义 */
    public static final class TeacherType {

        public static final String FULL_TIME = "FULL_TIME"; // 全职
        public static final String PART_TIME = "PART_TIME"; // 兼职
        public static final String TEST = "TEST"; // test

    }

    /* OnlineClass类型定义 */
    public static final class ClassType {

        public static final int MAJOR = 0;
        public static final int PRACTICUM = 1;
        public static final int INTERVIEW = 2;

    }

    /** UA 状态 */
    public static final class UaReportStatus {

        /** 新建 */
        public static final int NEWADD = 0;

        /** 已经审核通过 */
        public static final int REVIEWED = 1;

        /** 已驳回 */
        public static final int RESUBMIT = 2;

    }

    public static final class LoginType {

        public static final int CLASSROOMS = 0;

        public static final int PRACTICUM = 1;
        
        public static final int ADMINQUIZ = 2;
        
        public static final int EVALUATION = 3;
        
        public static final int EVALUATION_CLICK = 4;

    }

    public static final String REDIS_ACTIVITY_KEY = "activity-";

    /* Cookie key 定义 */
    public static final class CookieKey {

        public static final String TRPM_TOKEN = "TRPM_TOKEN";

        public static final String TRPM_PASSPORT = "TRPM_PASSPORT_NEW";

        public static final String TRPM_CHANGE_WINDOW = "TRPM_CHANGE_WINDOW";

        public static final String TRPM_LOGIN_TOKEN_ID = "TRPM_TOKEN_ID";

        public static final String TRPM_HOURS_24 = "TRPM_HOURS_24";
        
        public static final String AUTOKEN = "Authorization";

    }

    public static final class RedisConstants {

    	public static final String TEACHER_TOKEN = "TEACHER_TOKEN"; // 教师redis缓存key前缀

        //从app端跳h5页面时的cookie
        //必须和teacher-information-service的CookieConstant.APP_COOKIE_PREFIX保持一致,不然获取不到token
        public static final String APP_TOKEN = "appCookie_";
    	
        public static final String IMAGE_CODE_KEY = "TRPM_REST_IMAGE_CODE:%s"; // 图片验证码
        public static final String LOGIN_PASSWORD_FAILED_DAY_KEY =
                "TRPM_REST_LOGIN_PASSWORD_FAILD_KEY:%s"; // 一天内登录失败次数key
        public static final String LOGIN_IP_MAX_NUM_EXCEED_KEY =
                "TRPM_REST_LOGIN_IP_MAXNUM_EXCEED_KEY:%s"; // 一天内同一IP重复调用登陆接口key

        public static final int LOGIN_IP_MAX_NUM_EXCEED_DAY_NUM = 5; // 一天内同一IP重复调用登陆接口 不超过5次
        public static final int LOGIN_PASSWORD_FAILED_DAY_NUM = 5; // 一天内登录失败次数 不超过5次
        public static final int LOGIN_PASSWORD_FAILED_DAY_SEC = 60 * 60 * 24;// 一天=86400秒
        public static final int LOGIN_IP_MAX_NUM_EXCEED_DAY_SEC = 60 * 60 * 24;// 一天内同一IP重复调用登陆接口
                                                                               // 一天=86400秒

        public static final int IMAGE_CODE_INVALID_SEC = 120; // 图片验证码失效时间阀值
        public static final int PAYROLL_DISPLAY_MAX_NUM_EXCEED_DAY_SEC = 60 * 60 * 24 * 10 ;//payroll 部分老师可用 10天
    }

    Map<Integer, String> LEVEL_OF_DIFFITULTY = new HashMap<Integer, String>(){{
        put(1, "Very Difficult");
        put(2, "Slightly Difficult");
        put(3, "On Level");
        put(4, "Slightly Easy");
        put(5, "Very Easy");
    }};

}
