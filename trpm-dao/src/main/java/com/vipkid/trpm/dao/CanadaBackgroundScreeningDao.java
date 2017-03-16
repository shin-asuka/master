package com.vipkid.trpm.dao;

import com.vipkid.trpm.entity.CanadaBackgroundScreening;

/**
 * Created by luning on 2017/3/15.
 */
public interface CanadaBackgroundScreeningDao {
    CanadaBackgroundScreening findByTeacherId(long teacherId);
}
