package project.adam.admin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import project.adam.entity.common.ReportContent;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportContentDetail {

    private Long id;
    private String contentType;
    private Long contentId;
    private String title;
    private String body;

    public ReportContentDetail(ReportContent content, String title, String body) {
        this.id = content.getId();
        this.contentType = content.getContentType().toString();
        this.contentId = content.getContentId();
        this.title = title;
        this.body = body;
    }
}
