package project.adam.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FcmPushRequest {

    private String title;
    private String body;
    private Long postId;
}
