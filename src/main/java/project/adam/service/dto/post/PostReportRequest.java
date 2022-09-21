package project.adam.service.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.adam.entity.common.ReportType;

@Getter @Setter
@NoArgsConstructor
public class PostReportRequest {

    private ReportType report;

    public PostReportRequest(String reportType) {
        this.report = ReportType.valueOf(reportType);
    }
}
