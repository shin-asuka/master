package com.vipkid.trpm.dao;

import java.util.List;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.AppOnlineClass;
import com.vipkid.trpm.entity.AppRestful;

@Repository
public class AppRestfulDao extends MapperDaoTemplate<AppRestful> {
    
    @Autowired
    public AppRestfulDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, AppRestful.class);
    }  
    
    /**
     *  统计一段时间里课程数量按天分布
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @param timezone
     * @param startTime
     * @param endTime
     * @param classStatuses
     * @param classTypes
     * @return List<Map<String,Object>>
     * @date 2016年6月12日
     */
    public List<Map<String, Object>> appRestfulCountOnlineClass(long teacherId,String timezone,String startTime,String endTime,String[] classStatuses,String[] classTypes){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", teacherId);
        paramsMap.put("startTime", startTime);
        paramsMap.put("endTime", endTime);
        paramsMap.put("timezone",timezone);
        paramsMap.put("classStatus",classStatuses);
        paramsMap.put("classType",classTypes);
        return listEntity("AppRestfulCountOnlineClass", paramsMap);
    }
    
    /**
     *  统计一段时间里课程信息列表
     * @Author:ALong (ZengWeiLong)
     * @param teacherId
     * @param timezone
     * @param startTime
     * @param endTime
     * @param classStatuses
     * @param classTypes
     * @return List<Map<String,Object>>
     * @date 2016年6月12日
     */
    public List<AppOnlineClass> appRestfulListOnlineClass(long teacherId,long startTime,long endTime,Integer order,String[] classStatuses,String[] classTypes){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", teacherId);
        paramsMap.put("startTime", startTime);
        paramsMap.put("endTime", endTime);
        paramsMap.put("classStatus",classStatuses);
        paramsMap.put("classType",classTypes);
        paramsMap.put("order", order);
        return listEntity("appRestfulListOnlineClass", paramsMap);
    }
    
    public List<AppOnlineClass> appRestfulListForPage(long teacherId,long start,long limit,long order,int classStatus,String[] classTypes){
        Map<String, Object> paramsMap = Maps.newHashMap();
/*        start = start < 0 ? 0:start;
        long startNum = (start-1) * limit;
        startNum = startNum < 0 ? 0 : startNum;
        long endNum = start * limit;*/
        paramsMap.put("teacherId", teacherId);
        paramsMap.put("startNum", start);
        paramsMap.put("endNum", limit);
        paramsMap.put("order", order);
        paramsMap.put("classStatus",classStatus);
        paramsMap.put("currTime", System.currentTimeMillis());
        paramsMap.put("classType",classTypes);
        return listEntity("appRestfulListForPage", paramsMap);
    }
    
    public Map<String,Object>  appRestfulListForCount(long teacherId,long classStatus,String[] classTypes){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", teacherId);
        paramsMap.put("classStatus",classStatus);
        paramsMap.put("currTime", System.currentTimeMillis());
        paramsMap.put("classType",classTypes);
        return selectEntity("appRestfulListForCount", paramsMap);
    }
    
    public Map<String,Object> findAppTokenByTeacherId(long teacherId){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", teacherId);
        return super.selectEntity("findByAppTokenById", paramsMap);
    }
    
    public Map<String,Object> findIdByAppToken(String appToken){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("appToken", appToken);
        return super.selectEntity("findByAppTokenById", paramsMap);
    }
    
    public List<Map<String, Object>> selectFeedback(List<Long> onlineClassIds){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("onlineClassIds", onlineClassIds);
        return super.listEntity("selectFeedback", paramsMap);
    }   
    
    public Integer saveAppToken(long teacherId,String appToken){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", teacherId);
        paramsMap.put("appToken", appToken);
        return super.getSqlSession().insert("saveTeacherToken", paramsMap);
    }
    
    public Integer updateTeacherToken(long id,String appToken){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("id", id);
        paramsMap.put("appToken", appToken);
        return super.getSqlSession().update("updateTeacherToken", paramsMap);
    }
}
