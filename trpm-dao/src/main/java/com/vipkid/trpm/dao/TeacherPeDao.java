package com.vipkid.trpm.dao;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.vipkid.enums.TbdResultEnum;
import com.vipkid.trpm.entity.TeacherPe;

@Repository
public class TeacherPeDao extends MapperDaoTemplate<TeacherPe> {

    @Autowired
    public TeacherPeDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, TeacherPe.class);
    }

    public int countByTeacherId(long teacherId) {
        Preconditions.checkArgument(0 != teacherId);
        return selectCount(new TeacherPe().setPeId(teacherId));
    }

    public List<TeacherPe> pageByTeacherId(long teacherId, int curPage, int linePerPage) {
        TeacherPe teacherPe = new TeacherPe().setPeId(teacherId);
        teacherPe.setOrderString(" schedule_time DESC ");
        return selectPage(teacherPe, curPage, linePerPage);
    }

    public TeacherPe findById(int id) {
        Preconditions.checkArgument(0 != id);
        return selectOne(new TeacherPe().setId(id));
    }

    @Override
    public int save(TeacherPe entity) {
        return super.save(entity);
    }

    public int updateTeacherPeComments(TeacherPe teacherPe, String result, String comments) {
        teacherPe.setPeComment(comments);
        teacherPe.setOperatorTime(new Timestamp(System.currentTimeMillis()));
        teacherPe.setStatus(TbdResultEnum.getStatusEnum(result));
        return this.update(teacherPe);
    }

    public int updateTeacherPeStartTime(TeacherPe teacherPe) {
        return this.update(teacherPe, "updateStartTime");
    }

    public int updateTeacherPe(TeacherPe teacherPe) {
        return this.update(teacherPe);
    }

    public TeacherPe findByOnlineClassId(long onlineClassId) {
        Preconditions.checkArgument(0 != onlineClassId);
        return this.selectOne(new TeacherPe().setOnlineClassId(onlineClassId));
    }

    /**
     * 随机获取一个正式老师的ID
     *
     * @Author:ALong (ZengWeiLong)
     * @return long
     * @date 2016年6月29日
     */
    public List<Long> randomTeachersForPE() {
        List<Long> resultList = listEntity("randomTeachersForPE", Maps.newHashMap());
        if (CollectionUtils.isNotEmpty(resultList)) {
            Collections.shuffle(resultList);
        }
        return resultList;
    }

    public List<TeacherPe> findNotAllocations() {
        return selectList(new TeacherPe(), "findNotAllocations");
    }

    public List<TeacherPe> findNotRecyclings(long currentTimeMillis) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("currentTimeMillis", currentTimeMillis);
        return selectList("findNotRecyclings", params);
    }

    public List<TeacherPe> findNotReminds() {
        return selectList(new TeacherPe(), "findNotReminds");
    }

}
