package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import project.adam.service.CommentService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentFindResponse;
import project.adam.service.dto.comment.CommentListFindResponse;
import project.adam.service.dto.comment.CommentUpdateRequest;

@Slf4j
@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/new")
    public CommentFindResponse createComment(@PathVariable Long postId, @RequestBody CommentCreateRequest commentDto) {
        Long savedId = commentService.create(postId, commentDto);
        return commentService.find(savedId);
    }

    @GetMapping("/{commentId}")
    public CommentFindResponse findComment(@PathVariable Long commentId) {
        return commentService.find(commentId);
    }

    @PatchMapping("/{commentId}")
    public void updateComment(@PathVariable Long commentId, @RequestBody CommentUpdateRequest commentDto) {
        commentService.update(commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.remove(commentId);
    }

    @GetMapping
    public CommentListFindResponse findCommentsByPost(@PathVariable Long postId) {
        return new CommentListFindResponse(commentService.findByPost(postId));
    }
}
