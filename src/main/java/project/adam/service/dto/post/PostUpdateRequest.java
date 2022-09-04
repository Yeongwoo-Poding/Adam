package project.adam.service.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequest {

    @NotEmpty
    private String title;

    private String body;

    private Integer thumbnailIndex;

    public PostUpdateRequest(String title, String body) {
        this.title = title;
        this.body = body;
        this.thumbnailIndex = null;
    }
}
