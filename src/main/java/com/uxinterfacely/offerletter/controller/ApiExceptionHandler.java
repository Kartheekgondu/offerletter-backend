package com.uxinterfacely.offerletter.controller;

import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", safe(ex.getMessage())));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> notFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", safe(ex.getMessage())));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> tooLarge(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Map.of("error", "PDF exceeds the maximum upload size"));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> stateError(IllegalStateException ex) {
        log.error("Offer-letter request failed: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", safe(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> serverError(Exception ex) {
        log.error("Unexpected offer-letter API failure", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error. Check backend logs for details."));
    }

    private String safe(String message) {
        return message == null || message.isBlank()
                ? "Request failed"
                : message.substring(0, Math.min(message.length(), 400));
    }
}
