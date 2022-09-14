package project.adam.controller.dto.common;

import lombok.Getter;
import project.adam.entity.common.ReportType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class ReportListFindResponse {

    private List<ReportListContent> reports = new ArrayList<>();
    private String commonDescription;

    public ReportListFindResponse(String commonDescription) {
        this.reports = Stream.of(ReportType.values())
                .map(reportType -> new ReportListContent(reportType.toString(), reportType.title, reportType.description))
                .collect(Collectors.toList());
        this.commonDescription = commonDescription;
    }
}
