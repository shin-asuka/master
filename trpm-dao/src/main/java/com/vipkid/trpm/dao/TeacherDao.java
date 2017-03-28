package com.vipkid.trpm.dao;

import com.google.common.collect.Maps;
import com.vipkid.enums.TeacherEnum.LifeCycle;
import com.vipkid.rest.dto.ReferralTeacherDto;
import com.vipkid.trpm.entity.Teacher;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TeacherDao extends MapperDaoTemplate<Teacher> {
	
	private static Logger logger = LoggerFactory.getLogger(TeacherDao.class);
	
    @Autowired
    public TeacherDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, Teacher.class);
    }

    public Teacher findById(long id) {
        if (id == 0)
            return null;
        Teacher teacher = selectOne(new Teacher().setId(id));
        if(null != teacher){
        	 teacher.setCountry(NationalityTransfer.getRestoreNationality(teacher.getCountry()));
        }
        return teacher;
    }
    
    public List<Map<String,Object>> findTeacher(Map<String,Object> paramMap){
       return super.listEntity("findTeacher",paramMap);
    }

    @Override
    public int update(Teacher teacher) {
        teacher.setCountry(NationalityTransfer.getNationality(teacher.getCountry()));
        return super.update(teacher);
    }

    @Override
    public int save(Teacher teacher) {
        teacher.setCountry(NationalityTransfer.getNationality(teacher.getCountry()));
        return super.save(teacher);
    }

    //
    public Teacher findByRecruitToken(String strToken) {
        if (StringUtils.isEmpty(strToken))
            return null;
        return selectOne(new Teacher().setRecruitmentId(strToken));
    }

    /**
     * 获取可用的serialNumber
     *
     * @param
     * @return
     */
    public String getSerialNumber() {
        Teacher teacher = new Teacher();
        int count = this.selectCount(teacher, "getMaxSerialNumber");
        String serialNumber = String.format("%05d", count++);
        while (!checkSerialNumber(serialNumber)) {//
            serialNumber = String.format("%05d", count++);
        }
        return serialNumber;
    }

    /**
     * 检查生产的serialNumber是否可用
     *
     * @param serialNumber
     * @return
     */
    public boolean checkSerialNumber(String serialNumber) {
        Teacher t = new Teacher();
        t.setSerialNumber(serialNumber);
        int count = this.selectCount(t);
        return count == 0 ? true : false;

    }

    /**
     * 查询所有Regular老师Id 放入Redis
     *
     * @Author:ALong (ZengWeiLong)
     * @return List<String>
     * @date 2016年4月21日
     */
    public List<String> findAllRegular() {
        return super.listEntity("findAllRegularId", Maps.newHashMap());
    }

    /**
     * 查询所有Regular老师Id,不带Like条件
     * @return
     */
    public List<Long> findRegularIdNoLike(Map<String,Object> map){
        return super.listEntity("findRegularIdNoLike", map);
    }
    /**
     * 查询所有没有环信id的Regular老师Id
     *
     * @Author:ALong (ZengWeiLong)
     * @return List<String>
     * @date 2016年4月21日
     */
    public List<String> findAllRegularButNoHuanxinId() {
        return super.listEntity("findAllRegularButNoHuanxinId", Maps.newHashMap());
    }



    public Teacher findByEmail(String email) {
        try {
            return selectOne(new Teacher().setEmail(email));
        } catch (Exception e) {
            return new Teacher();
        }
    }

    public void insertLifeCycleLog(long teacherId,LifeCycle fromStatus,LifeCycle toStatus,long operatorId){
        Map<String,Object> paramMap = Maps.newHashMap();
        paramMap.put("teacherId", teacherId);
        paramMap.put("fromStatus", fromStatus);
        paramMap.put("toStatus", toStatus);
        paramMap.put("operatorId", operatorId);
        super.getSqlSession().insert("insertLifeCycleLog", paramMap);
    }

    public List<Teacher> findByIds(List<Long> ids) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("ids", ids);
        return listEntity("findTeachersByIds", paramsMap);
    }

    public List<ReferralTeacherDto> findReferralTeachers(Map<String, Object> paramsMap){
        return listEntity("findReferralTeachers", paramsMap);
    }

    public Integer findReferralTeachersCount(Map<String, Object> paramsMap){
        return selectCount("findReferralTeachersCount", paramsMap);
    }

    public List<ReferralTeacherDto> listReferralSucceedTeachers(Map<String, Object> paramsMap) {
        return listEntity("listReferralSucceedTeachers", paramsMap);
    }


    public Integer countReferralSucceedTeachers(Map<String, Object> paramsMap) {
        return selectCount("countReferralSucceedTeachers", paramsMap);
    }

    public List<ReferralTeacherDto> listReferralProcessingTeachers(Map<String, Object> paramsMap) {
        return listEntity("listReferralProcessingTeachers", paramsMap);
    }

    public Integer countReferralProcessingTeachers(Map<String, Object> paramsMap) {
        return selectCount("countReferralProcessingTeachers", paramsMap);
    }

    public List<ReferralTeacherDto> listReferralFailedTeachers(Map<String, Object> paramsMap) {
        return listEntity("listReferralFailedTeachers", paramsMap);
    }

    public Integer countReferralFailedTeachers(Map<String, Object> paramsMap) {
        return selectCount("countReferralFailedTeachers", paramsMap);
    }

    public ReferralTeacherDto getReferralTeacher(Map<String, Object> paramsMap) {
        List<ReferralTeacherDto> list = listEntity("getReferralTeacher", paramsMap);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

	/**
	 * 根据城市ID或者州ID获取同城市正式老师数量
	 * @param city 城市ID
	 * @param stateId 州ID
	 * @return
	 */
	public int findRegulareTeacherByCity(Integer city,Integer stateId){
		Map<String, Object> paramsMap = Maps.newHashMap();
		if(city != null && city != 0){
			paramsMap.put("city", city);
		}else{
			if(stateId != null && stateId != 0){
				paramsMap.put("stateId", stateId);
			}else{
				logger.warn("传入的城市ID或者州ID为0,TeacherDao.findRegulareTeacherByCity(int city,int stateId),city:{},stateId:{}",city,stateId);
				return 0;
			}
		}
		return this.selectEntity("findRegulareTeacherByCity", paramsMap);
	}
}
