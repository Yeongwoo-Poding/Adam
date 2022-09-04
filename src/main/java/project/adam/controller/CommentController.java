package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.comment.CommentCreateResponse;
import project.adam.controller.dto.comment.CommentFindResponse;
import project.adam.controller.dto.comment.CommentListFindResponse;
import project.adam.entity.Comment;
import project.adam.entity.Member;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentUpdateRequest;

import java.util.Objects;
import java.util.UUID;

import static project.adam.entity.Privilege.ADMIN;
import static project.adam.entity.Privilege.USER;

@Slf4j
@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final MemberService memberService;
    private final CommentService commentService;

    @PostMapping
    public CommentCreateResponse createComment(@RequestHeader UUID token,
                                             @PathVariable Long postId,
                                             @Validated @RequestBody CommentCreateRequest commentDto) {
        Comment savedComment = commentService.create(memberService.findByToken(token).getId(), postId, null, commentDto);
        return new CommentCreateResponse(savedComment);
    }

    @PostMapping("/{commentId}")
    public CommentCreateResponse createComment(@RequestHeader UUID token,
                                               @PathVariable Long postId,
                                               @PathVariable Long commentId,
                                               @Validated @RequestBody CommentCreateRequest commentDto) {
        Comment savedComment = commentService.create(memberService.findByToken(token).getId(), postId, commentId, commentDto);
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

    @PutMapping("/{commentId}")
    public void updateComment(@RequestHeader UUID token,
                              @PathVariable Long postId,
                              @PathVariable Long commentId,
                              @Validated @RequestBody CommentUpdateRequest commentDto) {
        Comment findComment = commentService.find(commentId);
        Member loginMember = memberService.findByToken(token);
        loginMember.authorization(Objects.equals(findComment.getWriter().getId(), loginMember.getId()) ? USER : ADMIN);
        validate(postId, findComment);
        commentService.update(commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@RequestHeader UUID token,
                              @PathVariable Long postId,
                              @PathVariable Long commentId) {
        Comment findComment = commentService.find(commentId);
        Member loginMember = memberService.findByToken(token);
        loginMember.authorization(Objects.equals(findComment.getWriter().getId(), loginMember.getId()) ? USER : ADMIN);
        validate(postId, findComment);
        commentService.remove(commentId);
    }

    @PostMapping("/{commentId}/report")
    public void createCommentReport(@RequestHeader UUID token,
                                    @PathVariable Long postId,
                                    @PathVariable Long commentId) {
        Comment findComment = commentService.find(commentId);
        Member loginMember = memberService.findByToken(token);
        validate(postId, findComment);
        commentService.createCommentReport(findComment, loginMember);
    }

    @DeleteMapping("/{commentId}/report")
    public void deleteCommentReport(@RequestHeader UUID token,
                                    @PathVariable Long postId,
                                    @PathVariable Long commentId) {
        Comment findComment = commentService.find(commentId);
        Member loginMember = memberService.findByToken(token);
        validate(postId, findComment);
        commentService.deleteCommentReport(findComment, loginMember);
    }

    private void validate(Long postId, Comment comment) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new ApiException(ExceptionEnum.NO_DATA);
        }
    }
}
