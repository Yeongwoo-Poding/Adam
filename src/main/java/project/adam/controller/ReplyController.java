package project.adam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.request.reply.ReplyCreateControllerRequest;
import project.adam.controller.dto.request.reply.ReplyReportControllerRequest;
import project.adam.controller.dto.request.reply.ReplyUpdateControllerRequest;
import project.adam.controller.dto.response.reply.ReplyFindResponse;
import project.adam.security.SecurityUtils;
import project.adam.service.ReplyService;
import project.adam.service.dto.reply.ReplyCreateServiceRequest;
import project.adam.service.dto.reply.ReplyReportServiceRequest;
import project.adam.service.dto.reply.ReplyUpdateServiceRequest;

@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping
    public ReplyFindResponse createReply(@Validated @RequestBody ReplyCreateControllerRequest request)  {
        String email = SecurityUtils.getCurrentMemberEmail();
        return new ReplyFindResponse(replyService.create(new ReplyCreateServiceRequest(email, request)));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{replyId}")
    public ReplyFindResponse findReply(@PathVariable Long replyId) {
        return new ReplyFindResponse(replyService.find(replyId));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/{replyId}")
    public void updateReply(@PathVariable Long replyId, @Validated @RequestBody ReplyUpdateControllerRequest request) {
        replyService.update(new ReplyUpdateServiceRequest(replyId, request));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping("/{replyId}")
    public void deleteReply(@PathVariable Long replyId) {
        replyService.remove(replyId);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/{replyId}/report")
    public void reportReply(@PathVariable Long replyId, @Validated @RequestBody ReplyReportControllerRequest request) {
        String email = SecurityUtils.getCurrentMemberEmail();
        replyService.report(new ReplyReportServiceRequest(email, replyId, request));
    }
}
