package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Board;
import project.adam.entity.Comment;
import project.adam.entity.Post;
import project.adam.repository.CommentRepository;
import project.adam.repository.MemberRepository;
import project.adam.repository.PostRepository;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostFindResponse;
import project.adam.service.dto.post.PostUpdateRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long create(PostCreateRequest postDto) {
        Post savedPost = postRepository.save(new Post(
                memberRepository.findById(postDto.getMemberId()).orElseThrow(),
                Board.valueOf(postDto.getBoardName()),
                postDto.getTitle(),
                postDto.getBody())
        );

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
//        commentRepository.deleteAll(commits);
        for (Comment commit : commits) {
            commentRepository.delete(commit);
        }
        postRepository.delete(postRepository.findById(postId).orElseThrow());
    }

    public PostFindResponse find(Long postId) {
        return new PostFindResponse(postRepository.findById(postId).orElseThrow());
    }

    public List<PostFindResponse> findAll() {
        return postRepository.findAll().stream()
                .map(PostFindResponse::new)
                .collect(Collectors.toList());
    }

    public List<PostFindResponse> findAllByWriter(Long memberId) {
        return postRepository.findAllByWriter(memberRepository.findById(memberId).orElseThrow()).stream()
                .map(PostFindResponse::new)
                .collect(Collectors.toList());
    }
}
