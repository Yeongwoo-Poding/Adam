package project.adam.service.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.common.ReportType;

@Getter
@NoArgsConstructor
public class PostReportRequest {

    private ReportType reportType;

    public PostReportRequest(String reportType) {
        this.reportType = ReportType.valueOf(reportType);
    }
}
