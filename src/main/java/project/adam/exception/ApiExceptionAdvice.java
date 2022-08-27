package project.adam.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.NoSuchElementException;

import static project.adam.exception.ExceptionEnum.*;

@Slf4j
@RestControllerAdvice
public class ApiExceptionAdvice {
    @ExceptionHandler({ApiException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request, final ApiException e) {
        log.warn("ApiException", e);
        return new ResponseEntity<>(new ApiExceptionEntity(e.getError()), e.getError().getStatus());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ApiExceptionEntity> jsonExceptionHandler(HttpServletRequest request, final Exception e) {
        log.warn("INVALID_JSON_FORMAT", e);
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_JSON_FORMAT), INVALID_JSON_FORMAT.getStatus());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestPartException.class})
    public ResponseEntity<ApiExceptionEntity> inputExceptionHandler(HttpServletRequest request, final Exception e) {
        log.warn("INVALID_INPUT", e);
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_INPUT), INVALID_INPUT.getStatus());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ApiExceptionEntity> parameterExceptionHandler(HttpServletRequest request, final Exception e) {
        log.warn("MissingServletRequestParameterException", e);
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_PARAMETER), INVALID_PARAMETER.getStatus());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiExceptionEntity> typeExceptionHandler(HttpServletRequest request, final Exception e) {
        log.warn("HttpMessageNotReadableException", e);
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_TYPE), INVALID_TYPE.getStatus());
    }

    @ExceptionHandler({MissingRequestHeaderException.class, MultipartException.class})
    public ResponseEntity<ApiExceptionEntity> headerExceptionHandler(HttpServletRequest request, final Exception e) {
        log.warn("MissingRequestHeaderException", e);
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_HEADER), INVALID_HEADER.getStatus());
    }

    @ExceptionHandler({MissingRequestCookieException.class})
    public ResponseEntity<ApiExceptionEntity> authenticationExceptionHandler(HttpServletRequest request, final Exception e) {
        log.warn("MissingRequestCookieException", e);
        return new ResponseEntity<>(new ApiExceptionEntity(AUTHENTICATION_FAILED), AUTHENTICATION_FAILED.getStatus());
    }

    @ExceptionHandler({NoSuchElementException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiExceptionEntity> dataExceptionHandler(HttpServletRequest request, final Exception e) {
        log.warn("NO_DATA", e);
        return new ResponseEntity<>(new ApiExceptionEntity(NO_DATA), NO_DATA.getStatus());
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ApiExceptionEntity> methodExceptionHandler(HttpServletRequest request, final Exception e) {
        log.warn("HttpMessageNotReadableException", e);
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_METHOD), INVALID_METHOD.getStatus());
    }

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<ApiExceptionEntity> conflictExceptionHandler(HttpServletRequest request, final Exception e) {
        log.warn("SQLIntegrityConstraintViolationException", e);
        return new ResponseEntity<>(new ApiExceptionEntity(UNIQUE_CONSTRAINT_VIOLATED), UNIQUE_CONSTRAINT_VIOLATED.getStatus());
    }
}
