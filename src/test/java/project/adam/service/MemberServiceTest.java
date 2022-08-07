package project.adam.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import project.adam.service.dto.post.PostFindCondition;
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
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest("id", "nickname");

        //when
        String savedId = memberService.join(memberJoinRequest);

        //then
        assertThat(memberService.find(savedId).getId()).isEqualTo(memberJoinRequest.getId());
        assertThat(memberService.find(savedId).getNickname()).isEqualTo(memberJoinRequest.getNickname());
    }

    @Test
    void member_withdraw() {
        //given
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest("id", "nickname");
        String savedId = memberService.join(memberJoinRequest);

        //when
        memberService.withdraw(savedId);

        //then
        assertThatThrownBy(() -> memberService.find(savedId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void member_withdraw_remove_post() {
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
        memberService.withdraw(member1Id);

        //then
        assertThat(postService.findAll(new PostFindCondition(), allPages).getContent().size()).isEqualTo(50);
    }

    @Test
    void member_withdraw_remove_comment() {
        //given
        String member1Id = memberService.join(new MemberJoinRequest("id1", "member1"));
        String member2Id = memberService.join(new MemberJoinRequest("id2", "member2"));
        Long post1Id = postService.create(memberService.find(member1Id).getId(),
                new PostCreateRequest("FREE", "post1", "post 1"));
        Long post2Id = postService.create(memberService.find(member2Id).getId(),
                new PostCreateRequest("FREE", "post2", "post 2"));

        for (int i = 0; i < 100; i++) {
            commentService.create((i % 2 == 0) ? memberService.find(member1Id).getId() : memberService.find(member2Id).getId(),
                    (i % 2 == 0) ? post2Id : post1Id,
                    new CommentCreateRequest("comment " + i)
            );
        }

        PageRequest allPages = PageRequest.of(0, 100);

        //when
        memberService.withdraw(member1Id);

        //then
        assertThat(commentService.findByPost(post2Id, allPages)).isEmpty();
    }

    @Test
    void member_withdraw_not_found() {
        assertThatThrownBy(() -> memberService.withdraw("FALSE"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void member_not_found() {
        assertThatThrownBy(() -> memberService.find("FALSE"))
                .isInstanceOf(NoSuchElementException.class);
    }
}