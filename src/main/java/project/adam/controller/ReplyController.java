package project.adam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.reply.ReplyFindResponse;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.entity.reply.Reply;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.security.SecurityUtils;
import project.adam.service.MemberService;
import project.adam.service.ReplyService;
import project.adam.service.dto.reply.ReplyCreateRequest;
import project.adam.service.dto.reply.ReplyReportRequest;
import project.adam.service.dto.reply.ReplyUpdateRequest;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
public class ReplyController {

    private final MemberService memberService;
    private final ReplyService replyService;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping
    public ReplyFindResponse createReply(@Validated @RequestBody ReplyCreateRequest request)  {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        return new ReplyFindResponse(replyService.create(member, request));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{replyId}")
    public ReplyFindResponse findReply(@PathVariable Long replyId) {
        Reply reply = replyService.find(replyId);
        validateReply(reply);

        return new ReplyFindResponse(reply);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/{replyId}")
    public void updateReply(@PathVariable Long replyId, @Validated @RequestBody ReplyUpdateRequest request) {
        Reply reply = replyService.find(replyId);
        validateReply(reply);

        memberService.authorization(reply.getWriter());
        replyService.update(reply, request);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping("/{replyId}")
    public void deleteReply(@PathVariable Long replyId) {
        Reply reply = replyService.find(replyId);
        validateReply(reply);

        memberService.authorization(reply.getWriter());
        replyService.remove(reply);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/{replyId}/report")
    public void reportReply(@PathVariable Long replyId, @Validated @RequestBody ReplyReportRequest request) {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        Reply reply = replyService.find(replyId);
        ReportType reportType = request.getReportType();
        if (reportType == null) {
            throw new ApiException(ExceptionEnum.INVALID_INPUT);
        }
        replyService.report(member, reply, reportType);
    }

    private void validateReply(Reply reply) {
        if (reply.getStatus().equals(ContentStatus.HIDDEN)) {
            throw new ApiException(ExceptionEnum.HIDDEN_CONTENT);
        }
        if (reply.getStatus().equals(ContentStatus.REMOVED)) {
            throw new NoSuchElementException();
        }
    }
}
