package com.natelxstudio.currencyaccounts.controllers;

import com.natelxstudio.currencyaccounts.accountsstore.exceptions.AccountNotFoundException;
import com.natelxstudio.currencyaccounts.accountsstore.exceptions.DuplicatedAccountIbanException;
import com.natelxstudio.currencyaccounts.accountsstore.exceptions.InsufficientFundsException;
import com.natelxstudio.currencyaccounts.model.ApiError;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
        MethodArgumentNotValidException ex
    ) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(" "));
        return getResponseEntityError(errorMessage, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({HandlerMethodValidationException.class})
    public ResponseEntity<ApiError> handleHeaderValidationErrors(
        HandlerMethodValidationException ex
    ) {
        String errorMessage = Arrays.stream(Objects.requireNonNull(ex.getDetailMessageArguments()))
            .map(Object::toString)
            .collect(Collectors.joining(" "));
        return getResponseEntityError(errorMessage, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ApiError> handleBadRequestNoBodyErrors(
        Exception ex
    ) {
        return getResponseEntityError("Missing request payload.", HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class,
        MissingRequestHeaderException.class,
        MissingServletRequestParameterException.class})
    public ResponseEntity<ApiError> handleBadRequestErrors(
        Exception ex
    ) {
        return getResponseEntityError(ex, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({
        InsufficientFundsException.class
    })
    public ResponseEntity<ApiError> handleForbiddenErrors(
        Exception ex
    ) {
        return getResponseEntityError(ex, HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler({
        AccountNotFoundException.class,
        HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<ApiError> handleNotFoundErrors(
        Exception ex
    ) {
        return getResponseEntityError(ex, HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler({
        DuplicatedAccountIbanException.class
    })
    public ResponseEntity<ApiError> handleConflictErrors(
        Exception ex
    ) {
        return getResponseEntityError(ex, HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleInternalServerErrors(
        Exception ex
    ) {
        log.error(ex.getMessage(), ex);
        return getResponseEntityError("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private static ResponseEntity<ApiError> getResponseEntityError(
        Exception ex,
        int code
    ) {
        return getResponseEntityError(ex.getMessage(), code);
    }

    private static ResponseEntity<ApiError> getResponseEntityError(
        String message,
        int code
    ) {
        ApiError apiError = new ApiError(Integer.toString(code), message);
        return new ResponseEntity<>(apiError, HttpStatusCode.valueOf(code));
    }
}
