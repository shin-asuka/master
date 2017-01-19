package com.vipkid.trpm.service.pe;

import com.vipkid.trpm.dao.TeacherPeTagsDao;
import com.vipkid.trpm.entity.TeacherPeTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by liuguowen on 2017/1/16.
 */
@Service
public class TeacherPeTagsService {

    @Autowired
    private TeacherPeTagsDao teacherPeTagsDao;

    public void updatePeTags(int applicationId, List<TeacherPeTags> teacherPeTags) {
        teacherPeTagsDao.deleteTeacherPeTags(applicationId);
        teacherPeTagsDao.saveTeacherPeTags(teacherPeTags);
    }

}
