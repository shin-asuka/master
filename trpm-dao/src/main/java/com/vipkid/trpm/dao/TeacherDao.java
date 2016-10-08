package com.vipkid.trpm.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vipkid.trpm.entity.Teacher;

@Repository
public class TeacherDao extends MapperDaoTemplate<Teacher> {

    @Autowired
    public TeacherDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, Teacher.class);
    }

    public Teacher findById(long id) {
        if (id == 0)
            return null;
        Teacher teacher = selectOne(new Teacher().setId(id));
        if (teacher != null) {
            // 2016-05-25 为兼容新旧Natioality数据，做转换
            String strCountry = teacher.getCountry();
            String strNationality = NationalityTransfer.nationalityFromDB(strCountry);
            teacher.setCountry(strNationality);
        }
        return teacher;
    }

    @Override
    public int update(Teacher teacher) {

        // 2016-05-25 为兼容新旧Natioality数据，做转换
        String strCountry = teacher.getCountry();
        String strNationality = NationalityTransfer.nationalityToDB(strCountry);
        teacher.setCountry(strNationality);

        return super.update(teacher);
    }

    @Override
    public int save(Teacher teacher) {
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
     * @param serialNumber
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

    public Teacher findByEmail(String email) {
        try {
            return selectOne(new Teacher().setEmail(email));
        } catch (Exception e) {
            return new Teacher();
        }
    }

}
