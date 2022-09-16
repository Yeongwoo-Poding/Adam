package project.adam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.reply.ReplyCreateResponse;
import project.adam.controller.dto.reply.ReplyFindResponse;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.entity.reply.Reply;
import project.adam.security.SecurityUtils;
import project.adam.service.MemberService;
import project.adam.service.ReplyService;
import project.adam.service.dto.reply.ReplyCreateRequest;
import project.adam.service.dto.reply.ReplyReportRequest;
import project.adam.service.dto.reply.ReplyUpdateRequest;

import java.io.IOException;

@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
public class ReplyController {

    private final MemberService memberService;
    private final ReplyService replyService;

    @Secured("ROLE_USER")
    @PostMapping
    public ReplyCreateResponse createReply(@Validated @RequestBody ReplyCreateRequest createDto) throws IOException {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        return new ReplyCreateResponse(replyService.create(member, createDto));
    }

    @GetMapping("/{replyId}")
    public ReplyFindResponse findReply(@PathVariable Long replyId) {
        return new ReplyFindResponse(replyService.find(replyId));
    }

    @Secured("ROLE_USER")
    @PutMapping("/{replyId}")
    public void updateReply(@PathVariable Long replyId, @Validated @RequestBody ReplyUpdateRequest updateDto) {
        Reply findReply = replyService.find(replyId);
        memberService.authorization(findReply.getWriter());
        replyService.update(findReply, updateDto.getBody());
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{replyId}")
    public void deleteReply(@PathVariable Long replyId) {
        Reply findReply = replyService.find(replyId);
        memberService.authorization(findReply.getWriter());
        replyService.delete(findReply);
    }

    @Secured("ROLE_USER")
    @PostMapping("/{replyId}/report")
    public void reportReply(@PathVariable Long replyId, @Validated @RequestBody ReplyReportRequest reportDto) {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        Reply findReply = replyService.find(replyId);
        replyService.report(member, findReply, ReportType.valueOf(reportDto.getReportType()));
    }
}
