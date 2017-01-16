package com.vipkid.trpm.dao;

import com.vipkid.trpm.entity.Tags;
import org.community.dao.support.MapperDaoTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liuguowen on 2017/1/13.
 */
@Repository
public class TagsDao extends MapperDaoTemplate<Tags> {

    @Autowired
    public TagsDao(SqlSessionTemplate sqlSessionTemplate) {
        super(sqlSessionTemplate, Tags.class);
    }

    public List<Tags> getTags() {
        return super.selectList(new Tags());
    }

}
