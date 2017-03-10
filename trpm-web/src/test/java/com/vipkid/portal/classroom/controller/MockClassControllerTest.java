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
    public void testpeReviewOutputDto() {
        PeReviewOutputDto peReviewOutputDto = new PeReviewOutputDto();

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
        peCriteriaDto.setTitle(
                        "The class background creates a learning atmosphere (visible props, designated area, and optimized for teaching).");
        peCriteriaDto.setType("radio");
        peCriteriaDto.setSeq(1);
        criteriaList.add(peCriteriaDto);

        // set peSectionDto
        List<PeSectionDto> sectionList = Lists.newArrayList();

        PeSectionDto peSectionDto = new PeSectionDto();
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

        peReviewOutputDto.setRubricList(rubricList);
        peReviewOutputDto.setThingsDidWell("things did well");
        peReviewOutputDto.setAreasImprovement("areas improvement");
        peReviewOutputDto.setToCoordinator(1);
        peReviewOutputDto.setToCoordinatorComment("to coordinator comment");
        peReviewOutputDto.setTotalScore(20);
        peReviewOutputDto.setStatus("SAVE");
        peReviewOutputDto.setApplicationId(16535);
        peReviewOutputDto.setTeachTrailClass(0);

        List<Integer> tagsList = Lists.newArrayList();
        tagsList.add(12);
        peReviewOutputDto.setTagsList(tagsList);

        List<Integer> levelsList = Lists.newArrayList();
        levelsList.add(10);
        peReviewOutputDto.setLevelsList(levelsList);

        logger.info(JsonUtils.toJSONString(peReviewOutputDto));
    }

    @Test
    public void testPeDoAuditInputDto() {
        PeDoAuditInputDto peDoAuditInputDto = new PeDoAuditInputDto();

        peDoAuditInputDto.setThingsDidWell("things did well");
        peDoAuditInputDto.setAreasImprovement("areas improvement");
        peDoAuditInputDto.setToCoordinator(1);
        peDoAuditInputDto.setToCoordinatorComment("to coordinator comment");
        peDoAuditInputDto.setStatus("SAVE");
        peDoAuditInputDto.setApplicationId(16535);
        peDoAuditInputDto.setTeachTrailClass(0);

        peDoAuditInputDto.setPeId(132243);

        List<Integer> tagsList = Lists.newArrayList();
        tagsList.add(12);
        peDoAuditInputDto.setTagsList(tagsList);

        List<Integer> levelsList = Lists.newArrayList();
        levelsList.add(10);
        peDoAuditInputDto.setLevelsList(levelsList);

        List<Integer> optionList = Lists.newArrayList();
        optionList.add(21);
        optionList.add(23);
        peDoAuditInputDto.setOptionList(optionList);

        logger.info(JsonUtils.toJSONString(peDoAuditInputDto));
    }

    @Test
    public void testCandidateFeedbackInputDto() {
        CandidateFeedbackInputDto candidateFeedbackInputDto = new CandidateFeedbackInputDto();
        candidateFeedbackInputDto.setApplicationId(231123);
        candidateFeedbackInputDto.setFriendly("friendly");
        candidateFeedbackInputDto.setHelpful("very helpful");
        candidateFeedbackInputDto.setInstructions("clear");
        candidateFeedbackInputDto.setPe("Sara H");
        candidateFeedbackInputDto.setRate(3);
        candidateFeedbackInputDto.setSuggestions("suggestions");
        candidateFeedbackInputDto.setToMentor("to mentor");
        candidateFeedbackInputDto.setCandidate("Bruce Lee");

        logger.info(JsonUtils.toJSONString(candidateFeedbackInputDto));
    }

}
