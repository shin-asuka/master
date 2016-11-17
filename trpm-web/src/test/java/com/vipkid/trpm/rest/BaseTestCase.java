package com.vipkid.trpm.rest;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/*.xml" })
@ActiveProfiles("development")
public class BaseTestCase {

    public static final String URL_PREFIX = "http://127.0.0.1:7080/trpm-web";
    
    
    public static final String TOKEN = "dfba4cde-7dd4-4b7f-8125-12cb95c8eb9f";
    
}
