package project.adam.controller.dto.request.post;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.common.ReportType;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostReportControllerRequest {

    @NotNull
    private ReportType reportType;
}
