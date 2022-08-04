package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.exception.ApiException;
import project.adam.service.MemberService;
import project.adam.service.PostService;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostFindResponse;
import project.adam.service.dto.post.PostListFindResponse;
import project.adam.service.dto.post.PostUpdateRequest;
import static org.springframework.util.StringUtils.*;
import static project.adam.exception.ExceptionEnum.INVALID_DATA;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final MemberService memberService;

    @PostMapping("/new")
    public PostFindResponse createPost(@Validated @RequestBody PostCreateRequest postDto,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ApiException(INVALID_DATA);
        }

        Long savedId = postService.create(postDto);
        return postService.find(savedId);
    }

    @GetMapping("/{postId}")
    public PostFindResponse findPost(@PathVariable Long postId) {
        return postService.find(postId);
    }

    @PutMapping("/{postId}")
    public void updatePost(@PathVariable Long postId,
                           @Validated @RequestBody PostUpdateRequest postDto,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ApiException(INVALID_DATA);
        }
        
        postService.update(postId, postDto);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.remove(postId);
    }

    @GetMapping
    public PostListFindResponse findAll(@RequestParam(required = false) String writerId) {
        return hasText(writerId) ?
                new PostListFindResponse(postService.findAllByWriter(writerId)) :
                new PostListFindResponse(postService.findAll());
    }
}
