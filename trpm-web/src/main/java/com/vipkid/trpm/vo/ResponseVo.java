package com.vipkid.trpm.vo;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by LP-813 on 2016/8/13.
 */
public class ResponseVo {

    public static JSONObject getReponseVo(Integer code,String msg,Object data){
        JSONObject jo = new JSONObject();
        jo.put("code",code);
        jo.put("msg",msg);
        jo.put("data",data);
        return jo;
    }

}
