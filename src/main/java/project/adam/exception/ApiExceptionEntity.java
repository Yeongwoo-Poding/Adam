package project.adam.exception;

import lombok.Getter;

@Getter
public class ApiExceptionEntity {

    private final String id;
    private final String status;
    private final String code;
    private final String message;

    public ApiExceptionEntity(ExceptionEnum e, String id) {
        this.id = id;
        this.status = e.getStatus().value() + " " + e.getStatus().getReasonPhrase();
        this.code = e.name();
        this.message = e.getMessage();
    }
}
