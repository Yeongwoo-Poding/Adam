package project.adam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Value("${file.dir}")
    private String imagePath;

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

    public Member find(UUID id) {
        return memberRepository.findById(id).orElseThrow();
    }

    public Member findByToken(UUID token) {
        return memberRepository.findByToken(token).orElseThrow();
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow();
    }
    @Transactional
    public void withdraw() {
        String email = SecurityUtil.getCurrentMemberEmail();
        Member deleteMember = memberRepository.findByEmail(SecurityUtil.getCurrentMemberEmail()).orElseThrow();
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
        removeExistingImage(member);
        memberRepository.delete(member);
    }

    @Transactional
    public void saveImage(MultipartFile file) throws IOException {
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentMemberEmail()).orElseThrow();
        removeExistingImage(member);

        String imageName = UUID.randomUUID() + getExtension(file);
        member.setImageName(imageName);

        File newFile = new File(imagePath + imageName);
        file.transferTo(newFile);
    }

    @Transactional
    public void removeImage() {
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentMemberEmail()).orElseThrow();
        removeExistingImage(member);
        member.setImageName(null);
    }

    private String getExtension(MultipartFile file) {
        String contentType = file.getContentType();
        String fileExtension;
        if (contentType == null) {
            throw new ApiException(ExceptionEnum.INVALID_HEADER);
        }

        if (contentType.equals("image/png")) {
            fileExtension = ".png";
        } else if (contentType.equals("image/jpeg")) {
            fileExtension = ".jpeg";
        } else {
            throw new ApiException(ExceptionEnum.INVALID_HEADER);
        }
        return fileExtension;
    }

    public String getImageName(Member member) {
        return member.getImageName();
    }

    public boolean hasImage(Member member) {
        String imageName = member.getImageName();
        if (imageName == null) {
            return false;
        }

        return new File(imagePath + imageName).exists();
    }

    private void removeExistingImage(Member member) {
        String existingImage = member.getImageName();
        if (existingImage != null) {
            File image = new File(imagePath + existingImage);
            if (!image.delete()) {
                log.warn( "[{}.removeImage] Image has not been deleted.", getClass().getName());
            }
        }
    }
}
