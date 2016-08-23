package com.vipkid.trpm.quartz;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.api.client.util.Maps;
import com.vipkid.email.EmailEngine;
import com.vipkid.email.handle.EmailConfig.EmailFormEnum;
import com.vipkid.email.templete.TempleteUtils;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.dao.TeacherPeDao;
import com.vipkid.trpm.entity.Teacher;
import com.vipkid.trpm.entity.TeacherPe;
import com.vipkid.trpm.proxy.redis.RedisClient;

@Component
public class PracticumScheduled {

    private static final Logger logger = LoggerFactory.getLogger(PracticumScheduled.class);

    private static final long HOURS_24 = 24 * 60 * 60 * 1000;

    private static final long HOURS_48 = 48 * 60 * 60 * 1000;

    @Autowired
    private TeacherPeDao teacherPeDao;

    @Autowired
    private TeacherDao teacherDao;

    @Scheduled(cron = "0 0 0/2 * * ?")
    public void executeAllocation() {
        logger.info("Practicum allocation start at {}", LocalDateTime.now());

        final String key = "TRPM.PE.executeAllocation";
        if (!RedisClient.me().lock(key)) {
            logger.info("Practicum allocation get lock failed at {}", LocalDateTime.now());
            return;
        }

        try {
            List<TeacherPe> notAllocations = teacherPeDao.findNotAllocations();
            if (CollectionUtils.isNotEmpty(notAllocations)) {
                logger.info("Practicum allocation get datas {} line", notAllocations.size());

                List<Long> teacherIds = teacherPeDao.randomTeachersForPE();
                Iterator<TeacherPe> it = notAllocations.iterator();

                if (CollectionUtils.isNotEmpty(teacherIds)) {
                    for (int i = 0; i < teacherIds.size();) {
                        if (it.hasNext()) {
                            TeacherPe teacherPe = it.next();
                            teacherPe.setPeId(teacherIds.get(i));
                            teacherPe.setExpiredRemind(0);
                            teacherPe.setCreationTime(new Timestamp(System.currentTimeMillis()));
                            // 设置过期时间为48小时之后
                            teacherPe.setExpiredMillis(System.currentTimeMillis() + HOURS_48);
                            teacherPeDao.updateTeacherPe(teacherPe);

                            if (i == (teacherIds.size() - 1)) {
                                i = 0;
                            } else {
                                i++;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        } finally {
            RedisClient.me().unlock(key);
        }

        logger.info("Practicum allocation end at {}", LocalDateTime.now());
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void executeRecycling() {
        logger.info("Practicum recycling start at {}", LocalDateTime.now());

        final String key = "TRPM.PE.executeRecycling";
        if (!RedisClient.me().lock(key)) {
            logger.info("Practicum recycling get lock failed at {}", LocalDateTime.now());
            return;
        }

        List<TeacherPe> notRecyclings = teacherPeDao.findNotRecyclings(System.currentTimeMillis());
        if (CollectionUtils.isNotEmpty(notRecyclings)) {
            logger.info("Practicum recycling get datas {} line", notRecyclings.size());

            Map<Long, Teacher> map = Maps.newHashMap();
            try {
                for (TeacherPe teacherPe : notRecyclings) {
                    long peId = teacherPe.getPeId();
                    teacherPe.setPeId(0);
                    teacherPeDao.updateTeacherPe(teacherPe);

                    // 统计每个PESupervisor的提示数量
                    Teacher peSupervisor = map.get(peId);
                    if (null == peSupervisor) {
                        peSupervisor = teacherDao.findById(peId);
                        peSupervisor.setTeachingExperience(1);
                    } else {
                        peSupervisor
                                .setTeachingExperience(peSupervisor.getTeachingExperience() + 1);
                    }
                    map.put(peId, peSupervisor);
                }
            } catch (Exception e) {
                logger.error("Practicum recycling error", e);
            }

            // 发送邮件
            map.forEach((k, v) -> sendMail(v, "PracticumRecycling.html",
                    "PracticumRecycling-Title.html"));

            // 发送邮件给TQ
            if (!map.isEmpty()) {
                StringBuffer tableDetails = new StringBuffer();
                tableDetails.append("<table border='1' cellpadding='5'>");
                tableDetails.append("<tr><th>Name</th><th>Email</th></tr>");

                map.forEach((k, v) -> tableDetails.append(
                        "<tr><td>" + v.getRealName() + "</td><td>" + v.getEmail() + "</td></tr>"));

                tableDetails.append("</table>");

                try {
                    Map<String, String> paramsMap = Maps.newHashMap();
                    paramsMap.put("tableDetails", tableDetails.toString());

                    Map<String, String> emailMap = new TempleteUtils().readTemplete(
                            "PracticumRemiderTQ.html", paramsMap, "PracticumRemiderTQ-Title.html");

                    new EmailEngine().addMailPool("practicum@vipkid.com.cn", emailMap,
                            EmailFormEnum.TEACHVIP);
                } catch (Exception e) {
                    logger.error("Send TQ mail error", e);
                }
            }
        }

        RedisClient.me().unlock(key);
        logger.info("Practicum recycling end at {}", LocalDateTime.now());
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void executeRemind() {
        logger.info("Practicum remind start at {}", LocalDateTime.now());

        final String key = "TRPM.PE.executeRemind";
        if (!RedisClient.me().lock(key)) {
            logger.info("Practicum remind get lock failed at {}", LocalDateTime.now());
            return;
        }

        List<TeacherPe> notReminds = teacherPeDao.findNotReminds();
        if (CollectionUtils.isNotEmpty(notReminds)) {
            logger.info("Practicum remind get datas {} line", notReminds.size());

            Map<Long, Teacher> map = Maps.newHashMap();
            try {
                for (TeacherPe teacherPe : notReminds) {
                    if ((teacherPe.getExpiredMillis() - System.currentTimeMillis()) <= HOURS_24) {
                        teacherPe.setExpiredRemind(1);
                        teacherPeDao.updateTeacherPe(teacherPe);

                        // 统计每个PESupervisor的提示数量
                        Teacher peSupervisor = map.get(teacherPe.getPeId());
                        if (null == peSupervisor) {
                            peSupervisor = teacherDao.findById(teacherPe.getPeId());
                            peSupervisor.setTeachingExperience(1);
                        } else {
                            peSupervisor.setTeachingExperience(
                                    peSupervisor.getTeachingExperience() + 1);
                        }
                        map.put(teacherPe.getPeId(), peSupervisor);

                        logger.info("Practicum remind for peSupervisor {}",
                                peSupervisor.getRealName());
                    }
                }
            } catch (Exception e) {
                logger.error("Practicum remind error", e);
            }
            // 发送邮件
            map.forEach((k, v) -> sendMail(v, "PracticumReminder.html",
                    "PracticumReminder-Title.html"));
        }

        RedisClient.me().unlock(key);
        logger.info("Practicum remind end at {}", LocalDateTime.now());
    }

    private void sendMail(Teacher peSupervisor, String contentTempateName,
            String titleTemplateName) {
        try {
            Map<String, String> paramsMap = Maps.newHashMap();
            paramsMap.put("PESupervisor", peSupervisor.getRealName());
            paramsMap.put("teachingExperience",
                    String.valueOf(peSupervisor.getTeachingExperience()));

            TempleteUtils templeteUtils = new TempleteUtils();
            Map<String, String> emailMap =
                    templeteUtils.readTemplete(contentTempateName, paramsMap, titleTemplateName);

            new EmailEngine().addMailPool(peSupervisor.getEmail(), emailMap,
                    EmailFormEnum.TEACHVIP);
        } catch (Exception e) {
            logger.error("Send mail error", e);
        }
    }

}
