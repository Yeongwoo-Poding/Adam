package project.adam.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
                        e.getError().getErrorType(),
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
                        VALIDATION_EXCEPTION.getErrorType(),
                        VALIDATION_EXCEPTION.getMessage()
                ),
                VALIDATION_EXCEPTION.getStatus()
        );
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final HttpMessageNotReadableException e) {
        return new ResponseEntity<>(
                new ApiExceptionEntity(
                        PARSE_EXCEPTION.getErrorType(),
                        PARSE_EXCEPTION.getMessage()
                ),
                PARSE_EXCEPTION.getStatus()
        );
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final MissingServletRequestParameterException e) {
        return new ResponseEntity<>(
                new ApiExceptionEntity(
                        PARAMETER_EXCEPTION.getErrorType(),
                        PARAMETER_EXCEPTION.getMessage()
                ),
                PARAMETER_EXCEPTION.getStatus()
        );
    }

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final SQLIntegrityConstraintViolationException e) {
        return new ResponseEntity<>(
                new ApiExceptionEntity(
                        INTEGRITY_EXCEPTION.getErrorType(),
                        INTEGRITY_EXCEPTION.getMessage()
                ),
                INTEGRITY_EXCEPTION.getStatus()
        );
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest request,
                                                               final IllegalArgumentException e) {
        return new ResponseEntity<>(
                new ApiExceptionEntity(
                        NO_BOARD_EXCEPTION.getErrorType(),
                        NO_BOARD_EXCEPTION.getMessage()
                ),
                NO_BOARD_EXCEPTION.getStatus()
        );
    }
}
