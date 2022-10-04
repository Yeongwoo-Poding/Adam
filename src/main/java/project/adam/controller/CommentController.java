package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.comment.CommentFindResponse;
import project.adam.controller.dto.reply.ReplyListFindResponse;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.security.SecurityUtils;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.ReplyService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentReportRequest;
import project.adam.service.dto.comment.CommentUpdateRequest;

import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final MemberService memberService;
    private final CommentService commentService;
    private final ReplyService replyService;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping
    public CommentFindResponse createComment(@Validated @RequestBody CommentCreateRequest request)  {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        Comment comment = commentService.create(member, request);
        return new CommentFindResponse(comment);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{commentId}")
    public CommentFindResponse findComment(@PathVariable Long commentId) {
        Comment comment = commentService.find(commentId);
        validateComment(comment);

        return new CommentFindResponse(comment);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{commentId}/replies")
    public ReplyListFindResponse findReplies(@PathVariable Long commentId) {
        Comment comment = commentService.find(commentId);
        validateComment(comment);

        return new ReplyListFindResponse(replyService.findByComment(comment));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/{commentId}")
    public void updateComment(@PathVariable Long commentId, @Validated @RequestBody CommentUpdateRequest request) {
        Comment comment = commentService.find(commentId);
        validateComment(comment);

        memberService.authorization(comment.getWriter());
        commentService.update(comment, request);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        Comment comment = commentService.find(commentId);
        validateComment(comment);

        memberService.authorization(comment.getWriter());
        commentService.remove(comment);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/{commentId}/report")
    public void reportComment(@PathVariable Long commentId, @RequestBody CommentReportRequest request) {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        Comment comment = commentService.find(commentId);
        ReportType reportType = request.getReportType();
        if (reportType == null) {
            throw new ApiException(ExceptionEnum.INVALID_INPUT);
        }
        commentService.report(member, comment, reportType);
    }

    private void validateComment(Comment comment) {
        if (comment.getStatus().equals(ContentStatus.HIDDEN)) {
            throw new ApiException(ExceptionEnum.HIDDEN_CONTENT);
        }
        if (comment.getStatus().equals(ContentStatus.REMOVED)) {
            throw new NoSuchElementException();
        }
    }
}
