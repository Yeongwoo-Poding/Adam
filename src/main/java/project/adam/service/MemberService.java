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
import project.adam.entity.member.Member;
import project.adam.exception.ApiException;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.member.MemberRepository;
import project.adam.repository.reply.ReplyRepository;
import project.adam.security.SecurityUtils;
import project.adam.security.TokenProvider;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;
import project.adam.utils.image.ImageUtils;

import java.io.IOException;
import java.util.List;

import static project.adam.exception.ExceptionEnum.INVALID_INPUT;
import static project.adam.exception.ExceptionEnum.UNIQUE_CONSTRAINT_VIOLATED;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
//    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final PostService postService;
    private final ImageUtils imageUtils;


    @Transactional
    public void join(MemberJoinRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(UNIQUE_CONSTRAINT_VIOLATED);
        }

        Member createdMember = Member.builder()
                .id(passwordEncoder.encode(request.getId()))
                .email(request.getEmail())
                .name(request.getName())
                .build();

        memberRepository.save(createdMember);
    }

    @Transactional
    public MemberLoginResponse login(MemberLoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        MemberLoginResponse memberLoginResponse = tokenProvider.generateTokenResponse(authenticate);
//        RefreshToken refreshToken = RefreshToken.builder()
//                .key(authenticate.getName())
//                .value(memberLoginResponse.getRefreshToken())
//                .build();
//
//        refreshTokenRepository.save(refreshToken);

        Member loginMember = memberRepository.findMemberByEmail(authenticate.getName()).orElseThrow();
        loginMember.login(request.getDeviceToken());

        return memberLoginResponse;
    }

    @Transactional
    public void logout(Member member) {
        member.logout();
    }

//    @Transactional
//    public MemberLoginResponse refreshToken(MemberRefreshResponse memberDto) {
//        if (!tokenProvider.validateToken(memberDto.getRefreshToken())) {
//            throw new ApiException(AUTHENTICATION_FAILED);
//        }
//
//        Authentication authentication = tokenProvider.getAuthentication(memberDto.getAccessToken());
//        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName()).orElseThrow(() -> new ApiException(AUTHENTICATION_FAILED));
//
//        if (!refreshToken.getValue().equals(memberDto.getRefreshToken())) {
//            throw new ApiException(AUTHENTICATION_FAILED);
//        }
//
//        MemberLoginResponse memberLoginResponse = tokenProvider.generateTokenDto(authentication);
//
//        RefreshToken newRefreshToken = refreshToken.updateValue(memberLoginResponse.getRefreshToken());
//        refreshTokenRepository.save(newRefreshToken);
//
//        return memberLoginResponse;
//    }

    public Member findByEmail(String email) {
        return memberRepository.findMemberByEmail(email).orElseThrow();
    }

    public List<Member> findLoginUsers() {
        return memberRepository.findLoginUsers();
    }

    @Transactional
    public void withdraw(Member member) {
        removeReplies(member);
        removeComments(member);
        removePosts(member);
        removeMember(member);
    }

    private void removeReplies(Member member) {
        replyRepository.deleteAll(replyRepository.findRepliesByWriter(member));
    }

    private void removeComments(Member member) {
        commentRepository.deleteAll(commentRepository.findCommentsByWriter(member));
    }

    private void removePosts(Member member) {
        member.getPosts().forEach(postService::remove);
    }

    private void removeMember(Member member) {
        imageUtils.removeImageFile(member.getImage());
        memberRepository.delete(member);
    }

    @Transactional
    public void saveImage(Member member, MultipartFile image) throws IOException {
        if (image == null) {
            throw new ApiException(INVALID_INPUT);
        }
        imageUtils.removeImageFile(member.getImage());
        String imageName = imageUtils.createImageFile(image).getName();
        member.setImage(imageName);
    }

    @Transactional
    public void removeImage(Member member) {
        imageUtils.removeImageFile(member.getImage());
        member.setImage(null);
    }

    public void authorization(Member member) {
        Member loginMember = memberRepository.findMemberByEmail(SecurityUtils.getCurrentMemberEmail()).orElseThrow();
        loginMember.authorization(member);
    }
}
