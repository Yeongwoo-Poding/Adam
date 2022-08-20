package project.adam.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {

    INVALID_PARAMETER(BAD_REQUEST, "파라미터가 없거나 잘못 입력되었습니다."),
    INVALID_JSON_FORMAT(BAD_REQUEST, "입력값이 JSON 형식이 아닙니다."),
    INVALID_INPUT(BAD_REQUEST, "혀용되지 않은 입력값입니다."),
    INVALID_TYPE(BAD_REQUEST, "입력값의 형식이 맞지 않습니다."),
    INVALID_HEADER(BAD_REQUEST, "Header에 필요한 데이터가 전달되지 않았습니다."),

    AUTHENTICATION_FAILED(UNAUTHORIZED, "로그인되지 않았습니다."),

    AUTHORIZATION_FAILED(FORBIDDEN, "권한이 없습니다."),

    NO_DATA(NOT_FOUND, "존재하지 않는 데이터입니다."),

    INVALID_METHOD(METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),

    UNIQUE_CONSTRAINT_VIOLATED(CONFLICT, "중복된 KEY가 존재합니다.");

    private final HttpStatus status;
    private final String message;
}
