package project.adam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.comment.Comment;
import project.adam.entity.member.Authority;
import project.adam.entity.member.Member;
import project.adam.entity.member.MemberSession;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.member.MemberRepository;
import project.adam.repository.post.PostRepository;
import project.adam.utils.image.ImageUtils;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Slf4j
@Profile({"dev", "local"})
@Component
@RequiredArgsConstructor
public class Init {

    private final Dev dev;

    @PostConstruct
    public void postConstruct() {
        dev.removeExistFiles();
        dev.createDummyData();
    }

    @Component
    @RequiredArgsConstructor
    private static class Dev {

        private final MemberRepository memberRepository;
        private final PostRepository postRepository;
        private final CommentRepository commentRepository;
        private final ImageUtils imageUtils;

        private static final long N = 5L;

        @Transactional
        public void removeExistFiles() {
            imageUtils.removeAll();
        }

        @Transactional
        public void createDummyData() {
            if (isDummyDataExist()) {
                return;
            }

            for (long i = 0L; i < N; i++) {
                memberRepository.save(new Member(UUID.randomUUID().toString(), "email" + (i + 1), "email" + (i + 1), MemberSession.MORNING));
            }

            for (long i = 0L; i < (N * N); i++) {
                Member writer = memberRepository.findByEmail("email" + (i % N + 1)).orElseThrow();
                postRepository.save(new Post(writer, Board.FREE, writer.getEmail(), "writer: " + writer.getName()));
                postRepository.save(new Post(writer, Board.QUESTION, writer.getEmail(), "writer: " + writer.getName()));
            }

            for (long i = 0L; i < (N * N * N); i++) {
                Member writer = memberRepository.findByEmail("email" + (i % N + 1)).orElseThrow();
                Post post = postRepository.findById(i % (N * N) + 1).orElseThrow();
                commentRepository.save(new Comment(null, writer, post, "writer: " + writer.getName() + " post: " + post.getId()));
            }

            for (long i = 0L; i < (N * N * N * N); i++) {
                Member writer = memberRepository.findByEmail("email" + (i % N + 1)).orElseThrow();
                Post post = postRepository.findById(i % (N * N) + 1).orElseThrow();
                Comment comment = commentRepository.findById(i % (N * N * N) + 1).orElseThrow();
                commentRepository.save(new Comment(comment, writer, post, "writer: " + writer.getName() + " post: " + post.getId()));
            }
        }

        private boolean isDummyDataExist() {
            return memberRepository.findAll().stream().anyMatch(member -> member.getAuthority().equals(Authority.ROLE_USER));
        }
    }
}
