package project.adam.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {
    VALIDATION_EXCEPTION(BAD_REQUEST, "VALIDATION_EXCEPTION", "잘못된 값이 입력되었습니다."),
    PARSE_EXCEPTION(BAD_REQUEST, "PARSE_ERROR", "잘못된 json 형식이 입력되었습니다."),
    PARAMETER_EXCEPTION(BAD_REQUEST, "PARAMETER_EXCEPTION", "잘못된 파라미터 값이 입력되었습니다."),

    INTEGRITY_EXCEPTION(FORBIDDEN, "INTEGRITY_EXCEPTION", "중복된 KEY가 존재합니다."),

    NO_RESULT_EXCEPTION(CONFLICT, "NO_RESULT_EXCEPTION", "검색 대상이 없습니다.");

    private final HttpStatus status;
    private final String errorType;
    private final String message;
}
