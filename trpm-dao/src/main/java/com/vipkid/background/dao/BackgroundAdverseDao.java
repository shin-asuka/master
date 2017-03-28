package com.vipkid.background.dao;

import com.vipkid.trpm.entity.BackgroundAdverse;

import java.util.Date;
import java.util.List;

/**
 * Created by luning on 2017/3/11.
 *
 */
public interface BackgroundAdverseDao {

    int batchInsert(List<BackgroundAdverse> backgroundAdverseList) ;

    BackgroundAdverse  findByScreeningIdTopOne(long ScreeningId);

    List<BackgroundAdverse> findUpdateTimeByBgScreeningId(Long bgScreeningId);

    int update(BackgroundAdverse backgroundAdverse);
}
