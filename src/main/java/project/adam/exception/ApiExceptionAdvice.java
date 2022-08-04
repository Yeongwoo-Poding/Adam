package project.adam.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;

import static project.adam.exception.ExceptionEnum.*;

@RestControllerAdvice
public class ApiExceptionAdvice {

    @ExceptionHandler({ApiException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final ApiException e) {
        return new ResponseEntity<>(
                new ApiExceptionEntity(
                        e.getError().getMessage()
                ),
                e.getError().getStatus()
        );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final MethodArgumentNotValidException e) {
        return new ResponseEntity<>(
                new ApiExceptionEntity(
                        INVALID_DATA.getMessage()
                ),
                INVALID_DATA.getStatus()
        );
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final HttpMessageNotReadableException e) {
        return new ResponseEntity<>(
                new ApiExceptionEntity(
                        INVALID_DATA.getMessage()
                ),
                INVALID_DATA.getStatus()
        );
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final MissingServletRequestParameterException e) {
        return new ResponseEntity<>(
                new ApiExceptionEntity(
                        INVALID_DATA.getMessage()
                ),
                INVALID_DATA.getStatus()
        );
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final IllegalArgumentException e) {
        return new ResponseEntity<>(
                new ApiExceptionEntity(
                        INVALID_DATA.getMessage()
                ),
                INVALID_DATA.getStatus()
        );
    }

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final SQLIntegrityConstraintViolationException e) {
        return new ResponseEntity<>(
                new ApiExceptionEntity(
                        UNIQUE_CONSTRAINT_VIOLATED.getMessage()
                ),
                UNIQUE_CONSTRAINT_VIOLATED.getStatus()
        );
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final HttpRequestMethodNotSupportedException e) {
        return new ResponseEntity<>(
                new ApiExceptionEntity(
                        UNIQUE_CONSTRAINT_VIOLATED.getMessage()
                ),
                UNIQUE_CONSTRAINT_VIOLATED.getStatus()
        );
    }
}
