package project.adam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.adam.entity.member.Member;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.member.MemberRepository;
import project.adam.service.dto.member.MemberJoinRequest;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    private final CommentRepository commentRepository;
    private final PostService postService;

    @Value("${file.dir}")
    private String imagePath;

    @Transactional
    public UUID join(MemberJoinRequest memberDto) {
        Member savedMember = memberRepository.save(new Member(
                UUID.fromString(memberDto.getId()),
                memberDto.getEmail(),
                memberDto.getName(),
                memberDto.getPrivilege()
        ));

        return savedMember.getToken();
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
    public UUID login(UUID memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow();
        return findMember.login();
    }

    @Transactional
    public void withdraw(UUID id) {
        Member deleteMember = memberRepository.findByToken(id).orElseThrow();
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
    public void saveImage(Member member, MultipartFile file) throws IOException {
        removeExistingImage(member);

        String imageName = UUID.randomUUID() + getExtension(file);
        member.setImageName(imageName);

        File newFile = new File(imagePath + imageName);
        file.transferTo(newFile);
    }

    @Transactional
    public void removeImage(Member member) {
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
