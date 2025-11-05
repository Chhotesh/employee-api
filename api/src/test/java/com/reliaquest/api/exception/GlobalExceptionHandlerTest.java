package com.reliaquest.api.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/employee/123");
    }

    @Test
    void handleEmployeeNotFoundException_ShouldReturnNotFound() {
        EmployeeNotFoundException exception = new EmployeeNotFoundException("Employee with id 123 not found");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleEmployeeNotFoundException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Employee Not Found", response.getBody().getError());
        assertEquals("Employee with id 123 not found", response.getBody().getMessage());
        assertEquals("/api/v1/employee/123", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleEmployeeValidationException_ShouldReturnBadRequest() {
        EmployeeValidationException exception = new EmployeeValidationException("Employee name is required");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleEmployeeValidationException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation Error", response.getBody().getError());
        assertEquals("Employee name is required", response.getBody().getMessage());
        assertEquals("/api/v1/employee/123", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleEmployeeServiceException_ShouldReturnInternalServerError() {
        EmployeeServiceException exception = new EmployeeServiceException("Database connection failed");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleEmployeeServiceException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Service Error", response.getBody().getError());
        assertEquals("An error occurred while processing your request", response.getBody().getMessage());
        assertEquals("/api/v1/employee/123", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleGenericException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertEquals("/api/v1/employee/123", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }
}
