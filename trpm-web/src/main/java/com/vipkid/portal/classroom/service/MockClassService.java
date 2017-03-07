package com.vipkid.portal.classroom.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.vipkid.portal.classroom.model.mockclass.*;
import com.vipkid.portal.classroom.util.BeanUtils;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.trpm.dao.*;
import com.vipkid.trpm.entity.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    private TeacherPeTagsDao teacherPeTagsDao;

    @Autowired
    private TeacherPeLevelsDao teacherPeLevelsDao;

    @Autowired
    private TeacherPeRubricDao teacherPeRubricDao;

    @Autowired
    private TeacherPeSectionDao teacherPeSectionDao;

    @Autowired
    private TeacherPeCriteriaDao teacherPeCriteriaDao;

    @Autowired
    private TeacherPeOptionDao teacherPeOptionDao;

    @Autowired
    private TeacherPeResultDao teacherPeResultDao;

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
        BeanUtils.copyPropertys(teacherPeComments, peViewOutputDto);

        // 查询 result 列表
        List<TeacherPeResult> peResultList = teacherPeResultDao.listTeacherPeResult(applicationId);
        // 查询 rubric 列表
        List<PeRubricDto> rubricDtoList = listPeRubric(teacherPeComments.getTemplateId(), peResultList);
        peViewOutputDto.setRubricList(rubricDtoList);

        // set tags
        List<TeacherPeTags> peTagsList = teacherPeTagsDao.getTeacherPeTagsByApplicationId(applicationId);
        if (CollectionUtils.isNotEmpty(peTagsList)) {
            List<Integer> tagIds =
                            peTagsList.stream().map(peTags -> new Integer(peTags.getId())).collect(Collectors.toList());
            peViewOutputDto.setTagsList(tagIds);
        }

        // set levels
        List<TeacherPeLevels> peLevelsList = teacherPeLevelsDao.getTeacherPeLevelsByApplicationId(applicationId);
        if (CollectionUtils.isNotEmpty(peLevelsList)) {
            List<Integer> levelIds = peLevelsList.stream().map(peLevels -> new Integer(peLevels.getId()))
                            .collect(Collectors.toList());
            peViewOutputDto.setLevelsList(levelIds);
        }

        return peViewOutputDto;
    }

    public TeacherPeTemplate getCurrentPeTemplate() {
        TeacherPeTemplate teacherPeTemplate = teacherPeTemplateDao.getCurrentPeTemplate();
        return Preconditions.checkNotNull(teacherPeTemplate, "Pe current template is not found!");
    }

    public TeacherPeTemplate getPeTempate(Integer id) {
        TeacherPeTemplate teacherPeTemplate = teacherPeTemplateDao.getPeTemplate(id);
        return Preconditions.checkNotNull(teacherPeTemplate, "Pe template is not found!");
    }

    public List<PeRubricDto> listPeRubric(Integer templateId, List<TeacherPeResult> peResultList) {
        List<PeRubricDto> rubricDtoList = Lists.newLinkedList();
        List<TeacherPeRubric> peRubricList = teacherPeRubricDao.listTeacherPeRubric(templateId);

        if (CollectionUtils.isNotEmpty(peRubricList)) {
            for (TeacherPeRubric peRubric : peRubricList) {
                List<PeSectionDto> peSectionDtoList = Lists.newLinkedList();
                List<TeacherPeSection> peSectionList = teacherPeSectionDao.listTeacherPeSection(peRubric.getId());

                if (CollectionUtils.isNotEmpty(peSectionList)) {
                    for (TeacherPeSection peSection : peSectionList) {
                        List<PeCriteriaDto> peCriteriaDtoList = Lists.newLinkedList();
                        List<TeacherPeCriteria> peCriteriaList =
                                        teacherPeCriteriaDao.listTeacherPeCriteria(peSection.getId());

                        if (CollectionUtils.isNotEmpty(peCriteriaList)) {
                            for (TeacherPeCriteria peCriteria : peCriteriaList) {
                                List<PeOptionDto> peOptionDtoList = Lists.newLinkedList();
                                List<TeacherPeOption> peOptionList =
                                                teacherPeOptionDao.listTeacherPeOption(peCriteria.getId());

                                if (CollectionUtils.isNotEmpty(peOptionList)) {
                                    for (TeacherPeOption peOption : peOptionList) {
                                        // set option
                                        PeOptionDto peOptionDto = new PeOptionDto();
                                        peOptionDto.setChecked(false);
                                        BeanUtils.copyPropertys(peOption, peOptionDto);

                                        if (CollectionUtils.isNotEmpty(peResultList)) {
                                            peResultList.stream().forEach(peResult -> {
                                                if (peResult.getId() == peOptionDto.getId()) {
                                                    peOptionDto.setChecked(true);
                                                }
                                            });
                                        }
                                        peOptionDtoList.add(peOptionDto);
                                    }
                                }

                                // set criteria
                                PeCriteriaDto peCriteriaDto = new PeCriteriaDto();
                                BeanUtils.copyPropertys(peCriteria, peCriteriaDto);
                                peCriteriaDto.setOptionList(peOptionDtoList);
                                peCriteriaDtoList.add(peCriteriaDto);
                            }
                        }

                        // set section
                        PeSectionDto peSectionDto = new PeSectionDto();
                        BeanUtils.copyPropertys(peSection, peSectionDto);
                        peSectionDto.setCriteriaList(peCriteriaDtoList);
                        peSectionDtoList.add(peSectionDto);
                    }
                }

                // set rubric
                PeRubricDto peRubricDto = new PeRubricDto();
                BeanUtils.copyPropertys(peRubric, peRubricDto);
                peRubricDto.setSectionList(peSectionDtoList);
                rubricDtoList.add(peRubricDto);
            }
        }

        return rubricDtoList;
    }

}
