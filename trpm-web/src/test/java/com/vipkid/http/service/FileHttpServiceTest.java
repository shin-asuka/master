package com.vipkid.http.service;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author Austin.Cao  Date: 19/12/2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/applicationContext.xml" })
@ActiveProfiles("a11")
public class FileHttpServiceTest {

    private static Logger logger = LoggerFactory.getLogger(FileHttpServiceTest.class);

    @Resource
    private FileHttpService fileHttpService;

    @Test
    public void testQueryTeacherFiles() {
        Long teacherId = 2070189L;

        fileHttpService.queryTeacherFiles(teacherId);
    }

}
