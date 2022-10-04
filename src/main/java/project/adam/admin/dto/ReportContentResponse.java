package project.adam.admin.dto;

import lombok.Getter;
import project.adam.entity.common.ReportContent;

@Getter
public class ReportContentResponse {

    private Long id;
    private String contentType;
    private Long contentId;

    public ReportContentResponse(ReportContent content) {
        this.id = content.getId();
        this.contentType = content.getContentType().toString();
        this.contentId = content.getContentId();
    }
}
