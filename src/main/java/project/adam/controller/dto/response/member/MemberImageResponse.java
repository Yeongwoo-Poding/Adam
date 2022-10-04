package project.adam.controller.dto.response.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberImageResponse {

    private final boolean hasImage;
    private final String imageName;

    public MemberImageResponse(boolean hasImage, String imageName) {
        this.hasImage = hasImage;
        this.imageName = hasImage ? imageName : null;
    }
}
