package com.vipkid.background;

import com.vipkid.trpm.entity.BackgroundAdverse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by luning on 2017/3/11.
 *
 */
public interface BackgroundAdverseDao {

    int batchInsert(List<BackgroundAdverse> backgroundAdverseList) ;

    BackgroundAdverse  findByScreeningIdTopOne(long ScreeningId);

    List<BackgroundAdverse> findUpdateTimeByBgScreeningId(Long bgScreeningId);

    BackgroundAdverse getByActionsIdBgSterlingScreeningId(@Param("actionsId") String actionsId, @Param("bgSterlingScreeningId") Long bgSterlingScreeningId);

    int update(BackgroundAdverse backgroundAdverse);
}
