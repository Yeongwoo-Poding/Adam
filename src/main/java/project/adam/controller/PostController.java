package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.adam.controller.dto.post.PostCreateResponse;
import project.adam.controller.dto.post.PostFindResponse;
import project.adam.controller.dto.post.PostListFindResponse;
import project.adam.controller.dto.post.PostUpdateResponse;
import project.adam.entity.Member;
import project.adam.entity.Post;
import project.adam.service.MemberService;
import project.adam.service.PostService;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.service.dto.post.PostReportRequest;
import project.adam.service.dto.post.PostUpdateRequest;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static project.adam.entity.Privilege.ADMIN;
import static project.adam.entity.Privilege.USER;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final MemberService memberService;
    private final PostService postService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public PostCreateResponse createPost(@RequestHeader UUID token,
                                         @Validated @RequestPart("data") PostCreateRequest postDto,
                                         @RequestPart(value = "images", required = false) MultipartFile[] images) throws IOException {
        Post savedPost = postService.create(memberService.findByToken(token).getId(), postDto, images);
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

    @PutMapping(value = "/{postId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public PostUpdateResponse updatePost(@RequestHeader UUID token,
                                         @PathVariable Long postId,
                                         @Validated @RequestPart("data") PostUpdateRequest postDto,
                                         @RequestPart(value = "images", required = false) MultipartFile[] images) throws IOException {
        Post findPost = postService.find(postId);
        Member findMember = memberService.findByToken(token);
        findMember.authorization(Objects.equals(findMember.getId(), findPost.getWriter().getId()) ? USER : ADMIN);
        postService.update(postId, postDto, images);
        return new PostUpdateResponse(findPost);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@RequestHeader UUID token,
                           @PathVariable Long postId) {
        Post findPost = postService.find(postId);
        Member findMember = memberService.findByToken(token);
        findMember.authorization(Objects.equals(findMember.getId(), findPost.getWriter().getId()) ? USER : ADMIN);
        postService.remove(postId);
    }

    @PostMapping("/{postId}/report")
    public void createReportPost(@RequestHeader UUID token,
                                 @PathVariable Long postId,
                                 @RequestBody PostReportRequest request) {
        Member findMember = memberService.findByToken(token);
        Post findPost = postService.find(postId);
        postService.createReport(findPost, findMember, request);
    }

//    @DeleteMapping("/{postId}/report")
//    public void deleteReportPost(@RequestHeader UUID token,
//                                 @PathVariable Long postId) {
//        Member findMember = memberService.findByToken(token);
//        Post findPost = postService.find(postId);
//        postService.deleteReport(findPost, findMember);
//    }
}
