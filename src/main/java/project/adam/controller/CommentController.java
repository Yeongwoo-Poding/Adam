package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.request.comment.CommentCreateControllerRequest;
import project.adam.controller.dto.request.comment.CommentReportControllerRequest;
import project.adam.controller.dto.request.comment.CommentUpdateControllerRequest;
import project.adam.controller.dto.response.comment.CommentFindResponse;
import project.adam.entity.comment.Comment;
import project.adam.security.SecurityUtils;
import project.adam.service.CommentService;
import project.adam.service.dto.comment.CommentCreateServiceRequest;
import project.adam.service.dto.comment.CommentReportServiceRequest;
import project.adam.service.dto.comment.CommentUpdateServiceRequest;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping
    public CommentFindResponse createComment(@Validated @RequestBody CommentCreateControllerRequest request)  {
        String email = SecurityUtils.getCurrentMemberEmail();
        Comment comment = commentService.create(new CommentCreateServiceRequest(email, request));
        return new CommentFindResponse(comment);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{commentId}")
    public CommentFindResponse findComment(@PathVariable Long commentId) {
        return new CommentFindResponse(commentService.find(commentId));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/{commentId}")
    public void updateComment(@PathVariable Long commentId, @Validated @RequestBody CommentUpdateControllerRequest request) {
        commentService.update(new CommentUpdateServiceRequest(commentId, request));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.remove(commentId);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/{commentId}/report")
    public void reportComment(@PathVariable Long commentId, @RequestBody CommentReportControllerRequest request) {
        String email = SecurityUtils.getCurrentMemberEmail();
        commentService.report(new CommentReportServiceRequest(email, commentId, request));
    }
}
