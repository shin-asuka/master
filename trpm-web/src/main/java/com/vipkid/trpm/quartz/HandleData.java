package com.vipkid.trpm.quartz;


/**
 * 
 * redis队列操作 和jedi服务器数据请求
 * 
 * @author Along(ZengWeiLong)
 * @ClassName: HandleData
 * @date 2016年4月25日 下午8:47:02
 *
 */
public interface HandleData {

    /**
     * 从redis中获取老师Id
     * 
     * @Author:ALong (ZengWeiLong)
     * @return String
     * @date 2016年4月25日
     */
    public String findTeacherIdByRedis();
}
