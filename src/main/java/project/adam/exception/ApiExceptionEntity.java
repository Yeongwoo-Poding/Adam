package project.adam.exception;

import lombok.Getter;

@Getter
public class ApiExceptionEntity {

    private String errorMessage;

    public ApiExceptionEntity(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
