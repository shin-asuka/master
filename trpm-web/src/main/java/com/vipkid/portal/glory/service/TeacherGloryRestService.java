package com.vipkid.portal.glory.service;

import com.google.api.client.util.Lists;
import com.vipkid.enums.TeacherGloryEnum;
import com.vipkid.portal.glory.model.TeacherGlory;
import com.vipkid.trpm.dao.TeacherDao;
import com.vipkid.trpm.entity.Teacher;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by LP-813 on 2017/4/25.
 */
@Service
public class TeacherGloryRestService{

    private static final String NO_GLORY = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";

    @Autowired
    private TeacherDao teacherDao;

    public String[] refeshGlory(String currentGlory,Integer userId) {

        //新建一个成就数组，初始化数据
        String initGlory  = NO_GLORY + "," + new Date().getTime()/1000;
        String[] gloryArr = StringUtils.split(initGlory);

        //加载当前成就
        if(StringUtils.isNotEmpty(currentGlory)) {
            gloryArr = StringUtils.split(currentGlory);
            Long cacheTime = NumberUtils.toLong(gloryArr[gloryArr.length - 1]);

            //距上次成就计算时间不足15min，不再重新计算
            if (new Date().getTime() / 1000 - cacheTime < 15 * 60) {
                return gloryArr;
            }
        }

        //计算成就，更新状态。
        GloryHandler gloryHandler = new GloryHandler();
        gloryArr[0] = gloryHandler.handle1(gloryArr[0],userId);
        gloryArr[1] = gloryHandler.handle2(gloryArr[1]);
        gloryArr[2] = gloryHandler.handle3(gloryArr[2]);
        gloryArr[3] = gloryHandler.handle4(gloryArr[3]);
        gloryArr[4] = gloryHandler.handle5(gloryArr[4]);
        gloryArr[5] = gloryHandler.handle6(gloryArr[5]);
        gloryArr[6] = gloryHandler.handle7(gloryArr[6]);
        gloryArr[7] = gloryHandler.handle8(gloryArr[7]);
        gloryArr[8] = gloryHandler.handle9(gloryArr[8]);
        gloryArr[9] = gloryHandler.handle10(gloryArr[9]);
        gloryArr[10] = gloryHandler.handle11(gloryArr[10]);
        gloryArr[11] = gloryHandler.handle12(gloryArr[11]);
        gloryArr[12] = gloryHandler.handle13(gloryArr[12]);
        gloryArr[13] = gloryHandler.handle14(gloryArr[13]);
        gloryArr[14] = gloryHandler.handle15(gloryArr[14]);
        gloryArr[15] = gloryHandler.handle16(gloryArr[15]);
        gloryArr[16] = String.valueOf(new Date().getTime()/1000);
        return gloryArr;
    }

    public List<TeacherGlory> getGloryView(String[] gloryArr) {
        List<TeacherGlory> teacherGloryList = Lists.newArrayList();
        for(int i=0;i<gloryArr.length;i++) {
            if(TeacherGloryEnum.Status.FINISH.value().equals(gloryArr[i])){
                TeacherGlory teacherGlory = new TeacherGlory();
                if(i==0) {
                    teacherGlory.setGloryId(1);
                    teacherGlory.setName("Become Regular");
                    teacherGlory.setUserId(2040456);
                    teacherGlory.setFinishTime("2017-04-24 16:17:00");
                    teacherGlory.setPriority(1);
                    teacherGlory.setAvatar("boy3");
                    teacherGlory.setTitle("万人迷");
                    teacherGlory.setDescription("在VIPKID被超过10000名学员关注");
                    teacherGlory.setShareTitle("哇！有超过10000名学生喜欢我！");
                    teacherGlory.setShareDescription("想和我⼀一样吗？点击加⼊VIPKID和我们⼀起改变世界吧");
                    teacherGloryList.add(teacherGlory);
                }
            }
        }
        return teacherGloryList;
    }

    public String[] markShownStatus(String[] gloryArr) {
        for(String glory:gloryArr) {
            if (TeacherGloryEnum.Status.FINISH.value().equals(glory)) {
                glory = TeacherGloryEnum.Status.SHOWN.value();
            }
        }
        return gloryArr;
    }

    class GloryHandler{
        //Life Cycle变为Regular
        String handle1(String status,Integer userId){
            Long now = Calendar.getInstance().getTime().getTime()/1000;
            if(status.equals(TeacherGloryEnum.Status.UNFINISH.value())){
                Teacher teacher = teacherDao.findById(userId);
                if(teacher != null && teacher.getEntryDate()!=null) {
                    if(now - teacher.getEntryDate().getTime()/1000 > 7*24*3600){
                        status = TeacherGloryEnum.Status.EXPIRED.value();
                    }else{
                        status = TeacherGloryEnum.Status.FINISH.value();
                    }
                }
            }
            return status;
        };
        //As Schedule课程记录达到1节
        String handle2(String status){
            return null;
        };
        //As Schedule课程记录达到10节
        String handle3(String status){
            return null;
        };
        //As Schedule课程记录达到100节
        String handle4(String status){
            return null;
        };
        //As Schedule课程记录达到500节
        String handle5(String status){
            return null;
        };
        //As Schedule课程记录达到1000节
        String handle6(String status){
            return null;
        };
        //推荐成功的⽼师（有⾄少⼀一节As Schedule完课记录）数量达到1
        String handle7(String status){
            return null;
        };
        //推荐成功的⽼师（有⾄少⼀一节As Schedule完课记录）数量达到10
        String handle8(String status){
            return null;
        };
        //推荐成功的⽼师（有⾄少⼀一节As Schedule完课记录）数量达到100
        String handle9(String status){
            return null;
        };
        //推荐成功的⽼师（有⾄少⼀一节As Schedule完课记录）数量达到500
        String handle10(String status){
            return null;
        };
        //推荐成功的⽼师（有⾄少⼀一节As Schedule完课记录）数量达到1000
        String handle11(String status){
            return null;
        };
        //收到了了第⼀个5苹果家⻓评价
        String handle12(String status){
            return null;
        };
        //Life Cycle变为Regular时间达到100天
        String handle13(String status){
            return null;
        };
        //Life Cycle变为Teaching Prep
        String handle14(String status){
            return null;
        };
        //Life Cycle变为Teaching Prep
        String handle15(String status){
            return null;
        };
        //Life Cycle变为Contract&Info
        String handle16(String status){
            return null;
        };
        //第⼀节Booked Class记录
        String handle17(String status){
            return null;
        };
    }

}
