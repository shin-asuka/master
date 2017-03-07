package com.vipkid.portal.classroom.controller;

import com.google.common.collect.Lists;
import com.vipkid.http.utils.JsonUtils;
import com.vipkid.portal.classroom.model.mockclass.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MockClassControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(MockClassControllerTest.class);

    @Test
    public void testPeViewOutputDto(){
        PeViewOutputDto peViewOutputDto=new PeViewOutputDto();

        // options
        List<PeOptionDto> optionList = Lists.newArrayList();
        PeOptionDto peOptionDto1 = new PeOptionDto();
        peOptionDto1.setId(1);
        peOptionDto1.setDescription("Yes");
        peOptionDto1.setPoints(1);
        peOptionDto1.setSeq(1);
        peOptionDto1.setChecked(true);
        optionList.add(peOptionDto1);

        PeOptionDto peOptionDto2 = new PeOptionDto();
        peOptionDto2.setId(2);
        peOptionDto2.setDescription("No");
        peOptionDto2.setPoints(0);
        peOptionDto2.setSeq(2);
        peOptionDto2.setChecked(false);
        optionList.add(peOptionDto2);

        // set peCriteriaDto
        List<PeCriteriaDto> criteriaList = Lists.newArrayList();

        PeCriteriaDto peCriteriaDto = new PeCriteriaDto();
        peCriteriaDto.setOptionList(optionList);
        peCriteriaDto.setId(1);
        peCriteriaDto.setPoints(1);
        peCriteriaDto.setTitle("The class background creates a learning atmosphere (visible props, designated area, and optimized for teaching).");
        peCriteriaDto.setType("radio");
        peCriteriaDto.setSeq(1);
        criteriaList.add(peCriteriaDto);

        // set peSectionDto
        List<PeSectionDto> sectionList = Lists.newArrayList();

        PeSectionDto peSectionDto=new PeSectionDto();
        peSectionDto.setCriteriaList(criteriaList);
        peSectionDto.setId(1);
        peSectionDto.setName("Background");
        peSectionDto.setSeq(1);
        sectionList.add(peSectionDto);

        // set peRubricDto
        List<PeRubricDto> rubricList = Lists.newArrayList();

        PeRubricDto peRubricDto = new PeRubricDto();
        peRubricDto.setSectionList(sectionList);
        peRubricDto.setId(1);
        peRubricDto.setName("Professionalism");
        peRubricDto.setSeq(1);
        rubricList.add(peRubricDto);

        peViewOutputDto.setRubricList(rubricList);
        peViewOutputDto.setThingsDidWell("things did well");
        peViewOutputDto.setAreasImprovement("areas improvement");
        peViewOutputDto.setToCoordinator(1);
        peViewOutputDto.setToCoordinatorComment("to coordinator comment");
        peViewOutputDto.setTotalScore(20);
        peViewOutputDto.setStatus("SAVE");
        peViewOutputDto.setApplicationId(16535);

        List<PeTagsDto> tagsList = Lists.newArrayList();
        PeTagsDto peTagsDto = new PeTagsDto();
        peTagsDto.setTagId(12);
        tagsList.add(peTagsDto);
        peViewOutputDto.setTagsList(tagsList);

        List<PeLevelsDto> levelsList = Lists.newArrayList();
        PeLevelsDto peLevelsDto = new PeLevelsDto();
        peLevelsDto.setLevel(10);
        levelsList.add(peLevelsDto);
        peViewOutputDto.setLevelsList(levelsList);

        logger.info(JsonUtils.toJSONString(peViewOutputDto));
    }

}
