package project.adam.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Comment;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentUpdateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.post.PostCreateRequest;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired CommentService commentService;

    @Test
    void comment_create() {
        //given
        String postWriterId = memberService.join(new MemberJoinRequest("id1", "member1"));
        String commentWriterId = memberService.join(new MemberJoinRequest("id2", "member2"));
        Long postId = postService.create(new PostCreateRequest(memberService.find(postWriterId).getId(), "FREE", "title", "new post"));

        //when
        Long commentId = commentService.create(postId, new CommentCreateRequest(memberService.find(commentWriterId).getId(), "new comment"));
        Comment comment = commentService.find(commentId);

        //then
        assertThat(comment.getWriter().getId()).isEqualTo(commentWriterId);
        assertThat(comment.getPost().getWriter().getId()).isEqualTo(postWriterId);
        assertThat(comment.getPost().getId()).isEqualTo(postId);
    }

    @Test
    void comment_create_no_post() {
        String commentWriterId = memberService.join(new MemberJoinRequest("id", "member2"));
        assertThatThrownBy(() -> commentService.create(0L, new CommentCreateRequest(memberService.find(commentWriterId).getId(), "body")))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void comment_create_no_member() {
        String postWriterId = memberService.join(new MemberJoinRequest("id", "member1"));
        Long postId = postService.create(new PostCreateRequest(memberService.find(postWriterId).getId(), "FREE", "title", "new post"));
        assertThatThrownBy(() -> commentService.create(postId, new CommentCreateRequest("NO", "body")))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void comment_update() {
        //given
        String postWriterId = memberService.join(new MemberJoinRequest("id1", "member1"));
        String commentWriterId = memberService.join(new MemberJoinRequest("id2", "member2"));
        Long postId = postService.create(new PostCreateRequest(memberService.find(postWriterId).getId(), "FREE", "title", "new post"));
        Long commentId = commentService.create(postId, new CommentCreateRequest(memberService.find(commentWriterId).getId(), "new comment"));

        //when
        commentService.update(commentId, new CommentUpdateRequest("updated comment"));

        //then
        assertThat(commentService.find(commentId).getBody()).isEqualTo("updated comment");
    }

    @Test
    void comment_remove() {
        //given
        String postWriterId = memberService.join(new MemberJoinRequest("id1", "member1"));
        String commentWriterId = memberService.join(new MemberJoinRequest("id2", "member2"));
        Long postId = postService.create(new PostCreateRequest(memberService.find(postWriterId).getId(), "FREE", "title", "new post"));
        Long commentId = commentService.create(postId, new CommentCreateRequest(memberService.find(commentWriterId).getId(), "new comment"));

        //when
        commentService.remove(commentId);

        //then
        assertThatThrownBy(() -> commentService.find(commentId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void comment_find_by_post() {
        //given
        String postWriterId = memberService.join(new MemberJoinRequest("id", "member1"));
        Long post1Id = postService.create(new PostCreateRequest(memberService.find(postWriterId).getId(), "FREE", "post1", "post 1"));
        Long post2Id = postService.create(new PostCreateRequest(memberService.find(postWriterId).getId(), "FREE", "post2", "post 2"));

        //when
        for (int i = 0; i < 100; i++) {
            commentService.create((i % 2 == 0) ? post1Id : post2Id, new CommentCreateRequest(memberService.find(postWriterId).getId(), "comment " + i));
        }

        //then
        assertThat(commentService.findByPost(post1Id).size()).isEqualTo(50);
        assertThat(commentService.findByPost(post2Id).size()).isEqualTo(50);
    }
}