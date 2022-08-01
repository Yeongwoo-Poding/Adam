package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Member;
import project.adam.exception.ApiException;
import project.adam.repository.CommentRepository;
import project.adam.service.dto.member.MemberFindResponse;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.repository.MemberRepository;

import static project.adam.exception.ExceptionEnum.NO_RESULT_EXCEPTION;

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
        commentRepository.deleteAll(commentRepository.findAllByWriter(memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(NO_RESULT_EXCEPTION))));
    }
    private void removePosts(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(NO_RESULT_EXCEPTION))
                .getPosts()
                .forEach(post -> postService.remove(post.getId()));
    }
    private void removeMember(Long memberId) {
        memberRepository.delete(memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(NO_RESULT_EXCEPTION)));
    }

    private void removeCommits(String uuid) {
        commentRepository.deleteAll(commentRepository.findAllByWriter(memberRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(NO_RESULT_EXCEPTION))));
    }
    private void removePosts(String uuid) {
        memberRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(NO_RESULT_EXCEPTION))
                .getPosts()
                .forEach(post -> postService.remove(post.getId()));
    }
    private void removeMember(String uuid) {
        memberRepository.delete(memberRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(NO_RESULT_EXCEPTION)));
    }

    public MemberFindResponse find(Long memberId) {
        return new MemberFindResponse(memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(NO_RESULT_EXCEPTION)));
    }

    public MemberFindResponse find(String uuid) {
        return new MemberFindResponse(memberRepository.findByUuid(uuid)
                .orElseThrow(() -> new ApiException(NO_RESULT_EXCEPTION)));
    }
}
