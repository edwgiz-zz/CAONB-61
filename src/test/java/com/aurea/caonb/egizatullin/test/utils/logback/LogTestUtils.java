package com.aurea.caonb.egizatullin.test.utils.logback;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import org.apache.commons.lang3.StringUtils;
import org.mockito.InOrder;


public final class LogTestUtils {

    public static void verifyLoggingEvent(AbstractLoggingEventListener log, Level level,
        Class<? extends Throwable> exceptionType, String msg) {

        verify(log, times(1)).accept(matcher(level, exceptionType, msg));
    }

    public static void verifyLoggingEvent(InOrder o, AbstractLoggingEventListener log, Level level,
        Class<? extends Throwable> exceptionType, String msg) {

        o.verify(log, times(1)).accept(matcher(level, exceptionType, msg));
    }

    private static ILoggingEvent matcher(Level level, Class<? extends Throwable> exceptionType,
        String msg) {
        return argThat(
            le -> le.getLevel() == level
                && equals(exceptionType, le.getThrowableProxy())
                && StringUtils.equals(le.getMessage(), msg)
        );
    }

    private static boolean equals(Class<? extends Throwable> expected, IThrowableProxy actual) {
        return
            (expected == null && actual == null) ||

            (expected != null && actual != null &&
                StringUtils.equals(
                    expected.getName(), actual.getClassName()));
    }


    private LogTestUtils() {
    }
}