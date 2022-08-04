package project.adam.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),
    INVALID_DATA(BAD_REQUEST, "잘못된 데이터가 입력되었습니다."),
    NO_DATA(NOT_FOUND, "존재하지 않는 데이터입니다."),
    UNIQUE_CONSTRAINT_VIOLATED(CONFLICT, "무결성 제약조건에 위배됩니다.");

//    VALIDATION_EXCEPTION(BAD_REQUEST, "VALIDATION_EXCEPTION", "잘못된 값이 입력되었습니다."),
//    PARSE_EXCEPTION(BAD_REQUEST, "PARSE_ERROR", "잘못된 json 형식이 입력되었습니다."),
//    PARAMETER_EXCEPTION(BAD_REQUEST, "PARAMETER_EXCEPTION", "잘못된 파라미터 값이 입력되었습니다."),
//
//    INTEGRITY_EXCEPTION(FORBIDDEN, "INTEGRITY_EXCEPTION", "중복된 KEY가 존재합니다."),
//
//    NO_RESULT_EXCEPTION(CONFLICT, "NO_RESULT_EXCEPTION", "검색 대상이 없습니다."),
//    NO_BOARD_EXCEPTION(CONFLICT, "NO_BOARD_EXCEPTION", "존재하지 않는 게시판입니다.");

    private final HttpStatus status;
//    private final String errorType;
    private final String message;
}
