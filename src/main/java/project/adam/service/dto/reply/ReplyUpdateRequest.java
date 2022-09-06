package project.adam.service.dto.reply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyUpdateRequest {

    @NotEmpty
    private String body;
}
