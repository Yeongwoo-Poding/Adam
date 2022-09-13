package project.adam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.adam.controller.dto.member.MemberLoginResponse;
import project.adam.controller.dto.member.MemberRefreshResponse;
import project.adam.entity.member.Member;
import project.adam.exception.ApiException;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.member.MemberRepository;
import project.adam.repository.reply.ReplyRepository;
import project.adam.security.SecurityUtil;
import project.adam.security.TokenProvider;
import project.adam.security.refreshtoken.RefreshToken;
import project.adam.security.refreshtoken.RefreshTokenRepository;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;
import project.adam.utils.ImageUtils;

import java.io.IOException;

import static project.adam.exception.ExceptionEnum.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final PostService postService;
    private final ImageUtils imageUtils;


    @Transactional
    public void join(MemberJoinRequest memberDto) {
        if (memberRepository.existsByEmail(memberDto.getEmail())) {
            throw new ApiException(UNIQUE_CONSTRAINT_VIOLATED);
        }

        memberRepository.save(new Member(
                passwordEncoder.encode(memberDto.getId()),
                memberDto.getEmail(),
                memberDto.getName(),
                memberDto.getAuthority()
        ));
    }

    @Transactional
    public MemberLoginResponse login(MemberLoginRequest memberDto) {
        UsernamePasswordAuthenticationToken authenticationToken = memberDto.toAuthentication();
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        MemberLoginResponse memberLoginResponse = tokenProvider.generateTokenDto(authenticate);
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authenticate.getName())
                .value(memberLoginResponse.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return memberLoginResponse;
    }

    @Transactional
    public MemberLoginResponse refreshToken(MemberRefreshResponse memberDto) {
        if (!tokenProvider.validateToken(memberDto.getRefreshToken())) {
            throw new ApiException(AUTHENTICATION_FAILED);
        }

        Authentication authentication = tokenProvider.getAuthentication(memberDto.getAccessToken());
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName()).orElseThrow(() -> new ApiException(AUTHENTICATION_FAILED));

        if (!refreshToken.getValue().equals(memberDto.getRefreshToken())) {
            throw new ApiException(AUTHENTICATION_FAILED);
        }

        MemberLoginResponse memberLoginResponse = tokenProvider.generateTokenDto(authentication);

        RefreshToken newRefreshToken = refreshToken.updateValue(memberLoginResponse.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return memberLoginResponse;
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow();
    }

    @Transactional
    public void withdraw(Member member) {
        removeReplies(member);
        removeComments(member);
        removePosts(member);
        removeMember(member);
    }

    private void removeReplies(Member member) {
        replyRepository.deleteAll(replyRepository.findAllByWriter(member));
    }

    private void removeComments(Member member) {
        commentRepository.deleteAll(commentRepository.findAllByWriter(member));
    }

    private void removePosts(Member member) {
        member.getPosts().forEach(post -> postService.remove(post.getId()));
    }

    private void removeMember(Member member) {
        imageUtils.removeImageFile(member.getImage());
        memberRepository.delete(member);
    }

    @Transactional
    public void saveImage(MultipartFile image) throws IOException {
        if (image == null) {
            throw new ApiException(INVALID_INPUT);
        }
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentMemberEmail()).orElseThrow();
        imageUtils.removeImageFile(member.getImage());
        String imageName = imageUtils.createImageFile(image).getName();
        member.setImage(imageName);
    }

    @Transactional
    public void removeImage() {
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentMemberEmail()).orElseThrow();
        imageUtils.removeImageFile(member.getImage());
        member.setImage(null);
    }
}
