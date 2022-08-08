package project.adam.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.entity.Post;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostUpdateRequest;

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
    void post_create() {
        //given
        String memberId = memberService.join(new MemberJoinRequest("id", "nickname"));
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                "FREE",
                "title",
                "body");
        Long savedId = postService.create(memberService.find(memberId).getId(), postCreateRequest);

        //when
        Post post = postService.find(savedId);

        //then
        assertThat(post.getWriter().getId()).isEqualTo(memberId);
        assertThat(post.getBoard().name()).isEqualTo(postCreateRequest.getBoardName().toString());
        assertThat(post.getTitle()).isEqualTo(postCreateRequest.getTitle());
        assertThat(post.getBody()).isEqualTo(postCreateRequest.getBody());
    }

    @Test
    void post_create_no_member() {
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                "FREE",
                "title",
                "body");

        assertThatThrownBy(() -> postService.create("NO_MEMBER", postCreateRequest))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void post_create_no_board() {
        String memberId = memberService.join(new MemberJoinRequest("id", "nickname"));
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                "NOBOARD",
                "title",
                "body");

        assertThatThrownBy(() -> postService.create(memberService.find(memberId).getId(), postCreateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void post_update() {
        //given
        String memberId = memberService.join(new MemberJoinRequest("id", "nickname"));
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                "FREE",
                "title",
                "body");
        Long savedId = postService.create(memberService.find(memberId).getId(), postCreateRequest);

        //when
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest("updatedTitle", "updated body");
        postService.update(savedId, postUpdateRequest);

        Post findPost = postService.find(savedId);

        //then
        assertThat(findPost.getTitle()).isEqualTo("updatedTitle");
        assertThat(findPost.getBody()).isEqualTo("updated body");
    }

    @Test
    void post_delete() {
        //given
        String memberId = memberService.join(new MemberJoinRequest("id", "nickname"));
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                "FREE",
                "title",
                "body");
        Long savedId = postService.create(memberService.find(memberId).getId(), postCreateRequest);
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
    void post_delete_remove_comments() {
        //given
        String postWriterId = memberService.join(new MemberJoinRequest("id", "member1"));
        Long post1Id = postService.create(memberService.find(postWriterId).getId(), new PostCreateRequest("FREE", "post1", "post 1"));
        Long post2Id = postService.create(memberService.find(postWriterId).getId(), new PostCreateRequest("FREE", "post2", "post 2"));

        List<Long> post1CommitId = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Long commentId = commentService.create(
                    memberService.find(postWriterId).getId(),
                    i % 2 == 0 ? post1Id : post2Id,
                    new CommentCreateRequest("comment " + i)
            );
            if (i % 2 == 0) {
                post1CommitId.add(commentId);
            }
        }

        //when
        postService.remove(post1Id);

        //then
        for (Long commitId : post1CommitId) {
            assertThatThrownBy(() -> commentService.find(commitId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Test
    void post_find_all() {
        //given
        String member1Id = memberService.join(new MemberJoinRequest("id1", "member1"));
        String member2Id = memberService.join(new MemberJoinRequest("id2", "member2"));

        for (int i = 0; i < 100; i++) {
            PostCreateRequest postCreateRequest = new PostCreateRequest(
                    "FREE",
                    "post" + i,
                    "post body " + i
            );

            postService.create((i % 2 == 0) ? "id1" : "id2", postCreateRequest);
        }

        PageRequest allPages = PageRequest.of(0, 100);

        //when
        Slice<Post> findPosts = postService.findAll(new PostFindCondition(), allPages);

        //then
        assertThat(findPosts.getSize()).isEqualTo(100);
    }

    @Test
    void post_find_all_by_writer() {
        //given
        String member1Id = memberService.join(new MemberJoinRequest("id1", "member1"));
        String member2Id = memberService.join(new MemberJoinRequest("id2", "member2"));

        for (int i = 0; i < 100; i++) {
            PostCreateRequest postCreateRequest = new PostCreateRequest(
                    "FREE",
                    "post" + i,
                    "post body " + i
            );

            postService.create((i % 2 == 0) ? memberService.find(member1Id).getId() : memberService.find(member2Id).getId(), postCreateRequest);
        }

        PageRequest allPages = PageRequest.of(0, 100);

        //when
        Slice<Post> member1Post = postService.findAll(new PostFindCondition(null, member1Id, null), allPages);
        Slice<Post> member2Post = postService.findAll(new PostFindCondition(null, member2Id, null), allPages);

        //then
        assertThat(member1Post.getContent().size()).isEqualTo(50);
        assertThat(member2Post.getContent().size()).isEqualTo(50);

        HashSet<Post> member1PostSet = new HashSet<>(member1Post.getContent());
        HashSet<Post> member2PostSet = new HashSet<>(member2Post.getContent());
        member1PostSet.retainAll(member2PostSet);
        assertThat(member1PostSet).isEmpty();
    }
}