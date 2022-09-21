package project.adam.service.dto.reply;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.common.ReportType;

@Getter
@NoArgsConstructor
public class ReplyReportRequest {

    private ReportType reportType;

    public ReplyReportRequest(String reportType) {
        this.reportType = ReportType.valueOf(reportType);
    }
}
