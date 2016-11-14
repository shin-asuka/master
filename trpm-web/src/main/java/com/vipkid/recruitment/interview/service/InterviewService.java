package com.vipkid.recruitment.interview.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Lists;

@Service
public class InterviewService {
    
    private static Logger logger = LoggerFactory.getLogger(InterviewService.class);
    
    public List<Map<String,Object>> list(){
        logger.info("interview - list");
        return Lists.newArrayList();
    }
}
