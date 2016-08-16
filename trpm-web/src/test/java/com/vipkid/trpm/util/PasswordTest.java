package com.vipkid.trpm.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordTest {
    
    private static Logger logger = LoggerFactory.getLogger(PasswordTest.class);

    @Test
    public void checkPassword() {
        String matching = "^([a-zA-Z0-9])+$";
        String matching1 = "^(?:.*[A-Z].*)(?:.*[0-9].*)|(?:.*[a-z].*)(?:.*[0-9].*)|(?:.*[A-Z].*)(?:.*[a-z].*).{0,}$";

       logger.info("----");
  
       logger.info("----");

       logger.info("----");

       logger.info("----");

       logger.info("----");
   
       //logger.info("11".matches(matching) && "11".matches(matching1));
    }
    
    
    @Test
    public void changeUrl(){
        String regex = "^(?:.*chedule.shtml)|(?:.*changePassword.shtml)|(?:.*changePasswordAction.json){0,}$";
    }
}
