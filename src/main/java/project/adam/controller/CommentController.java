package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.entity.Comment;
import project.adam.entity.Member;
import project.adam.entity.Privilege;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.controller.dto.comment.CommentFindResponse;
import project.adam.service.dto.comment.CommentUpdateRequest;

import java.util.stream.Collectors;

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
    public CommentFindResponse createComment(@CookieValue("sessionId") String sessionId,
                                             @PathVariable Long postId,
                                             @Validated @RequestBody CommentCreateRequest commentDto) {
        Long savedId = commentService.create(sessionId, postId, commentDto);
        return new CommentFindResponse(commentService.find(savedId));
    }

    @GetMapping("/{commentId}")
    public CommentFindResponse findComment(@PathVariable Long postId, @PathVariable Long commentId) {
        Comment findComment = commentService.find(commentId);
        return validateComment(postId, findComment);
    }

    @GetMapping
    public Slice<CommentFindResponse> findCommentsByPost(@PathVariable Long postId, Pageable pageable) {
        Slice<Comment> result = commentService.findByPost(postId, pageable);
        return new SliceImpl<>(
                result.getContent().stream()
                        .map(CommentFindResponse::new)
                        .collect(Collectors.toList()),
                result.getPageable(),
                result.hasNext());
    }

    @PutMapping("/{commentId}")
    public void updateComment(@CookieValue("sessionId") String sessionId,
                              @PathVariable Long postId,
                              @PathVariable Long commentId,
                              @Validated @RequestBody CommentUpdateRequest commentDto) {
        Comment findComment = commentService.find(commentId);
        Member loginMember = memberService.find(sessionId);
        loginMember.authorization(findComment.getWriter().getId().equals(sessionId) ? USER : ADMIN);
        validateComment(postId, findComment);
        commentService.update(commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@CookieValue("sessionId") String sessionId,
                              @PathVariable Long postId,
                              @PathVariable Long commentId) {
        Comment findComment = commentService.find(commentId);
        Member loginMember = memberService.find(sessionId);
        loginMember.authorization(findComment.getWriter().getId().equals(sessionId) ? USER : ADMIN);
        validateComment(postId, findComment);
        commentService.remove(commentId);
    }

    private CommentFindResponse validateComment(Long postId, Comment comment) {
        validate(postId, comment);
        return new CommentFindResponse(comment);
    }

    private void validate(Long postId, Comment comment) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new ApiException(ExceptionEnum.NO_DATA);
        }
    }
}
