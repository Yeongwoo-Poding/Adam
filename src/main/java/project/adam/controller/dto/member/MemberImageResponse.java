package project.adam.controller.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberImageResponse {

    private boolean hasImage;
    private String imagePath;

    public MemberImageResponse(boolean hasImage, String path) {
        this.hasImage = hasImage;
        this.imagePath = hasImage ? path : null;
    }
}
