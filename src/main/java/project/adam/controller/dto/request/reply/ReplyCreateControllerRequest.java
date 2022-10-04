package project.adam.controller.dto.request.reply;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReplyCreateControllerRequest {

    @NotNull
    public Long commentId;

    @NotEmpty
    private String body;
}
