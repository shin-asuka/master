package com.vipkid.portal.classroom.service;

import com.google.common.base.Preconditions;
import com.vipkid.portal.classroom.model.mockclass.PeViewOutputDto;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.TeacherPeComments;
import com.vipkid.trpm.entity.TeacherPeTemplate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.vipkid.enums.TeacherApplicationEnum.Result.REAPPLY;

@Service
public class MockClassService {

    private static Logger logger = LoggerFactory.getLogger(MockClassService.class);

    @Autowired
    private TeacherPeTemplateDao teacherPeTemplateDao;

    @Autowired
    private TeacherApplicationDao teacherApplicationDao;

    @Autowired
    private TeacherPeCommentsDao teacherPeCommentsDao;

    @Autowired
    private TeacherPeRubricDao teacherPeRubricDao;

    @Autowired
    private TeacherPeSectionDao teacherPeSectionDao;

    @Autowired
    private TeacherPeCriteriaDao teacherPeCriteriaDao;

    @Autowired
    private TeacherPeOptionDao teacherPeOptionDao;

    public PeViewOutputDto doPeView(Integer applicationId) {
        TeacherApplication teacherApplication = teacherApplicationDao.findApplictionById(applicationId);
        Preconditions.checkNotNull(teacherApplication, "Teacher application is not found!");

        PeViewOutputDto peViewOutputDto = new PeViewOutputDto();

        // 如果结果是 REAPPLY 则直接返回
        if (StringUtils.equals(teacherApplication.getResult(), REAPPLY.name())) {
            peViewOutputDto.setStatus(teacherApplication.getResult());
            return peViewOutputDto;
        }

        TeacherPeComments teacherPeComments = teacherPeCommentsDao.getTeacherPeComments(applicationId);
        // 首次进入初始化 Pe Comments
        if (null == teacherPeComments) {
            teacherPeComments = new TeacherPeComments();
            teacherPeComments.setApplicationId(applicationId);
            teacherPeComments.setTemplateId(getCurrentPeTemplate().getId());
        }

        return null;
    }

    public TeacherPeTemplate getCurrentPeTemplate() {
        TeacherPeTemplate teacherPeTemplate = teacherPeTemplateDao.getCurrentPeTemplate();
        return Preconditions.checkNotNull(teacherPeTemplate, "Pe current template is not found!");
    }

    public TeacherPeTemplate getPeTempate(Integer id) {
        TeacherPeTemplate teacherPeTemplate = teacherPeTemplateDao.getPeTemplate(id);
        return Preconditions.checkNotNull(teacherPeTemplate, "Pe template is not found!");
    }

}
