package project.adam.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
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
import java.util.UUID;

import static project.adam.exception.ExceptionEnum.*;

@Slf4j
@RestControllerAdvice
public class ApiExceptionAdvice {
    @ExceptionHandler({ApiException.class})
    public ResponseEntity<ApiExceptionEntity> apiExceptionHandler(HttpServletRequest request, final ApiException e) {
        String id = createId();
        log.warn("[{}] API_EXCEPTION at {}", id, request.getRequestURL(), e);
        return new ResponseEntity<>(new ApiExceptionEntity(e.getError(), id), e.getError().getStatus());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ApiExceptionEntity> jsonExceptionHandler(HttpServletRequest request, final Exception e) {
        String id = createId();

        // Type Error
        if (e.getCause() != null && e.getCause() instanceof InvalidFormatException) {
            log.warn("[{}] INVALID_TYPE at {}", id, request.getRequestURL(), e);
            return new ResponseEntity<>(new ApiExceptionEntity(INVALID_TYPE, id), INVALID_TYPE.getStatus());
        }
        log.warn("[{}] INVALID_JSON_FORMAT at {}", id, request.getRequestURL(), e);
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_JSON_FORMAT, id), INVALID_JSON_FORMAT.getStatus());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestPartException.class})
    public ResponseEntity<ApiExceptionEntity> inputExceptionHandler(HttpServletRequest request, final Exception e) {
        String id = createId();
        log.warn("[{}] INVALID_INPUT at {}", id, request.getRequestURL(), e);
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_INPUT, id), INVALID_INPUT.getStatus());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ApiExceptionEntity> parameterExceptionHandler(HttpServletRequest request, final Exception e) {
        String id = createId();
        log.warn("[{}] INVALID_PARAMETER at {}", id, request.getRequestURL(), e);
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_PARAMETER, id), INVALID_PARAMETER.getStatus());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiExceptionEntity> typeExceptionHandler(HttpServletRequest request, final Exception e) {
        String id = createId();
        log.warn("[{}] INVALID_TYPE at {}", id, request.getRequestURL(), e);
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_TYPE, id), INVALID_TYPE.getStatus());
    }

    @ExceptionHandler({MissingRequestHeaderException.class, MultipartException.class, HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ApiExceptionEntity> headerExceptionHandler(HttpServletRequest request, final Exception e) {
        String id = createId();
        log.warn("[{}] INVALID_HEADER at {}", id, request.getRequestURL(), e);
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_HEADER, id), INVALID_HEADER.getStatus());
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ApiExceptionEntity> methodExceptionHandler(HttpServletRequest request, final Exception e) {
        String id = createId();
        log.warn("[{}] INVALID_METHOD at {}", id, request.getRequestURL(), e);
        return new ResponseEntity<>(new ApiExceptionEntity(INVALID_METHOD, id), INVALID_METHOD.getStatus());
    }

    @ExceptionHandler({MissingRequestCookieException.class})
    public ResponseEntity<ApiExceptionEntity> authenticationExceptionHandler(HttpServletRequest request, final Exception e) {
        String id = createId();
        log.warn("[{}] AUTHENTICATION_FAILED at {}", id, request.getRequestURL(), e);
        return new ResponseEntity<>(new ApiExceptionEntity(AUTHENTICATION_FAILED, id), AUTHENTICATION_FAILED.getStatus());
    }

    @ExceptionHandler({NoSuchElementException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiExceptionEntity> dataExceptionHandler(HttpServletRequest request, final Exception e) {
        String id = createId();
        log.warn("[{}] NO_DATA at {}", id, request.getRequestURL(), e);
        return new ResponseEntity<>(new ApiExceptionEntity(NO_DATA, id), NO_DATA.getStatus());
    }

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<ApiExceptionEntity> conflictExceptionHandler(HttpServletRequest request, final Exception e) {
        String id = createId();
        log.warn("[{}] DUPLICATED at {}", id, request.getRequestURL(), e);
        return new ResponseEntity<>(new ApiExceptionEntity(DUPLICATED, id), DUPLICATED.getStatus());
    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
