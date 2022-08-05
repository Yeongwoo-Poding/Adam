package project.adam.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {
    INVALID_METHOD(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),
    INVALID_INPUT(BAD_REQUEST, "잘못된 데이터가 입력되었습니다."),
    NO_DATA(NOT_FOUND, "존재하지 않는 데이터입니다."),
    UNIQUE_CONSTRAINT_VIOLATED(CONFLICT, "무결성 제약조건에 위배됩니다.");

    private final HttpStatus status;
    private final String message;
}
