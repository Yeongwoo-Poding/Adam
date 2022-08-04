package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import project.adam.entity.Comment;
import project.adam.exception.ApiException;
import project.adam.repository.CommentRepository;
import project.adam.repository.MemberRepository;
import project.adam.repository.PostRepository;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentFindResponse;
import project.adam.service.dto.comment.CommentUpdateRequest;
import java.util.List;
import java.util.stream.Collectors;
import static project.adam.exception.ExceptionEnum.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long create(Long postId, CommentCreateRequest commentDto) {
        Comment savedComment = commentRepository.save(new Comment(
                memberRepository.findByUuid(commentDto.getWriterId())
                        .orElseThrow(() -> new ApiException(NO_DATA)),
                postRepository.findById(postId)
                        .orElseThrow(() -> new ApiException(NO_DATA)),
                commentDto.getBody()
        ));

        return savedComment.getId();
    }

    @Transactional
    public void update(Long commentId, CommentUpdateRequest commentDto) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(NO_DATA));
        findComment.update(commentDto.getBody());
    }

    @Transactional
    public void remove(Long commentId) {
        commentRepository.delete(commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(NO_DATA)));
    }

    public CommentFindResponse find(Long commentId) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(NO_DATA));
        return new CommentFindResponse(findComment);
    }

    public List<CommentFindResponse> findByPost(Long postId) {
        return commentRepository.findAllByPost(postRepository.findById(postId)
                        .orElseThrow(() -> new ApiException(NO_DATA)))
                .stream()
                .map(CommentFindResponse::new)
                .collect(Collectors.toList());
    }
}
