package project.adam.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.entity.*;
import project.adam.repository.CommentRepository;
import project.adam.repository.MemberRepository;
import project.adam.repository.PostRepository;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostUpdateRequest;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final JPAQueryFactory queryFactory;

    @Transactional
    public Long create(UUID sessionId, PostCreateRequest postDto) {
        Post savedPost = postRepository.save(new Post(
                memberRepository.findById(sessionId).orElseThrow(),
                Board.valueOf(postDto.getBoardName()),
                postDto.getTitle(),
                postDto.getBody()
        ));

        return savedPost.getId();
    }

    @Transactional
    public void update(Long postId, PostUpdateRequest postDto) {
        Post findPost = postRepository.findById(postId).orElseThrow();
        findPost.update(postDto.getTitle(), postDto.getBody());
    }

    @Transactional
    public void remove(Long postId) {
        List<Comment> commits = commentRepository.findAllByPost(postRepository.findById(postId).orElseThrow());
        commentRepository.deleteAll(commits);
        postRepository.delete(postRepository.findById(postId).orElseThrow());
    }

    public Post find(Long postId) {
        return postRepository.findById(postId).orElseThrow();
    }

    public Slice<Post> findAll(PostFindCondition condition, Pageable pageable) {
        return postRepository.findAll(condition, pageable);
    }
}
