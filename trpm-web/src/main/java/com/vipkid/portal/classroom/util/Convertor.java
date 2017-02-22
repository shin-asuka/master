package com.vipkid.portal.classroom.util;

import com.vipkid.portal.classroom.model.MajorCommentsVo;
import com.vipkid.portal.classroom.model.PrevipCommentsVo;
import com.vipkid.portal.classroom.model.bo.MajorCommentsBo;
import com.vipkid.portal.classroom.model.bo.PrevipCommentsBo;
import com.vipkid.trpm.entity.teachercomment.SubmitTeacherCommentDto;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by LP-813 on 2017/2/15.
 */
public class Convertor {

    public static SubmitTeacherCommentDto toSubmitTeacherCommentDto(PrevipCommentsBo previpCommentsVo){
        SubmitTeacherCommentDto submitTeacherCommentDto = new SubmitTeacherCommentDto();
        try {
            BeanUtils.copyProperties(submitTeacherCommentDto,previpCommentsVo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return submitTeacherCommentDto;
    }

    public static SubmitTeacherCommentDto toSubmitTeacherCommentDto(MajorCommentsBo classCommentsVo){
        SubmitTeacherCommentDto submitTeacherCommentDto = new SubmitTeacherCommentDto();
        try {
            BeanUtils.copyProperties(submitTeacherCommentDto,classCommentsVo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return submitTeacherCommentDto;
    }

    public static <T> PrevipCommentsBo toPrevipCommentsBo(T vo){
        PrevipCommentsBo bo = new PrevipCommentsBo();
        try {
            BeanUtils.copyProperties(bo,vo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return bo;
    }

    public static <T> MajorCommentsBo toMajorCommentsBo(T vo){
        MajorCommentsBo bo = new MajorCommentsBo();
        try {
            BeanUtils.copyProperties(bo,vo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return bo;
    }
}
