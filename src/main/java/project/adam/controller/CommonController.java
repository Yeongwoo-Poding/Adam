package project.adam.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.adam.controller.dto.response.common.BoardListFindResponse;
import project.adam.controller.dto.response.common.ReportListFindResponse;

@RestController
@RequestMapping
public class CommonController {

    @GetMapping("/info/boards")
    public BoardListFindResponse getBoards() {
        return new BoardListFindResponse();
    }

    @GetMapping("/info/reports")
    public ReportListFindResponse getReports() {
        return new ReportListFindResponse(
                "신고는 반대 의견을 나타내는 기능이 아닙니다. 신고 사유에 맞지 않은 신고를 했을 경우, 해당 신고는 처리되지 않습니다.");
    }
}
