package project.adam.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FcmTestRequest {
    private String title;
    private String body;
    private Long postId;
}
