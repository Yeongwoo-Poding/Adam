package project.adam.admin.dto;

import lombok.Getter;
import project.adam.entity.common.ReportContent;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReportContentListResponse {

    List<ReportContentResponse> reportContents;

    public ReportContentListResponse(List<ReportContent> contents) {
        this.reportContents = contents.stream()
                .map(ReportContentResponse::new)
                .collect(Collectors.toList());
    }
}
