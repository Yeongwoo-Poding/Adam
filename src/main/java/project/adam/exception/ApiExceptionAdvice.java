package project.adam.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;
import static project.adam.exception.ExceptionEnum.*;

@Slf4j
@RestControllerAdvice
public class ApiExceptionAdvice {
    @ExceptionHandler({ApiException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final ApiException e) {
        log.warn("ApiException");
        return new ResponseEntity<>(new ApiExceptionEntity(e.getError()), e.getError().getStatus());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException");
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_JSON_FORMAT), INVALID_JSON_FORMAT.getStatus());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                         final MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException");
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_INPUT), INVALID_INPUT.getStatus());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                         final MissingServletRequestParameterException e) {
        log.warn("MissingServletRequestParameterException");
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_PARAMETER), INVALID_PARAMETER.getStatus());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                         final MethodArgumentTypeMismatchException e) {
        log.warn("HttpMessageNotReadableException");
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_TYPE), INVALID_TYPE.getStatus());
    }

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                         final SQLIntegrityConstraintViolationException e) {
        log.warn("SQLIntegrityConstraintViolationException");
        return new ResponseEntity<>(new ApiExceptionEntity(UNIQUE_CONSTRAINT_VIOLATED), UNIQUE_CONSTRAINT_VIOLATED.getStatus());
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                         final NoSuchElementException e) {
        log.warn("NoSuchElementException");
        return new ResponseEntity<>(new ApiExceptionEntity(NO_DATA), NO_DATA.getStatus());
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                         final HttpRequestMethodNotSupportedException e) {
        log.warn("HttpMessageNotReadableException");
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_METHOD), INVALID_METHOD.getStatus());
    }

    @ExceptionHandler({MissingRequestCookieException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final MissingRequestCookieException e) {
        log.warn("MissingRequestCookieException");
        return new ResponseEntity<>(new ApiExceptionEntity(AUTHENTICATION_FAILED), AUTHENTICATION_FAILED.getStatus());
    }
}
