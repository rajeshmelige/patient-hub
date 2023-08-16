package com.rajesh.patient.hub.handler;

import com.rajesh.patient.hub.exceptions.PatientAlreadyExistsException;
import com.rajesh.patient.hub.exceptions.PatientDetailsNotAvailableException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);
    private static final String ERROR_RESPONSE_MESSAGE = "Building response for error : {}";

    public static ResponseEntity<Object> getResponseEntityForError(HttpStatus status, String errorMessage) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now());
        response.put("message", errorMessage);
        return ResponseEntity.status(status.value()).body(response);
    }

    @ExceptionHandler(PatientAlreadyExistsException.class)
    public ResponseEntity<Object> userExistsError(PatientAlreadyExistsException ex) {
        LOGGER.error(ERROR_RESPONSE_MESSAGE, ex);
        return getResponseEntityForError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(PatientDetailsNotAvailableException.class)
    public ResponseEntity<Object> userDoesNotExistsError(PatientDetailsNotAvailableException ex) {
        LOGGER.error(ERROR_RESPONSE_MESSAGE, ex);
        return getResponseEntityForError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        LOGGER.error(ERROR_RESPONSE_MESSAGE, ex);
        return getResponseEntityForError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity methodArgumentNotValidException(ConstraintViolationException ex) {
        LOGGER.error(ERROR_RESPONSE_MESSAGE, ex);
        return getResponseEntityForError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}

