package project.adam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.adam.entity.member.Member;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.member.MemberRepository;
import project.adam.security.SecurityUtil;
import project.adam.security.TokenProvider;
import project.adam.security.dto.RefreshTokenDto;
import project.adam.security.dto.TokenDto;
import project.adam.security.refreshtoken.RefreshToken;
import project.adam.security.refreshtoken.RefreshTokenRepository;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;
import project.adam.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

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
    private final PostService postService;
    private final ImageUtils imageUtils;


    @Transactional
    public UUID join(MemberJoinRequest memberDto) {
        if (memberRepository.existsByEmail(memberDto.getEmail())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }

        Member savedMember = memberRepository.save(new Member(
                passwordEncoder.encode(memberDto.getId()),
                memberDto.getEmail(),
                memberDto.getName(),
                memberDto.getAuthority()
        ));

        return savedMember.getToken();
    }

    @Transactional
    public TokenDto login(MemberLoginRequest memberDto) {
        UsernamePasswordAuthenticationToken authenticationToken = memberDto.toAuthentication();
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenDto tokenDto = tokenProvider.generateTokenDto(authenticate);
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authenticate.getName())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }

    public TokenDto refreshToken(RefreshTokenDto memberDto) {
        if (!tokenProvider.validateToken(memberDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다");
        }

        Authentication authentication = tokenProvider.getAuthentication(memberDto.getAccessToken());
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName()).orElseThrow(() -> new RuntimeException("로그아웃된 사용자입니다."));

        if (!refreshToken.getValue().equals(memberDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다");
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return tokenDto;
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow();
    }

    @Transactional
    public void withdraw(Member member) {
        removeCommits(member);
        removePosts(member);
        removeMember(member);
    }

    private void removeCommits(Member member) {
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
    public void saveImage(MultipartFile file) throws IOException {
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentMemberEmail()).orElseThrow();
        imageUtils.removeImageFile(member.getImage());
        String imageName = imageUtils.createImageFile(file).getName();
        member.setImage(imageName);
    }

    @Transactional
    public void removeImage() {
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentMemberEmail()).orElseThrow();
        imageUtils.removeImageFile(member.getImage());
        member.setImage(null);
    }
}
