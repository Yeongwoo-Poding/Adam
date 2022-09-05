package project.adam.service.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class PostReportRequest {

    private String reportType;

    public PostReportRequest(String reportType) {
        this.reportType = reportType;
    }
}
