package com.aurea.caonb.egizatullin.test.utils.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.function.Consumer;

/**
 * Class must be used in try-with-resource block and must be instantiated through
 * {@link org.mockito.Mockito#spy(Class)} method.
 * Standard use case:
 * <pre>
 * try (AbstractLoggingEventListener log = spy(AbstractLoggingEventListener.class)) {
 *     ...
 *     Mockito.verifyNoMoreInteractions(log);
 * }
 *
 * </pre>
 */
public abstract class AbstractLoggingEventListener implements Consumer<ILoggingEvent>, AutoCloseable {

    AbstractLoggingEventListener() {
        TestAppender.enable(this);
    }

    @Override
    public final void close() throws Exception {
        TestAppender.disable();
    }
}
