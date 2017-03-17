package com.vipkid.recruitment.dao;
import com.vipkid.recruitment.entity.TeacherContractFile;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangzhaojun on 2016/11/15.
 */

@Repository
public class TeacherContractFileDao extends MapperDaoTemplate<TeacherContractFile> {
    @Autowired
    public TeacherContractFileDao(SqlSessionTemplate sqlSessionTemplate)
    {super(sqlSessionTemplate, TeacherContractFile.class);}
    @Override
    public int save(TeacherContractFile teacherContractFile) {

        teacherContractFile.setCreateId(teacherContractFile.getTeacherId());
        teacherContractFile.setUpdateId(teacherContractFile.getTeacherId());
        teacherContractFile.setCreateTime(new Timestamp(System.currentTimeMillis()));

        return super.save(teacherContractFile);
    }
    public List<TeacherContractFile> findByTeacherIdAndTeacherApplicationId(long teacherId, long teacherApplicationId){
        TeacherContractFile teacherContractFile =new TeacherContractFile();
        teacherContractFile.setTeacherId(teacherId);
        teacherContractFile.setTeacherApplicationId(teacherApplicationId);
        return super.selectList(teacherContractFile,"findByTeacherIdAndTeacherApplicationId");
    }

    public List<TeacherContractFile> findByTeacherId(long teacherId){
        TeacherContractFile teacherContractFile =new TeacherContractFile();
        teacherContractFile.setTeacherId(teacherId);
        return super.selectList(teacherContractFile,"findByTeacherId");
    }

    public TeacherContractFile findAllowEditOne(Long teacherId, Integer fileType){
        TeacherContractFile teacherContractFile =new TeacherContractFile();
        teacherContractFile.setTeacherId(teacherId);
        teacherContractFile.setFileType(fileType);
        List<TeacherContractFile> list = super.selectList(teacherContractFile,"findAllowEditOne");
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

    public List<TeacherContractFile> findListByTypes(Long teacherId, String types){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("teacherId", teacherId);
        paramMap.put("types", types);

        List<TeacherContractFile> list = super.selectList("findListByTypes", paramMap);
        return list;
    }


    public int updateUrlAndScreeningId(TeacherContractFile teacherContractFile){
        teacherContractFile.setUpdateId(teacherContractFile.getTeacherId());
        teacherContractFile.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return super.update(teacherContractFile, "updateUrlAndScreeningId");
    }

    public int  delete(TeacherContractFile teacherContractFile){
        return super.delete(teacherContractFile);
    }

    public int  update(TeacherContractFile teacherContractFile){
        teacherContractFile.setUpdateId(teacherContractFile.getTeacherId());
        teacherContractFile.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return super.update(teacherContractFile);
    }

   public void  updateBatch(List<TeacherContractFile> teacherContractFile){
       super.updateBatch(teacherContractFile);
   }

   public TeacherContractFile findById(int id){
       TeacherContractFile teacherContractFile =new TeacherContractFile();
       teacherContractFile.setId(id);
       return super.selectOne(teacherContractFile,"findById");
   }
    public List<TeacherContractFile> findBackgroundFileByTeacherId(long teacherId){
        TeacherContractFile teacherContractFile =new TeacherContractFile();
        teacherContractFile.setTeacherId(teacherId);
        return super.selectList(teacherContractFile,"findBackgroundFileByTeacherId");
    }
}


