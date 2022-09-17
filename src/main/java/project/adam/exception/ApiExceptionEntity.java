package project.adam.exception;

import lombok.Getter;

@Getter
public class ApiExceptionEntity {

    private String id;
    private String status;
    private String code;
    private String message;

    public ApiExceptionEntity(ExceptionEnum e, String id) {
        this.id = id;
        this.status = e.getStatus().value() + " " + e.getStatus().getReasonPhrase();
        this.code = e.name();
        this.message = e.getMessage();
    }
}
