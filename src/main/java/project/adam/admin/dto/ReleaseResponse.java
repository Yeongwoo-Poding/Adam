package project.adam.admin.dto;

import lombok.Getter;
import project.adam.entity.common.ReportContent;

@Getter
public class ReleaseResponse {

    private final String contentType;
    private final Long contentId;

    public ReleaseResponse(ReportContent reportContent) {
        this.contentType = reportContent.getContentType().toString();
        this.contentId = reportContent.getContentId();
    }
}
