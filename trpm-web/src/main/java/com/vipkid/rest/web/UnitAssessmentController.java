package com.vipkid.rest.web;

        import com.alibaba.fastjson.JSONObject;
        import com.google.api.client.util.Maps;
        import com.vipkid.http.vo.OnlineClassVo;
        import com.vipkid.trpm.service.portal.OnlineClassService;
        import com.vipkid.trpm.vo.ResponseVo;
        import org.community.tools.JsonTools;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.HttpStatus;
        import org.springframework.security.access.prepost.PreAuthorize;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RequestParam;
        import org.springframework.web.bind.annotation.ResponseBody;
        import org.springframework.web.bind.annotation.RestController;

        import java.util.ArrayList;
        import java.util.Map;

/**
 * Created by LP-813 on 2016/10/26.
 */
@RestController
public class UnitAssessmentController {

    @Autowired
    private OnlineClassService onlineClassService;



    @RequestMapping("/unfinished")
    public Map<String, Object> getUnfinishedUA(@RequestParam(defaultValue = "1") Integer pageNo,@RequestParam(defaultValue = "1") Integer pageSize){
        ArrayList<OnlineClassVo> onlineClassVos = onlineClassService.getUnfinishUA(pageNo, pageSize);
        Map<String,Object> result = Maps.newHashMap();
        result.put("status", HttpStatus.OK.value());
        result.put("info",onlineClassVos);
        return result;
    }
}
