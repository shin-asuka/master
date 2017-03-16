package com.vipkid.common.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

/**
 * Created by liyang on 2017/2/15.
 */
public class OnWarnEvalutor extends EventEvaluatorBase<ILoggingEvent> {
    /**
     * Return true if event passed as parameter has level WARN or higher, returns
     * false otherwise.
     */
    public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
        return event.getLevel().levelInt >= Level.ERROR_INT;
    }
}