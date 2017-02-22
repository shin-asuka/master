package com.vipkid.vconfig;

import com.vipkid.rest.service.AdminQuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Created by liyang on 2017/2/21.
 */
public class VConfigPropertyPlaceholderConfigurer  extends PropertyPlaceholderConfigurer implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(VConfigPropertyPlaceholderConfigurer.class);
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("afterPropertiesSet begin");

        logger.info("afterPropertiesSet end");
    }
}
