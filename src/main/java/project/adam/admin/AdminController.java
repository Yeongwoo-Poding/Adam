package project.adam.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import project.adam.admin.dto.ReportContentDetail;
import project.adam.admin.dto.ReportContentListResponse;
import project.adam.entity.common.ReportContent;
import project.adam.entity.member.Member;

@Secured("ROLE_ADMIN")
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/reports/{reportId}/ban")
    public BanResponse ban(@PathVariable Long reportId, @RequestParam(required = false, defaultValue = "0") int days) {
        ReportContent reportContent = adminService.findReportContent(reportId);
        Member member = adminService.ban(reportContent, days);
        return new BanResponse(member);
    }

    @DeleteMapping("/reports/{reportId}/release")
    public String release(@PathVariable Long reportId) {
        ReportContent reportContent = adminService.findReportContent(reportId);
        adminService.release(reportContent);
        return "ok";
    }

    @GetMapping("/reports")
    public ReportContentListResponse findReportContents() {
        return new ReportContentListResponse(adminService.findReportContents());
    }

    @GetMapping("/reports/{reportId}")
    public ReportContentDetail findReportContent(@PathVariable Long reportId) {
        return adminService.getReportContentDetail(reportId);
    }
}
