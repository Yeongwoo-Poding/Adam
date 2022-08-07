package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Member;
import project.adam.repository.CommentRepository;
import project.adam.repository.MemberRepository;
import project.adam.service.dto.member.MemberJoinRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostService postService;

    @Transactional
    public Long join(MemberJoinRequest memberDto) {
        Member savedMember = memberRepository.save(new Member(
                memberDto.getId(),
                memberDto.getNickname(),
                memberDto.getPrivilege()
        ));
        return savedMember.getId();
    }

    public Member find(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow();
    }

    public Member find(String uuid) {
        return memberRepository.findByUuid(uuid).orElseThrow();
    }

    @Transactional
    public void withdraw(Long memberId) {
        removeCommits(memberId);
        removePosts(memberId);
        removeMember(memberId);
    }

    @Transactional
    public void withdraw(String uuid) {
        removeCommits(uuid);
        removePosts(uuid);
        removeMember(uuid);
    }

    private void removeCommits(Long memberId) {
        commentRepository.deleteAll(commentRepository.findAllByWriter(memberRepository.findById(memberId).orElseThrow()));
    }
    private void removePosts(Long memberId) {
        memberRepository.findById(memberId).orElseThrow()
                .getPosts()
                .forEach(post -> postService.remove(post.getId()));
    }
    private void removeMember(Long memberId) {
        memberRepository.delete(memberRepository.findById(memberId).orElseThrow());
    }

    private void removeCommits(String uuid) {
        commentRepository.deleteAll(commentRepository.findAllByWriter(memberRepository.findByUuid(uuid).orElseThrow()));
    }
    private void removePosts(String uuid) {
        memberRepository.findByUuid(uuid).orElseThrow()
                .getPosts()
                .forEach(post -> postService.remove(post.getId()));
    }
    private void removeMember(String uuid) {
        memberRepository.delete(memberRepository.findByUuid(uuid).orElseThrow());
    }
}
