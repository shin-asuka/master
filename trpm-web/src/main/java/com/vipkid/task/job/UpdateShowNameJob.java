package com.vipkid.task.job;

import com.google.api.client.util.Lists;
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
public class UpdateShowNameJob {
    private static final Logger logger = LoggerFactory.getLogger(UpdateShowNameJob.class);
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
        SensitiveWords.add("SB");
    }

    @Vschedule
    public void doJob(JobContext jobContext) {
        logger.info("【updateShowName  】START: ==================================================");
        for (int i = 0; i < 50000; i = i + 1000){
            List<User> userList = userDao.findUserShowNameAndIdList(i);
            if(CollectionUtils.isEmpty(userList)){
                continue;
            }else{
                for (User user:userList) {
                    if (user != null) {
                        String name = user.getName();
                        int showNumber = userDao.findUserShowNumber(name);
                        if (showNumber > 1) {

                            String showName;
                            int nameNum;//showName 重复的标记
                            int n = 2;//添加随机大写字母的个数
                            List<String> showNameList = Lists.newArrayList();

                            do {
                                if(StringUtils.isNotBlank(name)) {
                                    showName = name.substring(0, name.indexOf(" ") + 1);
                                }else{
                                    showName ="";
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
                                for (String str : SensitiveWords) {
                                    if (s.indexOf(str) != -1) {
                                        nameNum = 1;
                                        break;
                                    }
                                }
                                nameNum = userDao.findUserShowNumber(showName);

                            } while (nameNum > 0);
                            logger.info("uopdate teacher :{} showName:{} 编号：{}", user.getId(), showName,i);
                            user.setName(showName);
                            userDao.update(user);
                        }
                    }else{
                        continue;
                    }
                }
            }
        }
        logger.info("【updateShowNmae  】END: ==================================================");
    }
}
