package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Member;
import project.adam.repository.CommentRepository;
import project.adam.service.dto.member.MemberFindResponse;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.repository.MemberRepository;

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
                memberDto.getNickname()
        ));

        return savedMember.getId();
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
        System.out.println("MemberService.removeCommits");
        commentRepository.deleteAll(commentRepository.findAllByWriter(memberRepository.findById(memberId).orElseThrow()));
    }
    private void removePosts(Long memberId) {
        System.out.println("MemberService.removePosts");
        memberRepository.findById(memberId).orElseThrow().getPosts()
                .forEach(post -> postService.remove(post.getId()));
    }
    private void removeMember(Long memberId) {
        System.out.println("MemberService.removeMember");
        memberRepository.delete(memberRepository.findById(memberId).orElseThrow());
    }

    private void removeCommits(String uuid) {
        System.out.println("MemberService.removeCommits");
        commentRepository.deleteAll(commentRepository.findAllByWriter(memberRepository.findByUuid(uuid).orElseThrow()));
    }
    private void removePosts(String uuid) {
        System.out.println("MemberService.removePosts");
        memberRepository.findByUuid(uuid).orElseThrow().getPosts()
                .forEach(post -> postService.remove(post.getId()));
    }
    private void removeMember(String uuid) {
        System.out.println("MemberService.removeMember");
        memberRepository.delete(memberRepository.findByUuid(uuid).orElseThrow());
    }

    public MemberFindResponse find(Long memberId) {
        return new MemberFindResponse(memberRepository.findById(memberId).orElseThrow());
    }

    public MemberFindResponse find(String uuid) {
        return new MemberFindResponse(memberRepository.findByUuid(uuid).orElseThrow());
    }
}
