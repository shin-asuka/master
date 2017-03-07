package com.vipkid.portal.classroom.service;

import com.vipkid.portal.classroom.model.mockclass.PeViewOutputDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MockClassService {

    private static Logger logger = LoggerFactory.getLogger(MockClassService.class);

    public PeViewOutputDto doPeView(Integer applicationId){
        return null;
    }

}
