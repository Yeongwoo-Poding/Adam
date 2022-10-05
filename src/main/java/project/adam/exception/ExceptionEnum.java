package project.adam.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {
    UNKNOWN_EXCEPTION(INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다."),

    INVALID_PARAMETER(BAD_REQUEST, "파라미터가 없거나 잘못 입력되었습니다."),
    INVALID_JSON_FORMAT(BAD_REQUEST, "입력값이 JSON 형식이 아닙니다."),
    INVALID_INPUT(BAD_REQUEST, "혀용되지 않은 입력값입니다."),
    INVALID_TYPE(BAD_REQUEST, "입력값의 타입이 맞지 않습니다."),
    INVALID_HEADER(BAD_REQUEST, "필수 헤더가 없거나 잘못 입력되었습니다."),
    INVALID_METHOD(BAD_REQUEST, "허용되지 않은 메서드입니다."),

    AUTHENTICATION_FAILED(UNAUTHORIZED, "로그인되지 않았습니다."),

    AUTHORIZATION_FAILED(FORBIDDEN, "권한이 없습니다."),
    HIDDEN_CONTENT(FORBIDDEN, "숨겨진 컨텐츠입니다."),

    NO_DATA(NOT_FOUND, "존재하지 않는 데이터입니다."),

    DUPLICATED(CONFLICT, "중복된 데이터가 존재합니다."),
    INVALID_REPORT(CONFLICT, "신고할 수 없습니다."),
    SUSPENDED(CONFLICT, "정지된 사용자입니다."),
    INVALID_COMMENT(CONFLICT, "대댓글에는 댓글을 달 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
