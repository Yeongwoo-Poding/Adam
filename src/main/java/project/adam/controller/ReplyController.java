package project.adam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.reply.ReplyCreateResponse;
import project.adam.controller.dto.reply.ReplyFindResponse;
import project.adam.controller.dto.reply.ReplyListFindResponse;
import project.adam.controller.dto.reply.ReplyUpdateResponse;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.entity.member.Privilege;
import project.adam.entity.reply.Reply;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.ReplyService;
import project.adam.service.dto.reply.ReplyCreateRequest;
import project.adam.service.dto.reply.ReplyReportRequest;
import project.adam.service.dto.reply.ReplyUpdateRequest;

import java.util.UUID;

@RestController
@RequestMapping("/posts/{postId}/comments/{commentId}/replies")
@RequiredArgsConstructor
public class ReplyController {

    private final MemberService memberService;
    private final CommentService commentService;
    private final ReplyService replyService;

    @PostMapping
    public ReplyCreateResponse createReply(@RequestHeader UUID token,
                                           @PathVariable Long commentId,
                                           @Validated @RequestBody ReplyCreateRequest createDto) {
        Member loginMember = memberService.findByToken(token);
        return new ReplyCreateResponse(replyService.create(loginMember, commentId, createDto));
    }

    @GetMapping("/{replyId}")
    public ReplyFindResponse findReply(@PathVariable Long commentId,
                                       @PathVariable Long replyId) {
        validate(commentId, replyId);
        return new ReplyFindResponse(replyService.find(replyId));
    }

    @GetMapping
    public ReplyListFindResponse findReplies(@PathVariable Long commentId, Pageable pageable) {
        return new ReplyListFindResponse(replyService.findAllByComment(commentId, pageable));
    }

    @PutMapping("/{replyId}")
    public ReplyUpdateResponse updateReply(@RequestHeader UUID token,
                                           @PathVariable Long commentId,
                                           @PathVariable Long replyId,
                                           @Validated @RequestBody ReplyUpdateRequest updateDto) {
        validate(commentId, replyId);
        Member loginMember = memberService.findByToken(token);
        Reply findReply = replyService.find(replyId);
        loginMember.authorization(findReply.getWriter().getId() == loginMember.getId() ? Privilege.USER : Privilege.ADMIN);
        findReply.update(updateDto.getBody());
        return new ReplyUpdateResponse(findReply);
    }

    @DeleteMapping("/{replyId}")
    public void deleteReply(@RequestHeader UUID token,
                            @PathVariable Long commentId,
                            @PathVariable Long replyId) {
        validate(commentId, replyId);
        Member loginMember = memberService.findByToken(token);
        Reply findReply = replyService.find(replyId);
        loginMember.authorization(findReply.getWriter().getId() == loginMember.getId() ? Privilege.USER : Privilege.ADMIN);
        replyService.delete(replyId);
    }

    @PostMapping("/{replyId}/report")
    public void reportReply(@RequestHeader UUID token,
                            @PathVariable Long commentId,
                            @PathVariable Long replyId,
                            @Validated @RequestBody ReplyReportRequest reportDto) {
        validate(commentId, replyId);
        Member loginMember = memberService.findByToken(token);
        replyService.report(loginMember, replyId, ReportType.valueOf(reportDto.getReportType()));
    }

    private void validate(Long commentId, Long replyId) {
        Reply reply = replyService.find(replyId);
        if (reply.getComment().getId() != commentId) {
            throw new ApiException(ExceptionEnum.NO_DATA);
        }
    }
}
