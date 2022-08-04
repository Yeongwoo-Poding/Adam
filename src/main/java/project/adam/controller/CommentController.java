package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.exception.ApiException;
import project.adam.service.CommentService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentFindResponse;
import project.adam.service.dto.comment.CommentListFindResponse;
import project.adam.service.dto.comment.CommentUpdateRequest;

import static project.adam.exception.ExceptionEnum.INVALID_DATA;

@Slf4j
@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/new")
    public CommentFindResponse createComment(@PathVariable Long postId,
                                             @Validated @RequestBody CommentCreateRequest commentDto,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ApiException(INVALID_DATA);
        }

        Long savedId = commentService.create(postId, commentDto);
        return commentService.find(savedId);
    }

    @GetMapping("/{commentId}")
    public CommentFindResponse findComment(@PathVariable Long commentId) {
        return commentService.find(commentId);
    }

    @PutMapping("/{commentId}")
    public void updateComment(@PathVariable Long commentId,
                              @Validated @RequestBody CommentUpdateRequest commentDto,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ApiException(INVALID_DATA);
        }

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
