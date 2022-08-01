package project.adam.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiExceptionEntity {

    private String errorType;
    private String errorMessage;

    public ApiExceptionEntity(String errorType, String errorMessage) {
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }
}
