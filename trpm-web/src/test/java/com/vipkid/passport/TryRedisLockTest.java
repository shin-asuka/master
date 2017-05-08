package com.vipkid.passport;

import com.sun.media.jfxmediaimpl.MediaDisposer;
import com.vipkid.rest.dto.RegisterDto;
import com.vipkid.trpm.proxy.RedisProxy;
import com.vipkid.trpm.service.passport.PassportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

/**
 * Created by luojiaoxia on 17/5/3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/applicationContext.xml"})
public class TryRedisLockTest {

    @Resource
    private RedisProxy redisProxy;

    @Resource
    private PassportService passportService;

    private static final String key = "demo_luojiaoxia";

//    @Test
//    public void testLock(){
//        for(int i=0; i<5; i++){
//            TestThread th = new TestThread(i);
//            Thread thread = new Thread(th);
//            thread.start();
//        }
//    }

    @Test
    public void testSignUp(){
        CountDownLatch threadSignal = new CountDownLatch(2);
        TestThread th = new TestThread(1, threadSignal);
        Thread thread = new Thread(th);
        thread.start();


        TestThread th2 = new TestThread(2, threadSignal);
        Thread thread2 = new Thread(th2);
        thread2.start();

        try {
            threadSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + "结束.");
    }

    class TestThread implements Runnable{

        private int index;


        private CountDownLatch threadsSignal;
        @Override
        public void run() {
//            boolean result = redisProxy.tryLock(key, 3, 5);
//
//            System.out.println("**************result============"+result);

            RegisterDto dto = new RegisterDto();
            dto.setEmail("luojiaoxicdvdd"+index+"@aaa.com");
            dto.setPassword("aaaaa33");
            dto.setImageCode("aaaa");

            passportService.saveSignUp(dto);

            threadsSignal.countDown();

            System.out.println(Thread.currentThread().getName() + "结束. 还有" + threadsSignal.getCount() + " 个线程");
        }

        public TestThread(int index, CountDownLatch threadsSignal) {
            this.index = index;
            this.threadsSignal = threadsSignal;

        }
    }
}
