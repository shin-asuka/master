package com.vipkid.background.api.sterling.controller;

import com.google.common.collect.Maps;
import com.vipkid.background.api.sterling.dto.CandidateInputDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liyang on 2017/3/11.
 */
@RequestMapping("/api/background/sterling")
@Controller
public class SterlingApiController {



    @RequestMapping("/createCandidates.json")
    public Object createCandidates(CandidateInputDto candidateInputDto){


        return Maps.newHashMap();
    }



    @RequestMapping("/updateCandidates.json")
    public Object updateCandidates(CandidateInputDto candidateInputDto){


        return Maps.newHashMap();
    }


    @RequestMapping("/createScreening.json")
    public Object createScreening(Long teacherId){

        return Maps.newHashMap();
    }


    @RequestMapping("/createPreAdverse.json")
    public Object createPreAdverse(Long teacherId){

        return Maps.newHashMap();
    }



    @RequestMapping("/callback.json")
    public Object  callback(HttpServletRequest request){

        return Maps.newHashMap();
    }




}
