package com.vipkid.portal.glory.service;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.vipkid.enums.TeacherEnum;
import com.vipkid.enums.TeacherGloryEnum;
import com.vipkid.http.service.ManageGatewayService;
import com.vipkid.portal.glory.model.TeacherGlory;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.rest.portal.vo.StudentCommentPageVo;
import com.vipkid.rest.portal.vo.StudentCommentVo;
import com.vipkid.rest.security.AppContext;
import com.vipkid.rest.utils.UserUtils;
import com.vipkid.trpm.dao.OnlineClassDao;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherGloryInfoDao;
import com.vipkid.trpm.entity.OnlineClass;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherGloryInfo;
import com.vipkid.trpm.entity.User;
import com.vipkid.trpm.service.portal.OnlineClassService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * Created by LP-813 on 2017/4/25.
 */
@Service
public class TeacherGloryRestService{

    private static final String NO_GLORY = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";

    private static final Logger logger = LoggerFactory.getLogger(TeacherGloryRestService.class);

    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private OnlineClassDao onlineClassDao;
    @Autowired
    private ManageGatewayService manageGatewayService;
    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    @Autowired
    private TeacherGloryInfoDao teacherGloryInfoDao;
    public String[] refeshGlory(String currentGlory,Integer userId) {

        //新建一个成就数组，初始化数据
        String initGlory  = NO_GLORY + "," + new Date().getTime()/1000;
        String[] gloryArr = StringUtils.split(initGlory,",");

        //加载当前成就
        if(StringUtils.isNotEmpty(currentGlory)) {
            gloryArr = StringUtils.split(currentGlory,",");
            Long cacheTime = NumberUtils.toLong(gloryArr[gloryArr.length - 1]);

            //距上次成就计算时间不足15min，不再重新计算
            if (new Date().getTime() / 1000 - cacheTime < 15 * 60) {
                return gloryArr;
            }
        }

        //计算成就，更新状态。
        //为避免过度设计，现有方案采用内部类实现，未来预案可采用责任链模式
        GloryHandler gloryHandler = new GloryHandler(userId.longValue());
        gloryArr[0] = gloryHandler.handle1(gloryArr[0]);
        gloryArr[1] = gloryHandler.handle2(gloryArr[1]);
        gloryArr[2] = gloryHandler.handle3(gloryArr[2]);
        gloryArr[3] = gloryHandler.handle4(gloryArr[3]);
        gloryArr[4] = gloryHandler.handle5(gloryArr[4]);
        gloryArr[5] = gloryHandler.handle6(gloryArr[5]);
        gloryArr[6] = gloryHandler.handle7(gloryArr[6]);
        gloryArr[7] = gloryHandler.handle8(gloryArr[7]);
        gloryArr[8] = gloryHandler.handle9(gloryArr[8]);
        gloryArr[9] = gloryHandler.handle10(gloryArr[9]);
        gloryArr[10] = gloryHandler.handle11(gloryArr[10]);
        gloryArr[11] = gloryHandler.handle12(gloryArr[11]);
        gloryArr[12] = gloryHandler.handle13(gloryArr[12]);
        gloryArr[13] = gloryHandler.handle14(gloryArr[13]);
        gloryArr[14] = gloryHandler.handle15(gloryArr[14]);
        gloryArr[15] = gloryHandler.handle16(gloryArr[15]);
        gloryArr[16] = String.valueOf(new Date().getTime()/1000);
        return gloryArr;
    }

