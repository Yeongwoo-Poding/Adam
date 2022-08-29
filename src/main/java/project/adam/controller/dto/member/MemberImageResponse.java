package project.adam.controller.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberImageResponse {

    private boolean hasImage;
    private String imageName;

    public MemberImageResponse(boolean hasImage, String imageName) {
        this.hasImage = hasImage;
        this.imageName = hasImage ? imageName : null;
    }
}
