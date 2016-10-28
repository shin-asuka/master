package com.vipkid.rest.web;

import com.google.api.client.util.Maps;
import com.vipkid.http.vo.OnlineClassVo;
import com.vipkid.rest.config.RestfulConfig;
import com.vipkid.trpm.service.portal.OnlineClassService;
import org.community.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by LP-813 on 2016/10/26.
 */
@RestController
public class UnitAssessmentController {

    @Autowired
    private OnlineClassService onlineClassService;



    @RequestMapping(value = "/unfinishedUA", method = RequestMethod.GET,produces = RestfulConfig.JSON_UTF_8)
    @ResponseBody
    public Object getUnfinishedUA(@RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "1") Integer pageSize) {
        List<OnlineClassVo> onlineClassVos = onlineClassService.getUnfinishUA(pageNo, pageSize);
        Map<String, Object> result = Maps.newHashMap();
        result.put("status", HttpStatus.OK.value());
        result.put("info",onlineClassVos);
        return JsonTools.getJson(result);
    }
}
