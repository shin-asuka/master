package com.vipkid.background.service;

import com.alibaba.druid.util.StringUtils;
import com.google.api.client.util.Maps;
import com.vipkid.enums.BackgroundCheckEnum.BackgroundResult;
import com.vipkid.enums.BackgroundCheckEnum.DisputeStatus;
import com.vipkid.enums.BackgroundCheckEnum.BackgroundPhase;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.trpm.dao.BackgroundScreeningDao;
import com.vipkid.trpm.entity.BackgroundScreening;
import com.vipkid.trpm.entity.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;


@Service
public class CommonService {

    @Autowired
    private BackgroundScreeningDao backgroundScreeningDao;
    private static Logger logger = LoggerFactory.getLogger(CommonService.class);

    public Map<String, Object> getStatus(Teacher teacher) {
        Map<String, Object> result = Maps.newHashMap();
        Calendar currnet = Calendar.getInstance();
        currnet.add(Calendar.MONTH, 1);
        BackgroundScreening backgroundScreening = backgroundScreeningDao.findByTeacherId(teacher.getId());
        if (teacher.getContractEndDate().after(currnet.getTime()) || StringUtils.equals(teacher.getLifeCycle(), LifeCycle.CONTRACT_INFO.toString())){
            boolean in5Days = false;
            //TODO
            Date adverseTime = backgroundScreening.getUpdateTime();
            if (adverseTime.after(currnet.getTime())) {
                in5Days = true;
            }
            currnet.add(Calendar.MONTH, -1);
            currnet.add(Calendar.DATE, -5);
            currnet.add(Calendar.YEAR, 2);
            String backgroundResult = backgroundScreening.getResult();
            String disputeStatus = backgroundScreening.getStatus();
            switch (backgroundResult) {
                case "N/A":
                    result.put("phase", BackgroundPhase.PENDING);
                    result.put("result", BackgroundResult.NA);
                    break;
                case "CLEAR":
                    result.put("phase",BackgroundPhase.CLEAR);
                    result.put("result",BackgroundResult.CLEAR);
                    break;
                case "ALERT":
                    if (StringUtils.equalsIgnoreCase(disputeStatus, DisputeStatus.ACTIVE.toString()) && in5Days) {
                        result.put("phase", BackgroundPhase.PREADVERSE);
                    } else {
                        result.put("phase", BackgroundPhase.FALSE);
                    }
                    result.put("result", BackgroundResult.ALERT);
                    break;
            }
                /*if (StringUtils.equalsIgnoreCase(disputeStatus,DisputeStatus.ACTIVE.toString())){
                    result.put("disputeStatus",DisputeStatus.ACTIVE);
                }else {
                    result.put("disputeStatus",DisputeStatus.DEACTIVATED);
                }*/
        }

        return result;
    }

}
