package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Member;
import project.adam.repository.PostRepository;
import project.adam.service.dto.member.MemberFindResponse;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long join(MemberJoinRequest memberDto) {
        Member savedMember = memberRepository.save(new Member(
                memberDto.getUuid(),
                memberDto.getNickname()
        ));

        return savedMember.getId();
    }

    @Transactional
    public void withdraw(Long memberId) {
        postRepository.deleteAll(memberRepository.findById(memberId).orElseThrow().getPosts());
        memberRepository.delete(memberRepository.findById(memberId).orElseThrow());
    }

    public MemberFindResponse find(Long memberId) {
        return new MemberFindResponse(memberRepository.findById(memberId).orElseThrow());
    }
}
