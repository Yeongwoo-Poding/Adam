package project.adam.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.adam.entity.Post;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.service.dto.post.PostUpdateRequest;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired CommentService commentService;

    @Test
    void post_create() throws IOException {
        //given
        UUID memberId = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(memberId.toString(), "nickname"));
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                "FREE",
                "title",
                "body");
        Long savedId = postService.create(memberService.find(memberId).getId(), postCreateRequest, new MultipartFile[]{}).getId();

        //when
        Post post = postService.find(savedId);

        //then
        assertThat(post.getWriter().getId()).isEqualTo(memberId);
        assertThat(post.getBoard().name()).isEqualTo(postCreateRequest.getBoard());
        assertThat(post.getTitle()).isEqualTo(postCreateRequest.getTitle());
        assertThat(post.getBody()).isEqualTo(postCreateRequest.getBody());
    }

    @Test
    void post_create_no_member() {
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                "FREE",
                "title",
                "body");

        assertThatThrownBy(() -> postService.create(UUID.randomUUID(), postCreateRequest, new MultipartFile[]{}))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void post_create_no_board() {
        UUID memberId = UUID.randomUUID();
        UUID token = memberService.join(new MemberJoinRequest(memberId.toString(), "nickname"));
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                "NOBOARD",
                "title",
                "body");

        assertThatThrownBy(() -> postService.create(memberService.find(memberId).getId(), postCreateRequest, new MultipartFile[]{}))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void post_update() throws IOException {
        //given
        UUID memberId = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(memberId.toString(), "nickname"));
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                "FREE",
                "title",
                "body");
        Long savedId = postService.create(memberService.find(memberId).getId(), postCreateRequest, new MultipartFile[]{}).getId();

        //when
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest("updatedTitle", "updated body");
        postService.update(savedId, postUpdateRequest, new MultipartFile[]{});

        Post findPost = postService.find(savedId);

        //then
        assertThat(findPost.getTitle()).isEqualTo("updatedTitle");
        assertThat(findPost.getBody()).isEqualTo("updated body");
    }

    @Test
    void post_delete() throws IOException {
        //given
        UUID memberId = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(memberId.toString(), "nickname"));
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                "FREE",
                "title",
                "body");
        Long savedId = postService.create(memberService.find(memberId).getId(), postCreateRequest, new MultipartFile[]{}).getId();
        //when
        postService.remove(savedId);

        //then
        assertThatThrownBy(() -> postService.find(savedId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void post_delete_no_post() {
        assertThatThrownBy(() -> postService.remove(0L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void post_delete_remove_comments() throws IOException {
        //given
        UUID postWriterId = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(postWriterId.toString(), "member1"));
        Long post1Id = postService.create(memberService.find(postWriterId).getId(), new PostCreateRequest("FREE", "post1", "post 1"), new MultipartFile[]{}).getId();
        Long post2Id = postService.create(memberService.find(postWriterId).getId(), new PostCreateRequest("FREE", "post2", "post 2"), new MultipartFile[]{}).getId();

        List<Long> post1CommitId = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Long commentId = commentService.create(
                    memberService.find(postWriterId).getId(),
                    i % 2 == 0 ? post1Id : post2Id,
                    null,
                    new CommentCreateRequest("comment " + i)
            );
            if (i % 2 == 0) {
                post1CommitId.add(commentId);
            }
        }

        //when
        postService.remove(post1Id);

        //then
        for (Long commentId : post1CommitId) {
            assertThatThrownBy(() -> commentService.find(commentId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Test
    void post_find_all() throws IOException {
        //given
        UUID member1Id = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(member1Id.toString(), "member1"));
        UUID member2Id = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(member2Id.toString(), "member2"));

        for (int i = 0; i < 10; i++) {
            PostCreateRequest postCreateRequest = new PostCreateRequest(
                    "FREE",
                    "post" + i,
                    "post body " + i
            );

            postService.create((i % 2 == 0) ? member1Id : member2Id, postCreateRequest, new MultipartFile[]{});
        }

        PageRequest allPages = PageRequest.of(0, 10);

        //when
        Slice<Post> findPosts = postService.findAll(new PostFindCondition(), allPages);

        //then
        assertThat(findPosts.getSize()).isEqualTo(10);
    }

    @Test
    void post_find_all_by_writer() throws IOException {
        //given
        UUID member1Id = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(member1Id.toString(), "member1"));
        UUID member2Id = UUID.randomUUID();
        memberService.join(new MemberJoinRequest(member2Id.toString(), "member2"));

        for (int i = 0; i < 10; i++) {
            PostCreateRequest postCreateRequest = new PostCreateRequest(
                    "FREE",
                    "post" + i,
                    "post body " + i
            );

            postService.create((i % 2 == 0) ? memberService.find(member1Id).getId() : memberService.find(member2Id).getId(), postCreateRequest, new MultipartFile[]{});
        }

        PageRequest allPages = PageRequest.of(0, 10);

        //when
        Slice<Post> member1Post = postService.findAll(new PostFindCondition(member1Id, null), allPages);
        Slice<Post> member2Post = postService.findAll(new PostFindCondition(member2Id, null), allPages);

        //then
        assertThat(member1Post.getContent().size()).isEqualTo(5);
        assertThat(member2Post.getContent().size()).isEqualTo(5);

        HashSet<Post> member1PostSet = new HashSet<>(member1Post.getContent());
        HashSet<Post> member2PostSet = new HashSet<>(member2Post.getContent());
        member1PostSet.retainAll(member2PostSet);
        assertThat(member1PostSet).isEmpty();
    }
}