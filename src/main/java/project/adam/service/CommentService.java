package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Comment;
import project.adam.repository.CommentRepository;
import project.adam.repository.MemberRepository;
import project.adam.repository.PostRepository;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentUpdateRequest;

import java.util.List;

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
                memberRepository.findById(commentDto.getWriterId()).orElseThrow(),
                postRepository.findById(postId).orElseThrow(),
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

    public Comment find(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow();
    }

    public List<Comment> findByPost(Long postId) {
        return commentRepository.findAllByPost(postRepository.findById(postId).orElseThrow());
    }
}
