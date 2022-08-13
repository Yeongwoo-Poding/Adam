package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Member;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.CommentRepository;
import project.adam.repository.MemberRepository;
import project.adam.service.dto.member.MemberJoinRequest;

import java.util.UUID;

import static project.adam.exception.ExceptionEnum.AUTHENTICATION_FAILED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostService postService;

    @Transactional
    public UUID join(MemberJoinRequest memberDto) {
        Member savedMember = memberRepository.save(new Member(
                UUID.fromString(memberDto.getId()),
                memberDto.getName(),
                memberDto.getPrivilege()
        ));

        return savedMember.getSessionId();
    }

    public Member find(UUID id) {
        return memberRepository.findById(id).orElseThrow();
    }

    public Member findBySessionId(UUID sessionId) {
        return memberRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ApiException(AUTHENTICATION_FAILED));
    }

    @Transactional
    public UUID login(UUID memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow();
        return findMember.login();
    }

    @Transactional
    public void withdraw(UUID id) {
        Member deleteMember = memberRepository.findBySessionId(id).orElseThrow();
        removeCommits(deleteMember);
        removePosts(deleteMember);
        removeMember(deleteMember);
    }

    private void removeCommits(Member member) {
        commentRepository.deleteAll(commentRepository.findAllByWriter(member));
    }
    private void removePosts(Member member) {
        member.getPosts().forEach(post -> postService.remove(post.getId()));
    }
    private void removeMember(Member member) {
        memberRepository.delete(member);
    }
}