    public List<TeacherGlory> getGloryView(String[] gloryArr,long userId) {
        //读取glory字典
        List<TeacherGloryInfo> teacherGloryInfoList = teacherGloryInfoDao.getAll();
        Map<Integer,TeacherGloryInfo> gloryMap = Maps.newHashMap();
        for(TeacherGloryInfo teacherGloryInfo :teacherGloryInfoList){
            gloryMap.put(teacherGloryInfo.getId(),teacherGloryInfo);
        }

        //计算需要展示的成绩内容
        List<TeacherGlory> teacherGloryList = Lists.newArrayList();
        for(int i=0;i<gloryArr.length;i++) {
            if(TeacherGloryEnum.Status.FINISH.value().equals(gloryArr[i])){
                TeacherGlory teacherGlory = new TeacherGlory();
                Integer gloryId = i+1;
                teacherGlory.setId(gloryId);
                teacherGlory.setName(gloryMap.get(gloryId).getName());
                //teacherGlory.setUserId(new Long(AppContext.getUser().getId()).intValue());
                teacherGlory.setUserId(new Long(userId).intValue());
                teacherGlory.setFinishTime(DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
                teacherGlory.setPriority(gloryMap.get(gloryId).getPriority());
                teacherGlory.setAvatar(gloryMap.get(gloryId).getAvatar());
                teacherGlory.setTitle(gloryMap.get(gloryId).getTitle());
                teacherGlory.setDescription(gloryMap.get(gloryId).getDescription());
                teacherGlory.setShareTitle(gloryMap.get(gloryId).getShareTitle());
                teacherGlory.setShareDescription(gloryMap.get(gloryId).getShareDescription());
                teacherGloryList.add(teacherGlory);
            }
        }
        return teacherGloryList;
    }

    //完成展示，状态转移为SHOWN
    public String[] markShownStatus(String[] gloryArr) {
        for(int i=0;i<gloryArr.length;i++) {
            if (TeacherGloryEnum.Status.FINISH.value().equals(gloryArr[i])) {
                gloryArr[i] = TeacherGloryEnum.Status.SHOWN.value();
            }
        }
        return gloryArr;
    }

    class GloryHandler{

        private Long userId;
        private List<Map<String,Object>> teacherClassList = null;
        private List<Map<String,Object>> teacherReferalList = null;

        GloryHandler(Long userId){
            this.userId = userId;
        }

        //Life Cycle变为Regular
        String handle1(String status){
            if(status.equals(TeacherGloryEnum.Status.UNFINISH.value())){
                Long now = Calendar.getInstance().getTime().getTime()/1000;
                Teacher teacher = teacherDao.findById(userId);
                if(teacher != null && teacher.getEntryDate()!=null && "REGULAR".equals(teacher.getLifeCycle())) {
                    if(now - teacher.getEntryDate().getTime()/1000 > 7*24*3600){
                        status = TeacherGloryEnum.Status.EXPIRED.value();
                    }else{
                        status = TeacherGloryEnum.Status.FINISH.value();
                    }
                }
            }
            return status;
        };
        //As Schedule课程记录达到1节
        String handle2(String status){
            return classNumGlory(status,1);
        };
        //As Schedule课程记录达到10节
        String handle3(String status){
            return classNumGlory(status,10);
        };
        //As Schedule课程记录达到100节
        String handle4(String status){
            return classNumGlory(status,100);
        };
        //As Schedule课程记录达到500节
        String handle5(String status){
            return classNumGlory(status,500);
        };
        //As Schedule课程记录达到1000节
        String handle6(String status){
            return classNumGlory(status,1000);
        };
        //推荐成功的⽼师（有⾄少⼀节As Schedule完课记录）数量达到1
        String handle7(String status){
            return referalNumGlory(status,1);
        };
        //推荐成功的⽼师（有⾄少⼀节As Schedule完课记录）数量达到10
        String handle8(String status){
            return referalNumGlory(status,10);
        };
        //推荐成功的⽼师（有⾄少⼀节As Schedule完课记录）数量达到100
        String handle9(String status){
            return referalNumGlory(status,100);
        };
        //推荐成功的⽼师（有⾄少一节As Schedule完课记录）数量达到500
        String handle10(String status){
            return referalNumGlory(status,500);
        };
        //推荐成功的⽼师（有⾄少一节As Schedule完课记录）数量达到1000
        String handle11(String status){
            return referalNumGlory(status,1000);
        };
        //收到了了第⼀个5苹果家⻓评价
        String handle12(String status){
            if(TeacherGloryEnum.Status.UNFINISH.equals(status)) {
                StudentCommentPageVo studentCommentPageVo = manageGatewayService.getStudentCommentListByTeacherId(userId.intValue(), null, null, "high");
                Integer total = studentCommentPageVo.getTotal();
                if(total>0) {
                    StudentCommentPageVo lastStudentComment = manageGatewayService.getStudentCommentListByTeacherId(userId.intValue(), total-1,1, "high");
                    List<StudentCommentVo> studentCommentVos = lastStudentComment.getData();
                    if(CollectionUtils.isNotEmpty(studentCommentVos)) {
                        try {
                            Date finishTime = DateUtils.parseDate(studentCommentVos.get(0).getCreate_time());
                            Long now = Calendar.getInstance().getTime().getTime()/1000;
                            if(now - finishTime.getTime()/1000 <= 7*24*1000){
                                status = TeacherGloryEnum.Status.FINISH.value();
                            }else {
                                status = TeacherGloryEnum.Status.EXPIRED.value();
                            }
                        } catch (ParseException e) {
                            logger.error("【GloryNo12】ParseException---userId:" + userId, e);
                        }
                    }
                }
            }
            return status;
        };
        //Life Cycle变为Regular时间达到100天
        String handle13(String status){
            if(status.equals(TeacherGloryEnum.Status.UNFINISH.value())){
                Long now = Calendar.getInstance().getTime().getTime()/1000;
                Teacher teacher = teacherDao.findById(userId);
                if(teacher != null && teacher.getEntryDate()!=null) {
                    if(now - teacher.getEntryDate().getTime()/1000 > 100*24*3600){
                        status = TeacherGloryEnum.Status.FINISH.value();
                    }
                    if(now - teacher.getEntryDate().getTime()/1000 > 107*24*3600){
                        status = TeacherGloryEnum.Status.EXPIRED.value();
                    }
                }
            }
            return status;
        };

        //Life Cycle变为Teaching Prep
        String handle14(String status){
            return recruitmentStatusGlory(status,"INTERVIEW");
        };

        //Life Cycle变为Contract&Info
        String handle15(String status){
            return recruitmentStatusGlory(status,"PRACTICUM");
        };

        //第⼀节Booked Class记录
        String handle16(String status){
            if(status.equals(TeacherGloryEnum.Status.UNFINISH.value())) {
                Long now = Calendar.getInstance().getTime().getTime() / 1000;
                HashMap cond = Maps.newHashMap();
                cond.put("teacherId",userId);
                cond.put("limit",1);
                List<Map<String,Object>> bookclassList = onlineClassDao.findBookedClassByTeacherId(cond);
                if(CollectionUtils.isNotEmpty(bookclassList)) {
                    Map<String,Object> onlineClass = bookclassList.get(0);
                    Long finishTime = (Long)onlineClass.get("bookDateTime");
                    if (now - finishTime <= 7 * 24 * 3600) {
                        return TeacherGloryEnum.Status.FINISH.value();
                    } else {
                        return TeacherGloryEnum.Status.EXPIRED.value();
                    }
                }
            }
            return status;
        };

        /**
         * 招募状态类成就通用逻辑
         * @param status 成就状态
         * @param recruitmentStatus 招募状态
         * @return
         */
        String recruitmentStatusGlory(String status,String recruitmentStatus){
            if(status.equals(TeacherGloryEnum.Status.UNFINISH.value())) {
                Long now = Calendar.getInstance().getTime().getTime() / 1000;
                List<TeacherApplication> teacherApplications = teacherApplicationDao.findApplicationForStatusResult(userId, recruitmentStatus, "PASS");
                if(CollectionUtils.isNotEmpty(teacherApplications)) {
                    TeacherApplication ta = teacherApplications.get(0);
                    if(ta.getAuditDateTime()!=null) {
                        Long finishTime = ta.getAuditDateTime().getTime() / 1000;
                        if (now - finishTime <= 7 * 24 * 3600) {
                            return TeacherGloryEnum.Status.FINISH.value();
                        } else {
                            return TeacherGloryEnum.Status.EXPIRED.value();
                        }
                    }
                }
            }
            return status;
        }

        /**
         * AS_SCHEDULE类成就通用逻辑
         */
        String classNumGlory(String status,Integer classNumRequired){
            if(!status.equals(TeacherGloryEnum.Status.UNFINISH.value())){
                return status;
            }
            if(null==teacherClassList){
                HashMap cond = Maps.newHashMap();
                cond.put("teacherId",userId);
                teacherClassList = onlineClassDao.findClassByTeacherId(cond);
            };
            if(teacherClassList.size()<classNumRequired){
                return TeacherGloryEnum.Status.UNFINISH.value();
            }
            Long now = Calendar.getInstance().getTime().getTime()/1000;
            Map<String,Object> map = teacherClassList.get(0);
            Long scheduledDateTime = (Long) map.get("scheduledDateTime");
            if(now - scheduledDateTime >= 7*24*3600 + 25*60) {
                return TeacherGloryEnum.Status.EXPIRED.value();
            }else {
                return TeacherGloryEnum.Status.FINISH.value();
            }
        }

        /**
         * Referal类成就通用逻辑
         * @param status
         * @param referalNumRequired
         * @return
         */
        String referalNumGlory(String status,Integer referalNumRequired){
            if(!status.equals(TeacherGloryEnum.Status.UNFINISH.value())){
                return status;
            }
            if(null==teacherReferalList){
                HashMap cond = Maps.newHashMap();
                cond.put("teacherId",userId);
                teacherReferalList = onlineClassDao.findReferalByTeacherId(cond);
            };
            if(teacherReferalList.size()>referalNumRequired){
                Long lastScheduledDateTime = (Long)teacherReferalList.get(0).get("scheduledDateTime");
                Long now = Calendar.getInstance().getTime().getTime()/1000;
                if(null == lastScheduledDateTime || now - lastScheduledDateTime <= 7*24*3600 + 25*60) {
                    return TeacherGloryEnum.Status.FINISH.value();
                }else{
                    return TeacherGloryEnum.Status.EXPIRED.value();
                }
            }
            return status;
        }
    }

}
