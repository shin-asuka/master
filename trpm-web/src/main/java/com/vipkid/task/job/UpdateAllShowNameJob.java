package com.vipkid.task.job;

import com.google.api.client.util.Lists;
import com.vipkid.trpm.dao.TeacherDao;
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
 * Created by liuguowen on 2017/4/20.
 */
@Component
@Vschedule
public class UpdateAllShowNameJob {

    private static final Logger logger = LoggerFactory.getLogger(UpdateAllShowNameJob.class);

    private static final int LINE_PER_PAGE = 1000;

    private static final List<String> RANDOM_CODES = Lists.newArrayList();

    private static final List<String> SENSITIVE_WORDS = Lists.newArrayList();

    private static final String REGULAR = "REGULAR";

    static {
        for (int i = 'A'; i < 'A' + 26; i++) {
            for (int j = 'A'; j < 'A' + 26; j++) {
                StringBuffer s = new StringBuffer();
                s.append((char) i);
                s.append((char) j);
                RANDOM_CODES.add(s.toString());
            }
        }

        SENSITIVE_WORDS.add("BB");
        SENSITIVE_WORDS.add("XX");
        SENSITIVE_WORDS.add("SB");
        SENSITIVE_WORDS.add("BT");
        SENSITIVE_WORDS.add("FK");
        SENSITIVE_WORDS.add("EG");
        SENSITIVE_WORDS.add("FE");
        SENSITIVE_WORDS.add("FO");
        SENSITIVE_WORDS.add("IC");
        SENSITIVE_WORDS.add("IP");
        SENSITIVE_WORDS.add("IQ");
        SENSITIVE_WORDS.add("NM");
        SENSITIVE_WORDS.add("NP");
        SENSITIVE_WORDS.add("OT");
        SENSITIVE_WORDS.add("OZ");
        SENSITIVE_WORDS.add("PO");
        SENSITIVE_WORDS.add("RU");
        SENSITIVE_WORDS.add("SH");
    }

    @Autowired
    private UserDao userDao;

    @Autowired
    private TeacherDao teacherDao;

    @Vschedule
    public void doJob(JobContext jobContext) {
        if (null != jobContext.getData()) {
            if (jobContext.getData().equalsIgnoreCase("FullnameRegular")) {
                logger.info("Do update fullname equals showname regular users...");
                doUpdateFullNameEqualsShowNameUsers(REGULAR);
            }

            if (jobContext.getData().equalsIgnoreCase("FullnameOther")) {
                logger.info("Do update fullname equals showname other users...");
                doUpdateFullNameEqualsShowNameUsers(null);
            }

            if (jobContext.getData().equalsIgnoreCase("ShownameRegular")) {
                logger.info("Do update all showname duplicate regular users...");
                doUpdateAllShowNameDuplicateUsers(REGULAR);
            }

            if (jobContext.getData().equalsIgnoreCase("ShownameOther")) {
                logger.info("Do update all showname duplicate other users...");
                doUpdateAllShowNameDuplicateUsers(null);
            }
        } else {
            logger.info("Not match anything...");
        }
    }

    private void doUpdateFullNameEqualsShowNameUsers(String lifeCycle) {
        int totalPage = getTotalPage();
        logger.info("Find fullname equals showname users total page: {}", totalPage);

        for (int curPage = 1; curPage <= totalPage; curPage++) {
            List<User> userList = userDao.findFullNameEqualsShowNameUsers(lifeCycle, 0, LINE_PER_PAGE);
            logger.info("Find fullname equals showname users at page: {}", curPage);

            if (CollectionUtils.isEmpty(userList)) {
                break;
            }
            logger.info("Find fullname equals showname users number: {}", userList.size());
            doUpdateFullNameEqualsShowName(userList);
        }
    }

