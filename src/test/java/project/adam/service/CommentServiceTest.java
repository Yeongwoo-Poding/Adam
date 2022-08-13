package project.adam.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Comment;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentUpdateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.post.PostCreateRequest;

import java.util.NoSuchElementException;
import java.util.UUID;

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
        UUID postWriterId = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(postWriterId.toString(), "member1"));
        UUID commentWriterId = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(commentWriterId.toString(), "member2"));
        Long postId = postService.create(memberService.find(postWriterId).getId(), new PostCreateRequest("FREE", "title", "new post"));

        //when
        Long commentId = commentService.create(memberService.find(commentWriterId).getId(), postId, null, new CommentCreateRequest("new comment"));
        Comment comment = commentService.find(commentId);

        //then
        assertThat(comment.getWriter().getId()).isEqualTo(commentWriterId);
        assertThat(comment.getPost().getWriter().getId()).isEqualTo(postWriterId);
        assertThat(comment.getPost().getId()).isEqualTo(postId);
    }

    @Test
    void comment_create_no_post() {
        UUID commentWriterId = memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "member2"));
        assertThatThrownBy(() -> commentService.create(memberService.find(commentWriterId).getId(), 0L, null, new CommentCreateRequest("body")))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void comment_create_no_member() {
        UUID postWriterId = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(postWriterId.toString(), "member1"));
        Long postId = postService.create(memberService.find(postWriterId).getId(), new PostCreateRequest("FREE", "title", "new post"));
        assertThatThrownBy(() -> commentService.create(UUID.randomUUID(), postId, null, new CommentCreateRequest("body")))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void comment_update() {
        //given
        UUID postWriterId = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(postWriterId.toString(), "member1"));
        UUID commentWriterId = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(commentWriterId.toString(), "member2"));
        Long postId = postService.create(memberService.find(postWriterId).getId(), new PostCreateRequest("FREE", "title", "new post"));
        Long commentId = commentService.create(memberService.find(commentWriterId).getId(), postId, null, new CommentCreateRequest("new comment"));

        //when
        commentService.update(commentId, new CommentUpdateRequest("updated comment"));

        //then
        assertThat(commentService.find(commentId).getBody()).isEqualTo("updated comment");
    }

    @Test
    void comment_remove() {
        //given
        UUID postWriterId = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(postWriterId.toString(), "member1"));
        UUID commentWriterId = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(commentWriterId.toString(), "member2"));
        Long postId = postService.create(memberService.find(postWriterId).getId(), new PostCreateRequest("FREE", "title", "new post"));
        Long commentId = commentService.create(memberService.find(commentWriterId).getId(), postId, null, new CommentCreateRequest("new comment"));

        //when
        commentService.remove(commentId);

        //then
        assertThatThrownBy(() -> commentService.find(commentId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void comment_find_by_post() {
        //given
        UUID postWriterId = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(postWriterId.toString(), "member1"));
        Long post1Id = postService.create(postWriterId, new PostCreateRequest("FREE", "post1", "post 1"));
        Long post2Id = postService.create(postWriterId, new PostCreateRequest("FREE", "post2", "post 2"));

        PageRequest allPages = PageRequest.of(0, 100);

        //when
        for (int i = 0; i < 100; i++) {
            commentService.create(postWriterId, (i % 2 == 0) ? post1Id : post2Id, null, new CommentCreateRequest("comment " + i));
        }

        //then
        assertThat(commentService.findByPost(post1Id, allPages).getContent().size()).isEqualTo(50);
        assertThat(commentService.findByPost(post2Id, allPages).getContent().size()).isEqualTo(50);
    }
}