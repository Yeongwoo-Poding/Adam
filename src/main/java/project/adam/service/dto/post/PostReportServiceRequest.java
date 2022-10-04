package project.adam.service.dto.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.controller.dto.request.post.PostReportControllerRequest;
import project.adam.entity.common.ReportType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostReportServiceRequest {

    private String email;
    private Long postId;
    private ReportType reportType;

    public PostReportServiceRequest(String email, Long postId, PostReportControllerRequest request) {
        this.email = email;
        this.postId = postId;
        this.reportType = request.getReportType();
    }
}
