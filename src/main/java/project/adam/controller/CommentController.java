package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.comment.CommentCreateResponse;
import project.adam.controller.dto.comment.CommentFindResponse;
import project.adam.controller.dto.comment.CommentListFindResponse;
import project.adam.entity.comment.Comment;
import project.adam.entity.member.Member;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.member.MemberRepository;
import project.adam.security.SecurityUtil;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentReportRequest;
import project.adam.service.dto.comment.CommentUpdateRequest;

@Slf4j
@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final MemberService memberService;
    private final CommentService commentService;

    @Secured("ROLE_USER")
    @PostMapping
    public CommentCreateResponse createComment(@PathVariable Long postId,
                                             @Validated @RequestBody CommentCreateRequest commentDto) {
        Member member = memberService.findByEmail(SecurityUtil.getCurrentMemberEmail());
        Comment savedComment = commentService.create(member, postId, commentDto);
        return new CommentCreateResponse(savedComment);
    }

    @GetMapping("/{commentId}")
    public CommentFindResponse findComment(@PathVariable Long postId,
                                           @PathVariable Long commentId) {
        Comment findComment = commentService.find(commentId);
        validate(postId, findComment);
        return new CommentFindResponse(findComment);
    }

    @GetMapping
    public CommentListFindResponse findComments(@PathVariable Long postId, Pageable pageable) {
        Slice<Comment> result = commentService.findByPost(postId, pageable);
        return new CommentListFindResponse(result);
    }

    @Secured("ROLE_USER")
    @PutMapping("/{commentId}")
    public void updateComment(@PathVariable Long postId,
                                               @PathVariable Long commentId,
                                               @Validated @RequestBody CommentUpdateRequest commentDto) {
        Comment findComment = commentService.find(commentId);
        validate(postId, findComment);
        commentService.update(commentId, commentDto);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long postId,
                              @PathVariable Long commentId) {
        Comment findComment = commentService.find(commentId);
        validate(postId, findComment);
        commentService.remove(commentId);
    }

    @Secured("ROLE_USER")
    @PostMapping("/{commentId}/report")
    public void createCommentReport(@PathVariable Long postId,
                                    @PathVariable Long commentId,
                                    @RequestBody CommentReportRequest request) {
        Member member = memberService.findByEmail(SecurityUtil.getCurrentMemberEmail());
        Comment findComment = commentService.find(commentId);
        validate(postId, findComment);
        commentService.createCommentReport(member, commentId, request);
    }

    private void validate(Long postId, Comment comment) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new ApiException(ExceptionEnum.NO_DATA);
        }
    }
}
