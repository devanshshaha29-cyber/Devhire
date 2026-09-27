package com.devhire.exception;

import com.devhire.dto.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(
            IllegalArgumentException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request
        );
    }


    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        String message =
                exception
                        .getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error ->
                                error.getField()
                                        + ": "
                                        + error.getDefaultMessage()
                        )
                        .collect(
                                Collectors.joining(", ")
                        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request
        );
    }


    @ExceptionHandler(
            MaxUploadSizeExceededException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleLargeFile(
            MaxUploadSizeExceededException exception,
            HttpServletRequest request
    ) {

        return buildResponse(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "Resume file is too large. Maximum allowed size is 5 MB.",
                request
        );
    }


    @ExceptionHandler(
            AccessDeniedException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {

        return buildResponse(
                HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource.",
                request
        );
    }


    /*
     * We currently use IllegalStateException
     * when external services such as Gemini
     * or GitHub cannot complete a request.
     */
    @ExceptionHandler(
            IllegalStateException.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleExternalServiceFailure(
            IllegalStateException exception,
            HttpServletRequest request
    ) {

        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getMessage(),
                request
        );
    }


    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<ApiErrorResponse>
    handleUnexpectedError(
            Exception exception,
            HttpServletRequest request
    ) {

        /*
         * Log the real exception on the server.
         * Do NOT send stack traces to the browser.
         */
        exception.printStackTrace();

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong on the server.",
                request
        );
    }


    private ResponseEntity<ApiErrorResponse>
    buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {

        ApiErrorResponse response =
                new ApiErrorResponse(
                        LocalDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}