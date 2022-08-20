package project.adam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.adam.controller.dto.comment.CommentCreateResponse;
import project.adam.controller.dto.comment.CommentListFindResponse;
import project.adam.entity.Comment;
import project.adam.entity.Member;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.controller.dto.comment.CommentFindResponse;
import project.adam.service.dto.comment.CommentUpdateRequest;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static project.adam.entity.Privilege.ADMIN;
import static project.adam.entity.Privilege.USER;

@Slf4j
@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final MemberService memberService;
    private final CommentService commentService;

    @PostMapping
    public CommentFindResponse createComment(@RequestHeader UUID sessionId,
                                             @PathVariable Long postId,
                                             @Validated @RequestBody CommentCreateRequest commentDto) {
        Long savedId = commentService.create(memberService.findBySessionId(sessionId).getId(), postId, null, commentDto);
        return new CommentFindResponse(commentService.find(savedId));
    }

    @PostMapping("/{commentId}")
    public CommentCreateResponse createComment(@RequestHeader UUID sessionId,
                                               @PathVariable Long postId,
                                               @PathVariable Long commentId,
                                               @Validated @RequestBody CommentCreateRequest commentDto) {
        Long savedId = commentService.create(memberService.findBySessionId(sessionId).getId(), postId, commentId, commentDto);
        return new CommentCreateResponse(commentService.find(savedId));
    }

    @GetMapping("/{commentId}")
    public CommentFindResponse findComment(@PathVariable Long postId,
                                           @PathVariable Long commentId) {
        Comment findComment = commentService.find(commentId);
        validate(postId, findComment);
        return new CommentFindResponse(findComment);
    }

    @GetMapping
    public CommentListFindResponse findComments(@PathVariable Long postId, Pageable pageable) {
        Slice<Comment> result = commentService.findByPost(postId, pageable);
        return new CommentListFindResponse(result);
    }

    @PutMapping("/{commentId}")
    @ResponseStatus(NO_CONTENT)
    public void updateComment(@RequestHeader UUID sessionId,
                              @PathVariable Long postId,
                              @PathVariable Long commentId,
                              @Validated @RequestBody CommentUpdateRequest commentDto) {
        Comment findComment = commentService.find(commentId);
        Member loginMember = memberService.findBySessionId(sessionId);
        loginMember.authorization(Objects.equals(findComment.getWriter().getId(), loginMember.getId()) ? USER : ADMIN);
        validate(postId, findComment);
        commentService.update(commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteComment(@RequestHeader UUID sessionId,
                              @PathVariable Long postId,
                              @PathVariable Long commentId) {
        Comment findComment = commentService.find(commentId);
        Member loginMember = memberService.findBySessionId(sessionId);
        loginMember.authorization(Objects.equals(findComment.getWriter().getId(), loginMember.getId()) ? USER : ADMIN);
        validate(postId, findComment);
        commentService.remove(commentId);
    }

    private void validate(Long postId, Comment comment) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new ApiException(ExceptionEnum.NO_DATA);
        }
    }
}
