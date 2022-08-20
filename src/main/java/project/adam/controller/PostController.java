package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.post.PostCreateResponse;
import project.adam.controller.dto.post.PostListFindResponse;
import project.adam.entity.Member;
import project.adam.entity.Post;
import project.adam.entity.Privilege;
import project.adam.service.MemberService;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.service.PostService;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.controller.dto.post.PostFindResponse;
import project.adam.service.dto.post.PostUpdateRequest;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static project.adam.entity.Privilege.ADMIN;
import static project.adam.entity.Privilege.USER;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final MemberService memberService;
    private final PostService postService;

    @PostMapping
    public PostCreateResponse createPost(@RequestHeader UUID sessionId,
                                       @Validated @RequestBody PostCreateRequest postDto) {
        Long savedId = postService.create(memberService.findBySessionId(sessionId).getId(), postDto);
        return new PostCreateResponse(postService.find(savedId));
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

    @PutMapping("/{postId}")
    @ResponseStatus(NO_CONTENT)
    public void updatePost(@RequestHeader UUID sessionId,
                           @PathVariable Long postId,
                           @Validated @RequestBody PostUpdateRequest postDto) {
        Post findPost = postService.find(postId);
        Member findMember = memberService.findBySessionId(sessionId);
        findMember.authorization(Objects.equals(findMember.getId(), findPost.getWriter().getId()) ? USER : ADMIN);
        postService.update(postId, postDto);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(NO_CONTENT)
    public void deletePost(@RequestHeader UUID sessionId,
                           @PathVariable Long postId) {
        Post findPost = postService.find(postId);
        Member findMember = memberService.findBySessionId(sessionId);
        findMember.authorization(Objects.equals(findMember.getId(), findPost.getWriter().getId()) ? USER : ADMIN);
        postService.remove(postId);
    }
}
