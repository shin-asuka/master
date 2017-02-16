package com.vipkid.portal.classroom.util;

import com.vipkid.portal.classroom.model.MajorCommentsVo;
import com.vipkid.portal.classroom.model.PrevipCommentsVo;
import com.vipkid.trpm.entity.teachercomment.SubmitTeacherCommentDto;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by LP-813 on 2017/2/15.
 */
public class Convertor {

    public static SubmitTeacherCommentDto toSubmitTeacherCommentDto(PrevipCommentsVo previpCommentsVo){
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

    public static SubmitTeacherCommentDto toSubmitTeacherCommentDto(MajorCommentsVo classCommentsVo){
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
}
