package project.adam.controller.dto.response.common;

import lombok.Getter;
import project.adam.entity.common.ReportType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class ReportListFindResponse {

    private final List<ReportListContent> reports;
    private final String commonDescription;

    public ReportListFindResponse(String commonDescription) {
        this.reports = Stream.of(ReportType.values())
                .map(reportType -> new ReportListContent(reportType.toString(), reportType.title, reportType.description))
                .collect(Collectors.toList());
        this.commonDescription = commonDescription;
    }
}
