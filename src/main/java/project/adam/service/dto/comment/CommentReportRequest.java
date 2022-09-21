package project.adam.service.dto.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.common.ReportType;

@Getter
@NoArgsConstructor
public class CommentReportRequest {

    private ReportType report;

    public CommentReportRequest(String reportType) {
        this.report = ReportType.valueOf(reportType);
    }
}
