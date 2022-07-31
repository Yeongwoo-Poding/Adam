package project.adam.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.adam.service.dto.member.MemberJoinRequest;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;

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