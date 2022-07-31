package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Comment;
import project.adam.repository.CommentRepository;
import project.adam.repository.MemberRepository;
import project.adam.repository.PostRepository;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentFindResponse;
import project.adam.service.dto.comment.CommentUpdateRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long create(CommentCreateRequest commentDto) {
        Comment savedComment = commentRepository.save(new Comment(
                memberRepository.findById(commentDto.getWriterId()).orElseThrow(),
                postRepository.findById(commentDto.getPostId()).orElseThrow(),
                commentDto.getBody()
        ));

        return savedComment.getId();
    }

    @Transactional
    public void update(Long commentId, CommentUpdateRequest commentDto) {
        Comment findComment = commentRepository.findById(commentId).orElseThrow();
        findComment.update(commentDto.getBody());
    }

    @Transactional
    public void remove(Long commentId) {
        commentRepository.delete(commentRepository.findById(commentId).orElseThrow());
    }

    public CommentFindResponse find(Long commentId) {
        Comment findComment = commentRepository.findById(commentId).orElseThrow();
        return new CommentFindResponse(findComment);
    }

    public List<CommentFindResponse> findByPost(Long postId) {
        return commentRepository.findAllByPost(postRepository.findById(postId).orElseThrow()).stream()
                .map(CommentFindResponse::new)
                .collect(Collectors.toList());
    }
}
