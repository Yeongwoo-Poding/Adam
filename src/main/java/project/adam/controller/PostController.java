package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.entity.Post;
import project.adam.entity.Privilege;
import project.adam.service.MemberService;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.service.PostService;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.controller.dto.post.PostFindResponse;
import project.adam.service.dto.post.PostUpdateRequest;

import java.util.stream.Collectors;

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
    public PostFindResponse createPost(@CookieValue("sessionId") String sessionId,
                                       @Validated @RequestBody PostCreateRequest postDto) {
        Long savedId = postService.create(sessionId, postDto);

        log.info("Create Post {}", savedId);
        return new PostFindResponse(postService.find(savedId));
    }

    @GetMapping("/{postId}")
    public PostFindResponse findPost(@PathVariable Long postId) {
        return new PostFindResponse(postService.find(postId));
    }

    @GetMapping
    public Slice<PostFindResponse> findAll(@ModelAttribute PostFindCondition condition, Pageable pageable) {
        Slice<Post> result = postService.findAll(condition, pageable);

        log.info("Find Post Page {} (Size: {}), Condition: {}", pageable.getPageNumber(), pageable.getPageSize(), condition.toString());
        return new SliceImpl<>(
                result.getContent().stream()
                        .map(PostFindResponse::new)
                        .collect(Collectors.toList()),
                result.getPageable(),
                result.hasNext());
    }

    @PutMapping("/{postId}")
    public void updatePost(@CookieValue("sessionId") String sessionId,
                           @PathVariable Long postId,
                           @Validated @RequestBody PostUpdateRequest postDto) {
        Post findPost = postService.find(postId);
        memberService.find(sessionId).authorization(sessionId.equals(findPost.getWriter().getId()) ? USER : ADMIN);
        postService.update(postId, postDto);

        log.info("Update Post {}", postId);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@CookieValue("sessionId") String sessionId,
                           @PathVariable Long postId) {
        Post findPost = postService.find(postId);
        memberService.find(sessionId).authorization(sessionId.equals(findPost.getWriter().getId()) ? USER : ADMIN);
        postService.remove(postId);

        log.info("Delete Post {}", postId);
    }
}
