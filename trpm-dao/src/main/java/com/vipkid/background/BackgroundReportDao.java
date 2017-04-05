package com.vipkid.background;


import com.vipkid.trpm.entity.BackgroundReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by luning on 2017/3/11.
 */

public interface BackgroundReportDao {

    int batchInsert(List<BackgroundReport> backgroundReportList);

    List<BackgroundReport> findByBgSterlingScreeningId(Long id);

    BackgroundReport getByReportIdBgSterlingScreeningId(@Param("reportId") String reportId, @Param("bgSterlingScreeningId") Long bgSterlingScreeningId);

    int update(BackgroundReport backgroundReport);


}
