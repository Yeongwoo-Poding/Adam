package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.adam.controller.dto.post.PostCreateResponse;
import project.adam.controller.dto.post.PostFindResponse;
import project.adam.controller.dto.post.PostListFindResponse;
import project.adam.entity.post.Post;
import project.adam.service.PostService;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.service.dto.post.PostReportRequest;
import project.adam.service.dto.post.PostUpdateRequest;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Secured("ROLE_USER")
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public PostCreateResponse createPost(@Validated @RequestPart("data") PostCreateRequest postDto,
                                         @RequestPart(value = "images", required = false) MultipartFile[] images) throws IOException {
        Post savedPost = postService.create(postDto, images);
        return new PostCreateResponse(savedPost);
    }

    @GetMapping("/{postId}")
    public PostFindResponse findPost(@PathVariable Long postId) {
        return new PostFindResponse(postService.find(postId));
    }

    @GetMapping
    public PostListFindResponse findAll(@ModelAttribute PostFindCondition condition, Pageable pageable) {
        Slice<Post> result = postService.findAll(condition, pageable);
        return new PostListFindResponse(result);
    }

    @Secured("ROLE_USER")
    @PutMapping(value = "/{postId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public void updatePost(@PathVariable Long postId,
                                         @Validated @RequestPart("data") PostUpdateRequest postDto,
                                         @RequestPart(value = "images", required = false) MultipartFile[] images) throws IOException {
        postService.update(postId, postDto, images);
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.remove(postId);
    }

    @Secured("ROLE_USER")
    @PostMapping("/{postId}/report")
    public void createReportPost(@PathVariable Long postId,
                                 @RequestBody PostReportRequest request) {
        postService.createReport(postId, request);
    }
}
