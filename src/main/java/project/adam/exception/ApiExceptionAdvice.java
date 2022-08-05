package project.adam.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException");
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_INPUT.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                         final MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException");
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_INPUT.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                         final MissingServletRequestParameterException e) {
        log.warn("MissingServletRequestParameterException");
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_INPUT.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                         final MethodArgumentTypeMismatchException e) {
        log.warn("HttpMessageNotReadableException");
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_INPUT.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                         final SQLIntegrityConstraintViolationException e) {
        log.warn("SQLIntegrityConstraintViolationException");
        return new ResponseEntity<>(new ApiExceptionEntity(UNIQUE_CONSTRAINT_VIOLATED.getMessage()), CONFLICT);
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                         final NoSuchElementException e) {
        log.warn("NoSuchElementException");
        return new ResponseEntity<>(new ApiExceptionEntity(NO_DATA.getMessage()), NOT_FOUND);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                         final HttpRequestMethodNotSupportedException e) {
        log.warn("HttpMessageNotReadableException");
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_METHOD.getMessage()), METHOD_NOT_ALLOWED);
    }
}
