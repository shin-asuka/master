package com.vipkid.trpm.dao;

import com.vipkid.trpm.entity.TeacherGatedLaunch;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by luning on 2017/3/27.
 */
@Repository
public interface TeacherGatedLaunchDao {
    int countByTeacherId(Long teacherId);
}
