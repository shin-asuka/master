/**
 * Created by liyang on 2017/2/15.
 */
package com.vipkid.common.log;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.net.SMTPAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.helpers.CyclicBuffer;
import org.apache.commons.lang3.StringUtils;
import org.community.config.PropertyConfigurer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class SelfSmtpAppender extends SMTPAppender {

    private static String HOST_NAME;
    private static String HOST_ADDRESS;

    static {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String applicationName = PropertyConfigurer.stringValue("application.name");
            HOST_NAME = StringUtils.isNotBlank(applicationName)?applicationName:localHost.getHostName();
            HOST_ADDRESS = localHost.getHostAddress();
        } catch (UnknownHostException ignored) {
            HOST_NAME = "unknown-host";
            HOST_ADDRESS = "unknown-host";
        }
    }

    private String buildTitle(){
        return new StringBuilder().append(HOST_NAME)
                .append(" [ ").append(HOST_ADDRESS).append(" ]")
                .toString();
    }

    @Override
    protected Layout<ILoggingEvent> makeSubjectLayout(String subjectStr) {
        PatternLayout pl = new PatternLayout();
        pl.setContext(getContext());
        pl.setPattern(buildTitle());
        pl.setPostCompileProcessor(null);
        pl.start();
        return pl;
    }


    final static int MAX_DELAY_BETWEEN_STATUS_MESSAGES = (int) (1228800 * CoreConstants.MILLIS_IN_ONE_SECOND);
    int delayBetweenStatusMessages = (int) (300 * CoreConstants.MILLIS_IN_ONE_SECOND);
    long lastTrackerStatusPrint = 0;
    private int errorCount = 0;

    private Object lock = new Object();

    /**
     * 这个方法和父类完全一样, 复写的原因是因为重写了SenderRunnable类
     * @param eventObject
     */
    protected void append(ILoggingEvent eventObject) {

        if (!checkEntryConditions()) {
            return;
        }

        String key = discriminator.getDiscriminatingValue(eventObject);
        long now = System.currentTimeMillis();
        final CyclicBuffer<ILoggingEvent> cb = cbTracker.getOrCreate(key, now);
        subAppend(cb, eventObject);

        try {
            if (eventEvaluator.evaluate(eventObject)) {
                // clone the CyclicBuffer before sending out asynchronously
                CyclicBuffer<ILoggingEvent> cbClone = new CyclicBuffer<ILoggingEvent>(cb);
                // see http://jira.qos.ch/browse/LBCLASSIC-221
                cb.clear();

                if (isAsynchronousSending()) {
                    // perform actual sending asynchronously
                    SenderRunnable senderRunnable = new SenderRunnable(cbClone, eventObject);
                    context.getExecutorService().execute(senderRunnable);
                } else {
                    // synchronous sending
                    sendBuffer(cbClone, eventObject);
                }
            }
        } catch (EvaluationException ex) {
            errorCount++;
            if (errorCount < CoreConstants.MAX_ERROR_COUNT) {
                addError("SMTPAppender's EventEvaluator threw an Exception-", ex);
            }
        }

        // immediately remove the buffer if asked by the user
        if (eventMarksEndOfLife(eventObject)) {
            cbTracker.endOfLife(key);
        }

        cbTracker.removeStaleComponents(now);

        if (lastTrackerStatusPrint + delayBetweenStatusMessages < now) {
            addInfo("SMTPAppender [" + name + "] is tracking [" + cbTracker.getComponentCount() + "] buffers");
            lastTrackerStatusPrint = now;
            // quadruple 'delay' assuming less than max delay
            if (delayBetweenStatusMessages < MAX_DELAY_BETWEEN_STATUS_MESSAGES) {
                delayBetweenStatusMessages *= 4;
            }
        }
    }

    /**
     * 因为邮箱服务端有限制, 所以连续的logger.error最终只有一个会发出邮件
     * 猜测可能是服务器一段时间内(经反复实验, 应该小于100ms) 收到的第一封/最后一封邮件
     * 又因为是异步发送, 所以就相当于随机了.
     *
     * 所以重新实现了父类当中的SenderRunnable, run方法中获取了锁(此类单例), 并在每发一封邮件之后, 休眠2000ms
     */
    class SenderRunnable implements Runnable {

        final CyclicBuffer<ILoggingEvent> cyclicBuffer;
        final ILoggingEvent e;

        SenderRunnable(CyclicBuffer<ILoggingEvent> cyclicBuffer, ILoggingEvent e) {
            this.cyclicBuffer = cyclicBuffer;
            this.e = e;
        }

        public void run() {
            synchronized (lock) {
                sendBuffer(cyclicBuffer, e);
                try {
                    //
                    TimeUnit.MILLISECONDS.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }
    }


}
