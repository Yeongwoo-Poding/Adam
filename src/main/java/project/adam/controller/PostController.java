package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.entity.Post;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.service.PostService;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.controller.dto.post.PostFindResponse;
import project.adam.service.dto.post.PostUpdateRequest;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostFindResponse createPost(@Validated @RequestBody PostCreateRequest postDto) {
        Long savedId = postService.create(postDto);
        return new PostFindResponse(postService.find(savedId));
    }

    @GetMapping("/{postId}")
    public PostFindResponse findPost(@PathVariable Long postId) {
        return new PostFindResponse(postService.find(postId));
    }

    @GetMapping
    public Slice<PostFindResponse> findAll(@ModelAttribute PostFindCondition condition, Pageable pageable) {
        Slice<Post> result = postService.findAll(condition, pageable);
        return new SliceImpl<>(
                result.getContent().stream()
                        .map(PostFindResponse::new)
                        .collect(Collectors.toList()),
                result.getPageable(),
                result.hasNext());
    }

    @PutMapping("/{postId}")
    public void updatePost(@PathVariable Long postId,
                           @Validated @RequestBody PostUpdateRequest postDto) {
        postService.update(postId, postDto);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.remove(postId);
    }
}
