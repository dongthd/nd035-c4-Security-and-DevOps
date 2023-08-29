package com.example.demo.controllers;

import com.example.demo.security.HandlerExceptionController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class HandlerExceptionControllerTest {

    private HandlerExceptionController handlerExceptionController;

    @Before
    public void init() {
        handlerExceptionController = new HandlerExceptionController();
    }

    @Test
    public void testHandlerException() {
        Exception ex = new Exception("Test exception");
        final ResponseEntity<String> response = handlerExceptionController.handlerException(ex);
    }
}
