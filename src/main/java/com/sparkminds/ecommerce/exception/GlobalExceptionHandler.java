package com.sparkminds.ecommerce.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.sparkminds.ecommerce.dto.response.ErrorResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
        // err related to database when not finding entity
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex,
                        HttpServletRequest httpRequest) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .timestamp(LocalDateTime.now())
                                .path(httpRequest.getRequestURI())
                                .error("NOT_FOUND")
                                .message(ex.getMessage())
                                .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // err duplicated data
        @ExceptionHandler(ConflictResourceException.class)
        public ResponseEntity<ErrorResponse> handleConflictResourceException(ConflictResourceException ex,
                        HttpServletRequest httpRequest) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.CONFLICT.value())
                                .timestamp(LocalDateTime.now())
                                .path(httpRequest.getRequestURI())
                                .error("CONFLICT")
                                .message(ex.getMessage())
                                .build();
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        // err bad request from client
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex,
                        HttpServletRequest httpRequest) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .timestamp(LocalDateTime.now())
                                .path(httpRequest.getRequestURI())
                                .error("BAD_REQUEST")
                                .message(ex.getMessage())
                                .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // err business logic
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex,
                        HttpServletRequest httpRequest) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(ex.getStatus().value())
                                .timestamp(LocalDateTime.now())
                                .path(httpRequest.getRequestURI())
                                .error(ex.getStatus().name())
                                .message(ex.getMessage())
                                .build();
                return ResponseEntity.status(ex.getStatus().value()).body(errorResponse);
        }

        // err validate from client
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                        HttpServletRequest httpRequest) {
                // ex.getBindingResult() -> get the result of the validation
                // .getFieldErrors() -> get the list of errors
                // .stream() -> stream the list of errors
                // .map(err -> err.getField() + ": " + err.getDefaultMessage()) -> map each
                // error to a string
                // .collect(Collectors.joining(", ")) -> join the strings with a comma and a
                // space
                String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                                .map(err -> err.getDefaultMessage())
                                .collect(Collectors.joining(", "));

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .timestamp(LocalDateTime.now())
                                .path(httpRequest.getRequestURI())
                                .error("VALIDATION_FAILED")
                                .message(errorMessage)
                                .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // err malformed json request from client
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
                        HttpServletRequest httpRequest) {
                String message = "Malformed JSON request or data type mismatch";

                // if error in float to int
                if (ex.getMessage() != null && ex.getMessage().contains("Floating-point value")) {
                        message = "Data type mismatch: Integer value expected, but floating-point found.";
                }
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .timestamp(LocalDateTime.now())
                                .path(httpRequest.getRequestURI())
                                .error("MALFORMED_JSON_REQUEST")
                                .message(message)
                                .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex,
                        HttpServletRequest httpRequest) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .timestamp(LocalDateTime.now())
                                .path(httpRequest.getRequestURI())
                                .error(HttpStatus.UNAUTHORIZED.name())
                                .message("Unauthorized access: Please log in to continue.")
                                .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex,
                        HttpServletRequest httpRequest) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.FORBIDDEN.value())
                                .timestamp(LocalDateTime.now())
                                .path(httpRequest.getRequestURI())
                                .error(HttpStatus.FORBIDDEN.name())
                                .message("Access denied: You do not have permission.")
                                .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        @ExceptionHandler({
                        ExpiredJwtException.class,
                        SignatureException.class,
                        MalformedJwtException.class
        })
        public ResponseEntity<ErrorResponse> handleJwtException(Exception ex, HttpServletRequest httpServletRequest) {
                String message = "Token is invalid or expired";

                if (ex instanceof ExpiredJwtException) {
                        message = "Token has expired, please log in again";
                } else if (ex instanceof SignatureException) {
                        message = "Invalid token signature";
                } else if (ex instanceof MalformedJwtException) {
                        message = "Invalid token format";
                }

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .timestamp(LocalDateTime.now())
                                .path(httpServletRequest.getRequestURI())
                                .error(HttpStatus.UNAUTHORIZED.name())
                                .message(message)
                                .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // bad request authen
        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex,
                        HttpServletRequest httpServletRequest) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .timestamp(LocalDateTime.now())
                                .path(httpServletRequest.getRequestURI())
                                .error(HttpStatus.UNAUTHORIZED.name())
                                .message("Invalid username or password")
                                .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
                        HttpServletRequest httpServletRequest) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.CONFLICT.value())
                                .timestamp(LocalDateTime.now())
                                .path(httpServletRequest.getRequestURI())
                                .error("DATABASE_ERROR")
                                .message("Data integrity violation: some fields might be duplicated.")
                                .build();
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex,
                        HttpServletRequest httpRequest) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .timestamp(LocalDateTime.now())
                                .path(httpRequest.getRequestURI())
                                .error(HttpStatus.NOT_FOUND.name())
                                .message("The requested path was not found.")
                                .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        @ExceptionHandler(OptimisticLockingFailureException.class)
        public ResponseEntity<ErrorResponse> handleOptimisticLockingFailureException(
                        OptimisticLockingFailureException ex, HttpServletRequest request) {

                ErrorResponse error = ErrorResponse.builder()
                                .status(HttpStatus.CONFLICT.value()) // HTTP 409
                                .timestamp(LocalDateTime.now())
                                .path(request.getRequestURI())
                                .error("OPTIMISTIC_LOCKING_FAILURE")
                                .message("Data has been modified by another user. Please refresh and try again.")
                                .build();
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        // err internal server error
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleException(Exception ex,
                        HttpServletRequest httpRequest) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .timestamp(LocalDateTime.now())
                                .path(httpRequest.getRequestURI())
                                .error(HttpStatus.INTERNAL_SERVER_ERROR.name())
                                .message(ex.getMessage())
                                .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
}
