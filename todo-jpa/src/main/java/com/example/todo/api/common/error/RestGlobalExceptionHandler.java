package com.example.todo.api.common.error;

import jakarta.inject.Inject;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;
import org.terasoluna.gfw.common.exception.ResultMessagesNotificationException;
import org.terasoluna.gfw.common.message.ResultMessage;

@ControllerAdvice
public class RestGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Inject
    MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request
    ) {
        var responseBody = body;
        if (body == null) {
            responseBody = createApiError(request, "E999", ex.getMessage());
        }

        return ResponseEntity.status(statusCode).headers(headers).body(responseBody);
    }

    private ApiError createApiError(WebRequest request, String errorCode, Object... args) {
        return new ApiError(errorCode, messageSource.getMessage(errorCode, args, request.getLocale()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        ApiError apiError = createApiError(request, "E400");
        ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> createApiError(request, fieldError, fieldError.getField()))
                .forEach(apiError::addDetail);
        ex
                .getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(objectError -> createApiError(request, objectError, objectError.getObjectName()))
                .forEach(apiError::addDetail);

        return handleExceptionInternal(ex, apiError, headers, status, request);
    }

    private ApiError createApiError(
            WebRequest request, DefaultMessageSourceResolvable messageSourceResolvable, String target
    ) {
        return new ApiError(
                messageSourceResolvable.getCode(),
                messageSource.getMessage(messageSourceResolvable, request.getLocale()),
                target
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(
            BusinessException ex, WebRequest request
    ) {
        return handleResultMessagesNotificationException(ex, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    private ResponseEntity<Object> handleResultMessagesNotificationException(
            ResultMessagesNotificationException ex, HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        ResultMessage message = ex.getResultMessages().iterator().next();
        ApiError apiError = createApiError(request, message.getCode(), message.getArgs());
        return handleExceptionInternal(ex, apiError, headers, status, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request
    ) {
        return handleResultMessagesNotificationException(ex, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleSystemError(
            Exception ex, WebRequest request
    ) {
        ApiError apiError = createApiError(request, "E500");
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
