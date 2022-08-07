package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.entity.Comment;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.service.CommentService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.controller.dto.comment.CommentFindResponse;
import project.adam.controller.dto.comment.CommentListFindResponse;
import project.adam.service.dto.comment.CommentUpdateRequest;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentFindResponse createComment(@PathVariable Long postId,
                                             @Validated @RequestBody CommentCreateRequest commentDto) {
        Long savedId = commentService.create(postId, commentDto);
        return new CommentFindResponse(commentService.find(savedId));
    }

    @GetMapping("/{commentId}")
    public CommentFindResponse findComment(@PathVariable Long postId, @PathVariable Long commentId) {
        return validateComment(postId, commentId);
    }

    @GetMapping
    public CommentListFindResponse findCommentsByPost(@PathVariable Long postId) {
        return new CommentListFindResponse(
                commentService.findByPost(postId).stream()
                        .map(CommentFindResponse::new)
                        .collect(Collectors.toList())
        );
    }

    @PutMapping("/{commentId}")
    public void updateComment(@PathVariable Long postId,
                              @PathVariable Long commentId,
                              @Validated @RequestBody CommentUpdateRequest commentDto) {
        validateComment(postId, commentId);
        commentService.update(commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long postId,
                              @PathVariable Long commentId) {
        validateComment(postId, commentId);
        commentService.remove(commentId);
    }

    private CommentFindResponse validateComment(Long postId, Long commentId) {
        Comment comment = commentService.find(commentId);
        validate(postId, comment);
        return new CommentFindResponse(comment);
    }

    private void validate(Long postId, Comment comment) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new ApiException(ExceptionEnum.NO_DATA);
        }
    }
}
