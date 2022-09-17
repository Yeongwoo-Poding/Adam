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

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final MemberService memberService;
    private final CommentService commentService;
    private final ReplyService replyService;

    @Secured("ROLE_USER")
    @PostMapping
    public CommentFindResponse createComment(@Validated @RequestBody CommentCreateRequest commentDto) throws IOException {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        Comment savedComment = commentService.create(member, commentDto);
        return new CommentFindResponse(savedComment);
    }

    @Secured("ROLE_USER")
    @GetMapping("/{commentId}")
    public CommentFindResponse findComment(@PathVariable Long commentId) {
        return new CommentFindResponse(commentService.find(commentId));
    }

    @Secured("ROLE_USER")
    @GetMapping("/{commentId}/replies")
    public ReplyListFindResponse findReplies(@PathVariable Long commentId, Pageable pageable) {
        return new ReplyListFindResponse(replyService.findAllByComment(commentId, pageable));
    }

    @Secured("ROLE_USER")
    @PutMapping("/{commentId}")
    public void updateComment(@PathVariable Long commentId, @Validated @RequestBody CommentUpdateRequest commentDto) {
        Comment findComment = commentService.find(commentId);
        memberService.authorization(findComment.getWriter());
        commentService.update(findComment, commentDto);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        Comment findComment = commentService.find(commentId);
        memberService.authorization(findComment.getWriter());
        commentService.remove(findComment);
    }

    @Secured("ROLE_USER")
    @PostMapping("/{commentId}/report")
    public void reportComment(@PathVariable Long commentId, @RequestBody CommentReportRequest request) {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        Comment findComment = commentService.find(commentId);
        if (member.equals(findComment.getWriter())) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }
        commentService.createCommentReport(member, findComment, request);
    }
}
