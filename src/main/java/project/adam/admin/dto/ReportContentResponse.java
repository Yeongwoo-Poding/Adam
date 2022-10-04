package project.adam.admin.dto;

import lombok.Getter;
import project.adam.entity.common.ReportContent;

@Getter
public class ReportContentResponse {

    private final Long id;
    private final String contentType;
    private final Long contentId;

    public ReportContentResponse(ReportContent content) {
        this.id = content.getId();
        this.contentType = content.getContentType().toString();
        this.contentId = content.getContentId();
    }
}
