package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.adam.controller.dto.request.post.PostCreateControllerRequest;
import project.adam.controller.dto.request.post.PostListFindCondition;
import project.adam.controller.dto.request.post.PostReportControllerRequest;
import project.adam.controller.dto.request.post.PostUpdateControllerRequest;
import project.adam.controller.dto.response.comment.CommentListFindResponse;
import project.adam.controller.dto.response.post.PostFindResponse;
import project.adam.controller.dto.response.post.PostListFindResponse;
import project.adam.entity.post.Post;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.security.SecurityUtils;
import project.adam.service.PostService;
import project.adam.service.dto.post.PostCreateServiceRequest;
import project.adam.service.dto.post.PostReportServiceRequest;
import project.adam.service.dto.post.PostUpdateServiceRequest;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public PostFindResponse createPost(@Validated @RequestPart("data") PostCreateControllerRequest request,
                                         @RequestPart(value = "images", required = false) MultipartFile[] images)  {
        Post savedPost;
        if (images == null) {
            savedPost = postService.create(new PostCreateServiceRequest(SecurityUtils.getCurrentMemberEmail(), request));
        } else {
            if (isImageEmpty(images) || images.length > 10) {
                throw new ApiException(ExceptionEnum.INVALID_INPUT);
            }
            savedPost = postService.create(new PostCreateServiceRequest(SecurityUtils.getCurrentMemberEmail(), request), images);
        }

        return new PostFindResponse(savedPost);
    }

    private boolean isImageEmpty(MultipartFile[] images) {
        return images[0].isEmpty();
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{postId}")
    public PostFindResponse findPost(@PathVariable Long postId) {
        return new PostFindResponse(postService.find(postId));
    }

    @GetMapping
    public PostListFindResponse findPosts(@ModelAttribute PostListFindCondition condition, Pageable pageable) {
        return new PostListFindResponse(postService.findPosts(condition, pageable));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{postId}/comments")
    public CommentListFindResponse findComments(@PathVariable Long postId) {
        return new CommentListFindResponse(postService.findComments(postId));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping(value = "/{postId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public void updatePost(@PathVariable Long postId,
                           @Validated @RequestPart("data") PostUpdateControllerRequest request,
                           @RequestPart(value = "images", required = false) MultipartFile[] images)  {
        if (images == null) {
            postService.update(new PostUpdateServiceRequest(postId, request));
        } else {
            if (isImageEmpty(images) || images.length > 10) {
                throw new ApiException(ExceptionEnum.INVALID_INPUT);
            }
            postService.update(new PostUpdateServiceRequest(postId, request), images);
        }
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.remove(postId);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/{postId}/report")
    public void reportPost(@PathVariable Long postId, @RequestBody PostReportControllerRequest request) {
        String email = SecurityUtils.getCurrentMemberEmail();
        postService.report(new PostReportServiceRequest(email, postId, request));
    }
}
