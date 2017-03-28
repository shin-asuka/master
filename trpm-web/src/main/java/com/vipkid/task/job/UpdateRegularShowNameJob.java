package com.vipkid.task.job;

import com.google.api.client.util.Lists;
import com.google.common.base.Splitter;
import com.vipkid.trpm.dao.UserDao;
import com.vipkid.trpm.entity.User;
import com.vipkid.vschedule.client.common.Vschedule;
import com.vipkid.vschedule.client.schedule.JobContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zhangzhaojun on 2017/3/8.
 */
@Component
@Vschedule
public class UpdateRegularShowNameJob {
    private static final Logger logger = LoggerFactory.getLogger(UpdateRegularShowNameJob.class);
    @Autowired
    private UserDao userDao;
    private static List<String> RandomCodeList = Lists.newArrayList();

    static {
        for (int i = 'A'; i < 'A' + 26; i++) {
            for (int j = 'A'; j < 'A' + 26; j++) {
                String s = "";
                s += (char) i;
                s += (char) j;
                RandomCodeList.add(s);
            }
        }
    }

    private static List<String> SensitiveWordsList = Lists.newArrayList();

    static {
        SensitiveWordsList.add("BB");
        SensitiveWordsList.add("XX");
        SensitiveWordsList.add("SB");
        SensitiveWordsList.add("BT");
        SensitiveWordsList.add("FK");
        SensitiveWordsList.add("EG");
        SensitiveWordsList.add("FE");
        SensitiveWordsList.add("FO");
        SensitiveWordsList.add("IC");
        SensitiveWordsList.add("IP");
        SensitiveWordsList.add("IQ");
        SensitiveWordsList.add("NM");
        SensitiveWordsList.add("NP");
        SensitiveWordsList.add("OT");
        SensitiveWordsList.add("OZ");
        SensitiveWordsList.add("PO");
        SensitiveWordsList.add("RU");
        SensitiveWordsList.add("SH");


    }

    @Vschedule
    public void doJob(JobContext jobContext) {
        long startTime = System.currentTimeMillis();
        logger.info("【UpdateRegularShowNameJob  】START: ==================================================startTime :{}",startTime);
        String data =  jobContext.getData();
        List<String> userIds =  Lists.newArrayList();//获得teacherId 集合
        if(org.apache.commons.lang.StringUtils.isNotBlank(data)) {
            userIds = Splitter.on(",").trimResults().splitToList(data);
        }
        List<String> userIdList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(userIds)) {
            userIds.forEach(userId -> userIdList.add(userId.trim()));
        }
        List<User> userList =  userDao.findUserNameListByIdList(userIdList);
        if(CollectionUtils.isNotEmpty(userList)){
            for (User user : userList) {
                if (user != null) {
                    String showName = user.getName();
                    String currentShowName;
                    int duplicates;//showName 重复的标记
                    int n = 2;//添加随机大写字母的个数
                    List<String> randomCodeList = Lists.newArrayList();
                    int num = 0;
                    do {
                        if(StringUtils.isNotBlank(showName)) {
                            currentShowName = showName.substring(0, showName.indexOf(" ") + 1);
                        }else{
                            currentShowName ="";
                        }
                        if(StringUtils.isBlank(currentShowName)){
                            currentShowName=showName+" ";
                        }
                        String  randomCode = StringUtils.EMPTY;//添加随机字母的变量
                        //执行随机变量的逻辑
                        for (int j = 0; j < n; j++) {
                            randomCode += (char) (Math.random() * 26 + 'A');
                        }
                        randomCodeList.add( randomCode);
                        if (randomCodeList.containsAll(RandomCodeList)) {
                            ++n;
                        }
                        currentShowName +=  randomCode;
                        //敏感词过滤
                        duplicates = userDao.findUserCountByShowName(currentShowName);
                        for (String str : SensitiveWordsList) {
                            if ( randomCode.indexOf(str) != -1) {
                                duplicates = 1;
                                break;
                            }
                        }
                        logger.info("user :{} 循环次数:{}", user.getId(),num);
                        ++num;
                        if(num>500&&n==2){
                            ++n;
                        }
                    } while (duplicates > 0);
                    if (!StringUtils.equalsIgnoreCase(currentShowName, showName)) {
                        logger.info("uopdate teacher :{} showName:{}", user.getId(), currentShowName);
                        user.setName(currentShowName);
                        userDao.update(user);
                    }
                }else{
                    continue;
                }
            }
        }
        long endTime = System.currentTimeMillis();
        logger.info("【UpdateRegularShowNameJob  】END: ==================================================Time :{}",endTime-startTime);

    }
}
