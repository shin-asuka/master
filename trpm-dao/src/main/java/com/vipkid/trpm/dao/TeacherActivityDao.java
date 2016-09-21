package com.vipkid.trpm.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.TeacherActivity;

@Repository
public class TeacherActivityDao extends MapperDaoTemplate<TeacherActivity> {
    
    @Autowired
    public TeacherActivityDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherActivity.class);
    }
    
    /**
     * 查询老师在一年内上了多少次课
     * @Author:ALong (ZengWeiLong)
     * @param id
     * @param year
     * @return    
     * List<Map<String,Object>>
     * @date 2016年3月18日
     */
    public int countClassByTeacherId(long id,String yearmd){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", id);
        paramsMap.put("yearmd", yearmd);
        return selectCount("countClassByTeacherId", paramsMap);
    }
       /**
     * 查询老师在一年内教了多少学生
     * @Author:ALong (ZengWeiLong)
     * @param id
     * @param year
     * @return    
     * List<Map<String,Object>>
     * @date 2016年3月18日
     */
    public List<Map<String, Object>> countStudentByTeacherId(long id,String yearmd){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", id);
        paramsMap.put("yearmd", yearmd);        
        return listEntity("countStudentByTeacherId", paramsMap);
    }
    
    /**
     * 查询上的课程最多的学生 
     * @Author:ALong (ZengWeiLong)
     * @param id
     * @param year  @return    
     * List<Map<String,Object>>
     * @date 2016年3月18日
     */
    public List<Map<String, Object>> countStudentByMax(long id,String yearmd){
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("teacherId", id);
        paramsMap.put("yearmd", yearmd);        
        return listEntity("countStudentByMax", paramsMap);
    }
    
    /**
     * 查询某位老师推荐的所有老师的人数
     * @Author:zhangbole
     * @return int
     * @date 2016年9月20日
     */
    public int getNumOfTeachersByReferee(long id) {
	    if(id <= 0) return 0;
	    Map<String, Object> paramsMap = Maps.newHashMap();
	    String s = id+",%";
	    paramsMap.put("id", s);
	    return super.selectCount("getNumOfTeachersByReferee", paramsMap);
	}
    
    public List<String> getAvatarListOfTeachersByReferee(long id){
    	if(id <= 0) return null;
    	Map<String, Object> paramsMap = Maps.newHashMap();
	    String s = id+",%";
	    paramsMap.put("id", s);
	    return super.listEntity("getAvatarListOfTeachersByReferee", paramsMap);
    }
    
    /**
	 * 查询某位老师教过的学生数量
	 * 
	 * @Author:zhangbole
	 * @param teacherId
	 *            老师ID
	 * @return int
	 * @date 2016年9月21日
	 */
	public int countStuNumOfOneTeacher(long teacherId){
		if(teacherId <= 0)  return 0;
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("id", teacherId);
		return super.selectCount("countStuNumOfOneTeacher", paramsMap);
	}
	
	/**
	 * 查询某位老师教过的学生id列表，与学生上课的节数
	 * 
	 * @Author:zhangbole
	 * @param teacherId 老师ID
	 * @return List<Integer>
	 * @date 2016年9月21日
	 */
	public List<Map> getStudentListOfOneTeacher(long teacherId){
		if(teacherId <= 0 ) return null;
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("id", teacherId);
		List<Map> ret =  super.listEntity("getStudentListOfOneTeacher", paramsMap);
		return ret;
	}
	
	/**
	 * 查询某位老师上课的节数
	 * 
	 * @Author:zhangbole
	 * @param teacherId 老师ID
	 * @return int
	 * @date 2016年9月21日
	 */
	public int getClassNumOfOneTeacher(long teacherId){
		if(teacherId<=0) return 0;
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("id", teacherId);
		return super.selectCount("getClassNumOfOneTeacher", paramsMap);
	}
	
	/**
	 * 查询某位老师第一次上课的日期（这节课不要求AS_SCHEDULED）
	 * 
	 * @Author:zhangbole
	 * @param teacherId 老师ID
	 * @return int
	 * @date 2016年9月27日
	 */
	public Timestamp getFirstClassDateofOneTeacher(long teacherId){
		if(teacherId<=0) return null;
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("id", teacherId);
		Timestamp timestamp = super.selectEntity("getFirstClassDateofOneTeacher", paramsMap);
		return  timestamp;
		
	}

}
