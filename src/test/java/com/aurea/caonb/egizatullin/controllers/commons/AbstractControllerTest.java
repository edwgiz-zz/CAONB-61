package com.aurea.caonb.egizatullin.controllers.commons;

import static ch.qos.logback.classic.Level.ERROR;
import static com.aurea.caonb.egizatullin.controllers.commons.AbstractController.HTTP_INTERNAL_ERROR_MESSAGE;
import static com.aurea.caonb.egizatullin.test.utils.logback.LogTestUtils.verifyLoggingEvent;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.aurea.caonb.egizatullin.test.utils.logback.AbstractLoggingEventListener;
import org.junit.Test;
import org.springframework.http.ResponseEntity;


public class AbstractControllerTest {

    @Test
    public void testException() throws Exception {
        AbstractController c = new AbstractController() {
        };

        try (AbstractLoggingEventListener log = spy(AbstractLoggingEventListener.class)) {
            testException(c, log, "Test message", HTTP_INTERNAL_ERROR, "Test message");
            testException(c, log, null, HTTP_INTERNAL_ERROR, HTTP_INTERNAL_ERROR_MESSAGE);
        }
    }

    private void testException(AbstractController c, AbstractLoggingEventListener log,
        String errorMessage, int expectedHttpCode, String expectedResponseErrorMessage) {
        ResponseEntity<AbstractResponse> re = c
            .exception(new RuntimeException(errorMessage));
        assertEquals(expectedHttpCode, re.getStatusCodeValue());
        assertEquals(expectedResponseErrorMessage, re.getBody().errorMessage);
        verifyLoggingEvent(log, ERROR, RuntimeException.class, errorMessage);
        verifyNoMoreInteractions(log);
    }

}