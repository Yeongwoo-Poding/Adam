package project.adam.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.adam.exception.ApiException;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentFindResponse;
import project.adam.service.dto.comment.CommentUpdateRequest;
import project.adam.service.dto.member.MemberFindResponse;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostFindResponse;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired CommentService commentService;

    @Test
    void comment_create() {
        //given
        Long postWriterId = memberService.join(new MemberJoinRequest("uuid1", "member1"));
        Long commentWriterId = memberService.join(new MemberJoinRequest("uuid2", "member2"));
        Long postId = postService.create(new PostCreateRequest(memberService.find(postWriterId).getUuid(), "FREE", "title", "new post"));

        //when
        Long commentId = commentService.create(postId, new CommentCreateRequest(memberService.find(commentWriterId).getUuid(), "new comment"));
        CommentFindResponse commentFindResponse = commentService.find(commentId);

        //then
        assertThat(commentFindResponse.getWriterId()).isEqualTo(commentWriterId);
        assertThat(postService.find(commentFindResponse.getPostId()).getWriterId()).isEqualTo(postWriterId);
        assertThat(commentFindResponse.getPostId()).isEqualTo(postId);
    }

    @Test
    void comment_create_no_post() {
        Long commentWriterId = memberService.join(new MemberJoinRequest("uuid", "member2"));
        assertThatThrownBy(() -> commentService.create(0L, new CommentCreateRequest(memberService.find(commentWriterId).getUuid(), "body")))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void comment_create_no_member() {
        Long postWriterId = memberService.join(new MemberJoinRequest("uuid", "member1"));
        Long postId = postService.create(new PostCreateRequest(memberService.find(postWriterId).getUuid(), "FREE", "title", "new post"));
        assertThatThrownBy(() -> commentService.create(postId, new CommentCreateRequest("NO", "body")))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void comment_update() {
        //given
        Long postWriterId = memberService.join(new MemberJoinRequest("uuid1", "member1"));
        Long commentWriterId = memberService.join(new MemberJoinRequest("uuid2", "member2"));
        Long postId = postService.create(new PostCreateRequest(memberService.find(postWriterId).getUuid(), "FREE", "title", "new post"));
        Long commentId = commentService.create(postId, new CommentCreateRequest(memberService.find(commentWriterId).getUuid(), "new comment"));

        //when
        commentService.update(commentId, new CommentUpdateRequest("updated comment"));

        //then
        assertThat(commentService.find(commentId).getBody()).isEqualTo("updated comment");
    }

    @Test
    void comment_remove() {
        //given
        Long postWriterId = memberService.join(new MemberJoinRequest("uuid1", "member1"));
        Long commentWriterId = memberService.join(new MemberJoinRequest("uuid2", "member2"));
        Long postId = postService.create(new PostCreateRequest(memberService.find(postWriterId).getUuid(), "FREE", "title", "new post"));
        Long commentId = commentService.create(postId, new CommentCreateRequest(memberService.find(commentWriterId).getUuid(), "new comment"));

        //when
        commentService.remove(commentId);

        //then
        assertThatThrownBy(() -> commentService.find(commentId))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void comment_find_by_post() {
        //given
        Long postWriterId = memberService.join(new MemberJoinRequest("uuid", "member1"));
        Long post1Id = postService.create(new PostCreateRequest(memberService.find(postWriterId).getUuid(), "FREE", "post1", "post 1"));
        Long post2Id = postService.create(new PostCreateRequest(memberService.find(postWriterId).getUuid(), "FREE", "post2", "post 2"));

        //when
        for (int i = 0; i < 100; i++) {
            commentService.create((i % 2 == 0) ? post1Id : post2Id, new CommentCreateRequest(memberService.find(postWriterId).getUuid(), "comment " + i));
        }

        //then
        assertThat(commentService.findByPost(post1Id).size()).isEqualTo(50);
        assertThat(commentService.findByPost(post2Id).size()).isEqualTo(50);
    }
}