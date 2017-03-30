package com.aurea.caonb.egizatullin.test.utils.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;


public class TestAppender extends AppenderBase<ILoggingEvent> {

    private static final ThreadLocal<AbstractLoggingEventListener> LISTENERS = new ThreadLocal<>();

    static void enable(AbstractLoggingEventListener listener) {
        LISTENERS.set(listener);
    }

    static void disable() {
        LISTENERS.remove();
    }

    @Override
    protected void append(ILoggingEvent e) {
        AbstractLoggingEventListener listener = LISTENERS.get();
        if (listener != null) {
            listener.accept(e);
        }
    }
}