package com.example.authenticationservice.exception;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @PostConstruct
        public void init() {
                logger.info("GlobalExceptionHandler initialized and ready to handle exceptions");
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthException(
                        AuthenticationException ex,
                        HttpServletRequest request) {

                logger.error("Authentication Exception - Path: {}, Message: {}",
                                request.getRequestURI(), ex.getMessage());
                logger.debug("Authentication Exception details:", ex);

                ErrorResponse response = ErrorResponse.builder()
                                .timestamp(Instant.now())
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("UNAUTHORIZED")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                logger.info("Returning UNAUTHORIZED response for path: {}", request.getRequestURI());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        @ExceptionHandler(InvalidRequestException.class)
        public ResponseEntity<ErrorResponse> handleInvalidRequest(
                        InvalidRequestException ex,
                        HttpServletRequest request) {

                logger.warn("Invalid Request Exception - Path: {}, Message: {}",
                                request.getRequestURI(), ex.getMessage());
                logger.debug("Invalid Request Exception details:", ex);

                ErrorResponse response = ErrorResponse.builder()
                                .timestamp(Instant.now())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("BAD_REQUEST")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                logger.info("Returning BAD_REQUEST response for path: {}", request.getRequestURI());
                return ResponseEntity.badRequest().body(response);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                        Exception ex,
                        HttpServletRequest request) {

                logger.error("Unhandled Exception - Path: {}, Type: {}, Message: {}",
                                request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());
                logger.error("Unhandled Exception full details:", ex);

                ErrorResponse response = ErrorResponse.builder()
                                .timestamp(Instant.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error("INTERNAL_SERVER_ERROR")
                                .message("Something went wrong")
                                .path(request.getRequestURI())
                                .build();

                logger.info("Returning INTERNAL_SERVER_ERROR response for path: {}", request.getRequestURI());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
}
