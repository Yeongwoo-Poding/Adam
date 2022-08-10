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

import static project.adam.exception.ExceptionEnum.AUTHENTICATION_FAILED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostService postService;

    @Transactional
    public String join(MemberJoinRequest memberDto) {
        Member savedMember = memberRepository.save(new Member(
                memberDto.getId(),
                memberDto.getName(),
                memberDto.getPrivilege()
        ));
        return savedMember.getId();
    }

    public Member find(String id) {
        return memberRepository.findById(id).orElseThrow();
    }

    public Member findBySessionId(String sessionId) {
        return memberRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ApiException(AUTHENTICATION_FAILED));
    }

    @Transactional
    public String login(String memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow();
        return findMember.login();
    }

    @Transactional
    public void withdraw(String id) {
        removeCommits(id);
        removePosts(id);
        removeMember(id);
    }

    private void removeCommits(String id) {
        commentRepository.deleteAll(commentRepository.findAllByWriter(memberRepository.findById(id).orElseThrow()));
    }
    private void removePosts(String id) {
        memberRepository.findById(id).orElseThrow()
                .getPosts()
                .forEach(post -> postService.remove(post.getId()));
    }
    private void removeMember(String id) {
        memberRepository.delete(memberRepository.findById(id).orElseThrow());
    }
}
