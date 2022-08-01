package project.adam.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentFindResponse;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostFindResponse;
import project.adam.service.dto.post.PostUpdateRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
        Long memberId = memberService.join(new MemberJoinRequest("uuid", "nickname"));
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                memberService.find(memberId).getUuid(),
                "FREE",
                "title",
                "body");
        Long savedId = postService.create(postCreateRequest);

        //when
        PostFindResponse postFindResponse = postService.find(savedId);

        //then
        assertThat(postFindResponse.getWriterId()).isEqualTo(memberId);
        assertThat(postFindResponse.getBoardName()).isEqualTo(postCreateRequest.getBoardName().toString());
        assertThat(postFindResponse.getTitle()).isEqualTo(postCreateRequest.getTitle());
        assertThat(postFindResponse.getBody()).isEqualTo(postCreateRequest.getBody());
    }

    @Test
    void post_create_no_member() {
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                "NO_MEMBER",
                "FREE",
                "title",
                "body");

        assertThatThrownBy(() -> postService.create(postCreateRequest))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void post_create_no_board() {
        Long memberId = memberService.join(new MemberJoinRequest("uuid", "nickname"));
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                memberService.find(memberId).getUuid(),
                "NOBOARD",
                "title",
                "body");

        assertThatThrownBy(() -> postService.create(postCreateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void post_update() {
        //given
        Long memberId = memberService.join(new MemberJoinRequest("uuid", "nickname"));
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                memberService.find(memberId).getUuid(),
                "FREE",
                "title",
                "body");
        Long savedId = postService.create(postCreateRequest);

        //when
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest("updatedTitle", "updated body");
        postService.update(savedId, postUpdateRequest);

        PostFindResponse findPostResponse = postService.find(savedId);

        //then
        assertThat(findPostResponse.getTitle()).isEqualTo("updatedTitle");
        assertThat(findPostResponse.getBody()).isEqualTo("updated body");
    }

    @Test
    void post_delete() {
        //given
        Long memberId = memberService.join(new MemberJoinRequest("uuid", "nickname"));
        PostCreateRequest postCreateRequest = new PostCreateRequest(
                memberService.find(memberId).getUuid(),
                "FREE",
                "title",
                "body");
        Long savedId = postService.create(postCreateRequest);
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
        Long postWriterId = memberService.join(new MemberJoinRequest("uuid", "member1"));
        Long post1Id = postService.create(new PostCreateRequest(memberService.find(postWriterId).getUuid(), "FREE", "post1", "post 1"));
        Long post2Id = postService.create(new PostCreateRequest(memberService.find(postWriterId).getUuid(), "FREE", "post2", "post 2"));

        List<Long> post1CommitId = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Long commentId = commentService.create(i % 2 == 0 ? post1Id : post2Id, new CommentCreateRequest(memberService.find(postWriterId).getUuid(), "comment " + i));
            if (i % 2 == 0) {
                post1CommitId.add(commentId);
                System.out.println("commentId = " + commentId);
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
        Long member1Id = memberService.join(new MemberJoinRequest("uuid1", "member1"));
        Long member2Id = memberService.join(new MemberJoinRequest("uuid2", "member2"));

        for (int i = 0; i < 100; i++) {
            PostCreateRequest postCreateRequest = new PostCreateRequest(
                    (i % 2 == 0) ? "uuid1" : "uuid2",
                    "FREE",
                    "post" + i,
                    "post body " + i
            );

            postService.create(postCreateRequest);
        }

        //when
        List<PostFindResponse> posts = postService.findAll();

        //then
        assertThat(posts.size()).isEqualTo(100);
    }

    @Test
    void post_find_all_by_writer() {
        //given
        Long member1Id = memberService.join(new MemberJoinRequest("uuid1", "member1"));
        Long member2Id = memberService.join(new MemberJoinRequest("uuid2", "member2"));

        for (int i = 0; i < 100; i++) {
            PostCreateRequest postCreateRequest = new PostCreateRequest(
                    (i % 2 == 0) ? memberService.find(member1Id).getUuid() : memberService.find(member2Id).getUuid(),
                    "FREE",
                    "post" + i,
                    "post body " + i
            );

            postService.create(postCreateRequest);
        }

        //when
        List<PostFindResponse> member1Post = postService.findAllByWriter(member1Id);
        List<PostFindResponse> member2Post = postService.findAllByWriter(member2Id);

        //then
        assertThat(member1Post.size()).isEqualTo(50);
        assertThat(member2Post.size()).isEqualTo(50);

        member1Post.retainAll(member2Post);
        assertThat(member1Post).isEmpty();
    }
}