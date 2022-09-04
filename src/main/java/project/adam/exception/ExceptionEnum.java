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
    INVALID_TYPE(BAD_REQUEST, "입력값의 타입이 맞지 않습니다."),
    INVALID_HEADER(BAD_REQUEST, "필수 헤더가 없거나 잘못 입력되었습니다."),

    AUTHENTICATION_FAILED(UNAUTHORIZED, "로그인되지 않았습니다."),

    AUTHORIZATION_FAILED(FORBIDDEN, "권한이 없습니다."),

    NO_DATA(NOT_FOUND, "존재하지 않는 데이터입니다."),

    INVALID_METHOD(METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),

    UNIQUE_CONSTRAINT_VIOLATED(CONFLICT, "중복된 KEY가 존재합니다."),
    HIDDEN_POST(CONFLICT, "숨겨진 게시물입니다."),
    REPORTED_POST(CONFLICT, "이미 신고된 게시물입니다."),
    NOT_REPORTED_POST(CONFLICT, "아직 신고되지 않은 게시물입니다."),
    HIDDEN_COMMENT(CONFLICT, "숨겨진 댓글입니다."),
    REPORTED_COMMENT(CONFLICT, "이미 신고된 댓글입니다."),
    NOT_REPORTED_COMMENT(CONFLICT, "아직 신고되지 않은 댓글입니다.");

    private final HttpStatus status;
    private final String message;
}
