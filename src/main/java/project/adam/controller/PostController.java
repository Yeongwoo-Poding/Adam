package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.adam.controller.dto.comment.CommentListFindResponse;
import project.adam.controller.dto.post.PostFindResponse;
import project.adam.controller.dto.post.PostListFindResponse;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Authority;
import project.adam.entity.member.Member;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.security.SecurityUtils;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.PostService;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.service.dto.post.PostReportRequest;
import project.adam.service.dto.post.PostUpdateRequest;
import project.adam.utils.push.PushUtils;
import project.adam.utils.push.dto.PushRequest;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final PushUtils pushUtils;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public PostFindResponse createPost(@Validated @RequestPart("data") PostCreateRequest request,
                                         @RequestPart(value = "images", required = false) MultipartFile[] images)  {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());

        if (request.getBoard() == null) {
            throw new ApiException(ExceptionEnum.INVALID_INPUT);
        }

        Post savedPost;
        if (images == null) {
            savedPost = postService.create(member, request);
        } else {
            if (isImageEmpty(images)) {
                throw new ApiException(ExceptionEnum.INVALID_INPUT);
            }
            savedPost = postService.create(member, request, images);
        }

        if (savedPost.getBoard().equals(Board.NOTICE)) {
            if (member.getAuthority().equals(Authority.ROLE_ADMIN)) {
                pushUtils.pushAll(new PushRequest(savedPost.getTitle(), savedPost.getBody(), savedPost.getId()));
            } else {
                throw new ApiException(ExceptionEnum.AUTHORIZATION_FAILED);
            }
        }

        return new PostFindResponse(savedPost);
    }

    private boolean isImageEmpty(MultipartFile[] images) {
        return images[0].isEmpty();
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{postId}")
    public PostFindResponse findPost(@PathVariable Long postId) {
        return new PostFindResponse(postService.showPost(postId));
    }

    @GetMapping
    public PostListFindResponse findPosts(@ModelAttribute PostFindCondition condition, Pageable pageable) {
        return new PostListFindResponse(postService.findPosts(condition, pageable));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{postId}/comments")
    public CommentListFindResponse findComments(@PathVariable Long postId, Pageable pageable) {
        return new CommentListFindResponse(commentService.findByPost(postId, pageable));
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping(value = "/{postId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public void updatePost(@PathVariable Long postId,
                           @Validated @RequestPart("data") PostUpdateRequest request,
                           @RequestPart(value = "images", required = false) MultipartFile[] images)  {
        Post findPost = postService.find(postId);
        memberService.authorization(findPost.getWriter());
        postService.update(findPost, request, images);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        Post findPost = postService.find(postId);
        memberService.authorization(findPost.getWriter());
        postService.remove(findPost);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/{postId}/report")
    public void reportPost(@PathVariable Long postId, @RequestBody PostReportRequest request) {
        Member member = memberService.findByEmail(SecurityUtils.getCurrentMemberEmail());
        Post findPost = postService.find(postId);
        ReportType reportType = request.getReportType();
        if (reportType == null) {
            throw new ApiException(ExceptionEnum.INVALID_INPUT);
        }
        postService.report(member, findPost, reportType);
    }
}
