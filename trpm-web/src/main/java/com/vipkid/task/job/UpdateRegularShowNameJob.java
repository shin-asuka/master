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
    private static List<String> RandomAB = Lists.newArrayList();

    static {
        for (int i = 'A'; i < 'A' + 26; i++) {
            for (int j = 'A'; j < 'A' + 26; j++) {
                String s = "";
                s += (char) i;
                s += (char) j;
                RandomAB.add(s);
            }
        }
    }

    private static List<String> SensitiveWords = Lists.newArrayList();

    static {
        SensitiveWords.add("BB");
        SensitiveWords.add("XX");
        SensitiveWords.add("SB");
        SensitiveWords.add("BT");
        SensitiveWords.add("FK");
        SensitiveWords.add("EG");
        SensitiveWords.add("FE");
        SensitiveWords.add("FO");
        SensitiveWords.add("IC");
        SensitiveWords.add("IP");
        SensitiveWords.add("IQ");
        SensitiveWords.add("NM");
        SensitiveWords.add("NP");
        SensitiveWords.add("OT");
        SensitiveWords.add("OZ");
        SensitiveWords.add("PO");
        SensitiveWords.add("RU");
        SensitiveWords.add("SH");


    }

    @Vschedule
    public void doJob(JobContext jobContext) {
        logger.info("【UpdateRegularShowNameJob  】START: ==================================================Time :{}",System.currentTimeMillis());
        String date =  jobContext.getData();
        List<String> userIds =  Lists.newArrayList();//获得teacherId 集合
        if(org.apache.commons.lang.StringUtils.isNotBlank(date)) {
            userIds = Splitter.on(",").trimResults().splitToList(date);
        }
        List<String> userIdList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(userIds)) {
            userIds.forEach(userId -> userIdList.add(userId.trim()));
        }
        List<User> userList =  userDao.findUserNameListByIdList(userIdList);
        if(CollectionUtils.isNotEmpty(userList)){
            for (User user:userList) {
                if (user != null) {
                    String name = user.getName();
                    String showName;
                    int nameNum;//showName 重复的标记
                    int n = 2;//添加随机大写字母的个数
                    List<String> showNameList = Lists.newArrayList();
                    int num = 0;
                    do {
                        if(StringUtils.isNotBlank(name)) {
                            showName = name.substring(0, name.indexOf(" ") + 1);
                        }else{
                            showName ="";
                        }
                        if(StringUtils.isBlank(showName)){
                            showName=name+" ";
                        }
                        String s = StringUtils.EMPTY;//添加随机字母的变量
                        //执行随机变量的逻辑
                        for (int j = 0; j < n; j++) {
                            s += (char) (Math.random() * 26 + 'A');
                        }
                        showNameList.add(s);
                        if (showNameList.containsAll(RandomAB)) {
                            ++n;
                        }
                        showName += s;
                        //敏感词过滤
                        nameNum = userDao.findUserShowNumber(showName);
                        for (String str : SensitiveWords) {
                            if (s.indexOf(str) != -1) {
                                nameNum = 1;
                                break;
                            }
                        }
                        logger.info("user :{} 循环次数:{}", user.getId(),num);
                        ++num;
                    } while (nameNum > 0);
                    if (!StringUtils.equalsIgnoreCase(showName, name)) {
                        logger.info("uopdate teacher :{} showName:{}", user.getId(), showName);
                        user.setName(showName);
                        userDao.update(user);
                    }
                }else{
                    continue;
                }
            }
        }

        logger.info("【UpdateRegularShowNameJob  】END: ==================================================Time :{}",System.currentTimeMillis());

    }
}
