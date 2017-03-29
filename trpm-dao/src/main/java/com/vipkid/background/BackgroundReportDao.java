package com.vipkid.background;


import com.vipkid.trpm.entity.BackgroundReport;
import java.util.List;

/**
 * Created by luning on 2017/3/11.
 */

public interface BackgroundReportDao {

    int batchInsert(List<BackgroundReport> backgroundReportList);

    List<BackgroundReport> findByBgSterlingScreeningId(Long id);

    int update(BackgroundReport backgroundReport);


}
