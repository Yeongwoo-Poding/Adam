package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.comment.CommentFindResponse;
import project.adam.controller.dto.reply.ReplyListFindResponse;
import project.adam.entity.comment.Comment;
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
        Comment savedComment = commentService.create(member, request);
        return new CommentFindResponse(savedComment);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{commentId}")
    public CommentFindResponse findComment(@PathVariable Long commentId) {
        return new CommentFindResponse(commentService.find(commentId));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{commentId}/replies")
    public ReplyListFindResponse findReplies(@PathVariable Long commentId, Pageable pageable) {
        Comment comment = commentService.find(commentId);
        return new ReplyListFindResponse(replyService.findRepliesByComment(comment));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/{commentId}")
    public void updateComment(@PathVariable Long commentId, @Validated @RequestBody CommentUpdateRequest request) {
        Comment findComment = commentService.find(commentId);
        memberService.authorization(findComment.getWriter());
        commentService.update(findComment, request);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        Comment findComment = commentService.find(commentId);
        memberService.authorization(findComment.getWriter());
        commentService.remove(findComment);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/{commentId}/report")
    public void reportComment(@PathVariable Long commentId, @RequestBody CommentReportRequest request) {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        Comment findComment = commentService.find(commentId);
        if (member.equals(findComment.getWriter())) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }
        commentService.report(member, findComment, request);
    }
}
