package project.adam.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiExceptionEntity {

    private String code;
    private String error;
    private String message;

    public ApiExceptionEntity(ExceptionEnum e) {
        this.code = e.getStatus().value() + " " + e.getStatus().getReasonPhrase();
        this.error = e.name();
        this.message = e.getMessage();
    }
}
