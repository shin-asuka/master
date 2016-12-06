package com.vipkid.recruitment.training.service;

import com.vipkid.enums.TeacherApplicationEnum;
import com.vipkid.recruitment.dao.TeacherApplicationDao;
import com.vipkid.recruitment.entity.TeacherApplication;
import com.vipkid.recruitment.utils.ReturnMapUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.Teacher;

import java.util.List;
import java.util.Map;

@Service
public class TrainingService {
    
    private static Logger logger = LoggerFactory.getLogger(TrainingService.class);

    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private TeacherApplicationDao teacherApplicationDao;
    /**
     * Next --> 更新步骤<br/>
     * @param teacher
     * @return
     */
    public Map<String,Object> toPracticum(Teacher teacher){

        logger.info("用户：{}，更改LifeCycle",teacher.getId());
        List<TeacherApplication> listEntity = teacherApplicationDao.findCurrentApplication(teacher.getId());
        if(CollectionUtils.isEmpty(listEntity)){
            return ReturnMapUtils.returnFail("You have no legal power into the next phase !");
        }

        //执行逻辑 只有在TRAINING的PASS状态才能进入
        if(TeacherApplicationEnum.Status.TRAINING.toString().equals(listEntity.get(0).getStatus())
                && TeacherApplicationEnum.Result.PASS.toString().equals(listEntity.get(0).getResult())){
            teacher.setLifeCycle(LifeCycle.PRACTICUM.toString());
            this.teacherDao.insertLifeCycleLog(teacher.getId(),LifeCycle.TRAINING,LifeCycle.PRACTICUM, teacher.getId());
            this.teacherDao.update(teacher);
            return ReturnMapUtils.returnSuccess();
        }
        return ReturnMapUtils.returnFail("You have no legal power into the next phase !");
    }

}
