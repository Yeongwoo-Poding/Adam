package project.adam.service.dto.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CommentReportRequest {

    private String reportType;

    public CommentReportRequest(String reportType) {
        this.reportType = reportType;
    }
}
