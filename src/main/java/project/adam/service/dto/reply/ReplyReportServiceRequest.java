package project.adam.service.dto.reply;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.controller.dto.request.reply.ReplyReportControllerRequest;
import project.adam.entity.common.ReportType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyReportServiceRequest {

    private String email;
    private Long replyId;
    private ReportType reportType;

    public ReplyReportServiceRequest(String email, Long replyId, ReplyReportControllerRequest request) {
        this.email = email;
        this.replyId = replyId;
        this.reportType = request.getReportType();
    }
}