    private void doUpdateFullNameEqualsShowName(List<User> userList) {
        if (CollectionUtils.isNotEmpty(userList)) {
            for (User user : userList) {
                if (null == user) {
                    continue;
                }

                String showName = user.getName();
                String currentShowName;

                int duplicates;// showName 重复的标记
                int n = 2;// 添加随机大写字母的个数

                List<String> randomCodeList = Lists.newArrayList();
                int num = 0;
                do {
                    if (StringUtils.isNotBlank(showName)) {
                        currentShowName = showName.substring(0, showName.indexOf(" ") + 1);
                    } else {
                        currentShowName = "";
                    }

                    if (StringUtils.isBlank(currentShowName)) {
                        currentShowName = showName + " ";
                    }
                    String randomCode = StringUtils.EMPTY;// 添加随机字母的变量

                    // 执行随机变量的逻辑
                    for (int j = 0; j < n; j++) {
                        randomCode += (char) (Math.random() * 26 + 'A');
                    }
                    randomCodeList.add(randomCode);

                    if (randomCodeList.containsAll(RANDOM_CODES)) {
                        ++n;
                    }
                    currentShowName += randomCode;

                    // 敏感词过滤
                    duplicates = userDao.findUserCountByShowName(currentShowName);
                    for (String str : SENSITIVE_WORDS) {
                        if (randomCode.indexOf(str) != -1) {
                            duplicates = 1;
                            break;
                        }
                    }

                    logger.info("user :{} 循环次数:{}", user.getId(), num);

                    ++num;
                    if (num > 500 && n == 2) {
                        ++n;
                    }
                } while (duplicates > 0);

                if (!StringUtils.equalsIgnoreCase(currentShowName, showName)) {
                    logger.info("uopdate teacher :{} showName:{}", user.getId(), currentShowName);

                    user.setName(currentShowName);
                    userDao.update(user);
                }
            }
        }
    }

    private void doUpdateAllShowNameDuplicateUsers(String lifeCycle) {
        int totalPage = getTotalPage();
        logger.info("Find all showname duplicate users total page: {}", totalPage);

        for (int curPage = 1; curPage <= totalPage; curPage++) {
            List<User> userList = userDao.findAllShowNameDuplicateUsers(lifeCycle, 0, LINE_PER_PAGE);
            logger.info("Find all showname duplicate users at page: {}", curPage);

            if (CollectionUtils.isEmpty(userList)) {
                break;
            }
            logger.info("Find all showname duplicate users number: {}", userList.size());
            doUpdateShowName(userList);
        }
    }

    private int getTotalPage() {
        int count = userDao.getCount();
        return count % LINE_PER_PAGE == 0 ? count / LINE_PER_PAGE : Math.abs(count / LINE_PER_PAGE) + 1;
    }

    private void doUpdateShowName(List<User> userList) {
        if (CollectionUtils.isNotEmpty(userList)) {
            for (User user : userList) {
                if (null == user) {
                    continue;
                }

                String showName = user.getName();
                int showNumber = userDao.findUserCountByShowName(showName);

                if (showNumber > 1) {
                    String currentShowName;
                    int duplicates;// showName 重复的标记
                    int n = 2;// 添加随机大写字母的个数

                    List<String> randomCodeList = Lists.newArrayList();
                    int num = 0;
                    do {
                        if (StringUtils.isNotBlank(showName)) {
                            currentShowName = showName.substring(0, showName.indexOf(" ") + 1);
                        } else {
                            currentShowName = "";
                        }

                        if (StringUtils.isBlank(currentShowName)) {
                            currentShowName = showName + " ";
                        }

                        String randomCode = StringUtils.EMPTY;// 添加随机字母的变量
                        // 执行随机变量的逻辑
                        for (int j = 0; j < n; j++) {
                            randomCode += (char) (Math.random() * 26 + 'A');
                        }

                        randomCodeList.add(randomCode);
                        if (randomCodeList.containsAll(RANDOM_CODES)) {
                            ++n;
                        }
                        currentShowName += randomCode;

                        // 敏感词过滤
                        duplicates = userDao.findUserCountByShowName(currentShowName);
                        for (String str : SENSITIVE_WORDS) {
                            if (randomCode.indexOf(str) != -1) {
                                duplicates = 1;
                                break;
                            }
                        }

                        logger.info("user :{} 循环次数:{}", user.getId(), num);

                        ++num;
                        if (num > 500 && n == 2) {
                            ++n;
                        }
                    } while (duplicates > 0);
                    logger.info("Update teacher :{} showName:{}", user.getId(), currentShowName);
                    User currentUser = new User();
                    currentUser.setId(user.getId());
                    currentUser.setName(currentShowName);
                    userDao.update(currentUser);
                }
            }
        }
    }

}
