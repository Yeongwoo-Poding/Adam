package project.adam.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.post.PostCreateRequest;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired CommentService commentService;

    @Test
    void member_join() {
        //given
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest("uuid", "nickname");

        //when
        Long savedId = memberService.join(memberJoinRequest);

        //then
        assertThat(memberService.find(savedId).getUuid()).isEqualTo(memberJoinRequest.getUuid());
        assertThat(memberService.find(savedId).getNickname()).isEqualTo(memberJoinRequest.getNickname());
    }

    @Test
    void member_withdraw() {
        //given
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest("uuid", "nickname");
        Long savedId = memberService.join(memberJoinRequest);

        //when
        memberService.withdraw(savedId);

        //then
        assertThatThrownBy(() -> memberService.find(savedId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void member_withdraw_remove_post() {
        //given
        Long member1Id = memberService.join(new MemberJoinRequest("uuid1", "member1"));
        Long member2Id = memberService.join(new MemberJoinRequest("uuid2", "member2"));

        for (int i = 0; i < 100; i++) {
            PostCreateRequest postCreateRequest = new PostCreateRequest(
                    (i % 2 == 0) ? member1Id : member2Id,
                    "FREE",
                    "post" + i,
                    "post body " + i
            );

            postService.create(postCreateRequest);
        }

        //when
        memberService.withdraw(member1Id);

        //then
        assertThat(postService.findAll().size()).isEqualTo(50);
    }

    @Test
    void member_withdraw_remove_comment() {
        //given
        Long member1Id = memberService.join(new MemberJoinRequest("uuid1", "member1"));
        Long member2Id = memberService.join(new MemberJoinRequest("uuid2", "member2"));
        Long post1Id = postService.create(new PostCreateRequest(member1Id, "FREE", "post1", "post 1"));
        Long post2Id = postService.create(new PostCreateRequest(member2Id, "FREE", "post2", "post 2"));

        for (int i = 0; i < 100; i++) {
            commentService.create(new CommentCreateRequest(
                    (i % 2 == 0) ? member1Id : member2Id,
                    (i % 2 == 0) ? post2Id : post1Id,
                    "comment " + i
            ));
        }

        //when
        memberService.withdraw(member1Id);

        //then
        assertThat(commentService.findByPost(post2Id)).isEmpty();
    }

    @Test
    void member_withdraw_not_found() {
        assertThatThrownBy(() -> memberService.withdraw(0L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void member_not_found() {
        assertThatThrownBy(() -> memberService.find(0L))
                .isInstanceOf(NoSuchElementException.class);
    }
}