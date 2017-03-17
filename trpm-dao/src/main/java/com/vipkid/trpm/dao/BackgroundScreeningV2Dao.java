package com.vipkid.trpm.dao;

import com.vipkid.trpm.entity.BackgroundScreening;

import java.util.List;

/**
 * Created by liyang on 2017/3/15.
 */
public interface BackgroundScreeningV2Dao {

    Long insert(BackgroundScreening backgroundScreening);

    int update(BackgroundScreening backgroundScreening);

    BackgroundScreening findByTeacherIdTopOne(Long teacherId);

    BackgroundScreening findById(Long id);

    List<Long> findIdByResult(String result);

    List<Long> findTeacherIdBycandidateIdNone();
}
