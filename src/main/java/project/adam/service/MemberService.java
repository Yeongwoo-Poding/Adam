package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Member;
import project.adam.repository.CommentRepository;
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
    private final CommentRepository commentRepository;
    private final PostService postService;

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
        memberRepository.findById(memberId).orElseThrow().getPosts()
                        .forEach(post -> postService.remove(post.getId()));
        commentRepository.deleteAll(commentRepository.findAllByWriter(memberRepository.findById(memberId).orElseThrow()));
        memberRepository.delete(memberRepository.findById(memberId).orElseThrow());
    }

    public MemberFindResponse find(Long memberId) {
        return new MemberFindResponse(memberRepository.findById(memberId).orElseThrow());
    }
}
