package project.adam.service.dto.comment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.controller.dto.request.comment.CommentReportControllerRequest;
import project.adam.entity.common.ReportType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentReportServiceRequest {

    private String email;
    private Long commentId;
    private ReportType reportType;

    public CommentReportServiceRequest(String email, Long commentId, CommentReportControllerRequest request) {
        this.email = email;
        this.commentId = commentId;
        this.reportType = request.getReportType();
    }
}
