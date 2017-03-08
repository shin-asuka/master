package com.vipkid.trpm.service.pe;

import com.vipkid.trpm.dao.TeacherPeResultDao;
import com.vipkid.trpm.entity.TeacherPeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherPeResultService {

    @Autowired
    private TeacherPeResultDao teacherPeResultDao;

    public void updateTeacherPeResults(int applicationId, List<TeacherPeResult> teacherPeLevels) {
        teacherPeResultDao.deleteTeacherPeResults(applicationId);
        teacherPeResultDao.saveTeacherPeResults(teacherPeLevels);
    }

}
