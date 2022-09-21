package project.adam.service.dto.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.common.ReportType;

@Getter
@NoArgsConstructor
public class CommentReportRequest {

    private ReportType reportType;

    public CommentReportRequest(String reportType) {
        this.reportType = ReportType.valueOf(reportType);
    }
}
