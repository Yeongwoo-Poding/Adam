package project.adam.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.member.Member;
import project.adam.entity.member.MemberStatus;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.entity.reply.Reply;
import project.adam.exception.ApiException;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.reply.ReplyCreateRequest;
import project.adam.utils.image.ImageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired CommentService commentService;
    @Autowired ReplyService replyService;
    @Autowired ImageUtils imageUtils;

    @Value("${image.path}")
    String imagePath;

    @BeforeEach
    void before_each() {
        imageUtils.removeAll();
    }

    @Test
    @DisplayName("회원가입")
    void join() {
        // given when
        memberService.join(new MemberJoinRequest("id", "email", "name"));
        Member member = memberService.findByEmail("email");

        // then
        assertThat(member.getName()).isEqualTo("name");
    }

    @Test
    @DisplayName("회원가입시 중복된 이메일이 있으면 오류")
    void join_unique_constraint() {
        // given
        memberService.join(new MemberJoinRequest("id", "email", "name"));

        // when then
        assertThatThrownBy(() -> memberService.join(new MemberJoinRequest("id", "email", "new member")))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("로그인")
    void login() {
        // given
        memberService.join(new MemberJoinRequest("id", "email", "name"));
        Member member = memberService.findByEmail("email");

        // when
        memberService.login(new MemberLoginRequest("id", "email", "deviceToken"));

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.LOGIN);
    }

    @Test
    @DisplayName("로그인시 존재하지 않는 사용자면 오류")
    void login_no_member() {
        // given when then
        assertThatThrownBy(() -> memberService.login(new MemberLoginRequest("id", "email", "deviceToken")))
                .isInstanceOf(InternalAuthenticationServiceException.class);
    }

    @Test
    @DisplayName("로그아웃")
    void logout() {
        // given
        memberService.join(new MemberJoinRequest("id", "email", "name"));
        memberService.login(new MemberLoginRequest("id", "email", "deviceToken"));
        Member member = memberService.findByEmail("email");

        // when
        memberService.logout(member);

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.LOGOUT);
    }

    @Test
    @DisplayName("회원 조회시 존재하지 않은 사용자면 오류")
    void find_member_no_exist() {
        assertThatThrownBy(() -> memberService.findByEmail("not_exist"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("회원 탈퇴")
    void withdraw() {
        // given
        memberService.join(new MemberJoinRequest("id", "email", "name"));
        Member member = memberService.findByEmail("email");

        // when
        memberService.withdraw(member);

        // then
        assertThat(memberService.findByEmail("email").getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
    }

    @Test
    @DisplayName("회원 탈퇴시 회원이 작성한 게시글 삭제")
    void withdraw_remove_posts() {
        // given
        memberService.join(new MemberJoinRequest("id", "email", "name"));
        Member member = memberService.findByEmail("email");
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));

        // when
        memberService.withdraw(member);

        // then
        assertThat(postService.find(post.getId()).getStatus()).isEqualTo(ContentStatus.REMOVED);
    }

    @Test
    @DisplayName("회원 탈퇴시 작성한 댓글 삭제")
    void withdraw_remove_comments() {
        // given
        Post post = createOthersPost();

        memberService.join(new MemberJoinRequest("id", "email", "name"));
        Member member = memberService.findByEmail("email");
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));

        // when
        memberService.withdraw(member);

        // then
        assertThat(commentService.find(comment.getId()).getStatus()).isEqualTo(ContentStatus.REMOVED);
    }

    @Test
    @DisplayName("회원 탈퇴시 작성한 대댓글 삭제")
    void withdraw_remove_replies() {
        // given
        Comment comment = createOthersComment();

        memberService.join(new MemberJoinRequest("id", "email", "name"));
        Member member = memberService.findByEmail("email");
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));

        // when
        memberService.withdraw(member);

        // then
        assertThat(replyService.find(reply.getId()).getStatus()).isEqualTo(ContentStatus.REMOVED);
    }

    @Test
    @DisplayName("프로필 이미지 추가")
    void create_profile_image() throws IOException {
        // given
        memberService.join(new MemberJoinRequest("id", "email", "name"));
        Member member = memberService.findByEmail("email");


        // when
        memberService.saveImage(member, new MockMultipartFile("name", "originName", "image/png", getFileInputStream()));

        // then
        assertThat(getImage(member.getImage()).exists()).isTrue();
    }

    @Test
    @DisplayName("프로필 이미지 삭제")
    void remove_profile_image() throws IOException {
        // given
        memberService.join(new MemberJoinRequest("id", "email", "name"));
        Member member = memberService.findByEmail("email");

        memberService.saveImage(member, new MockMultipartFile("name", "originName", "image/png", getFileInputStream()));
        File profileImage = getImage(member.getImage());

        // when
        memberService.removeImage(member);

        // then
        assertThat(member.getImage()).isNull();
        assertThat(profileImage.exists()).isFalse();
    }

    private Post createOthersPost() {
        memberService.join(new MemberJoinRequest("othersId", "othersEmail", "postWriter"));
        Member writer = memberService.findByEmail("othersEmail");
        return postService.create(writer, new PostCreateRequest(Board.FREE, "title", "body"));
    }

    private Comment createOthersComment() {
        memberService.join(new MemberJoinRequest("othersId", "othersEmail", "postWriter"));
        Member writer = memberService.findByEmail("othersEmail");
        Post post = postService.create(writer, new PostCreateRequest(Board.FREE, "title", "body"));
        return commentService.create(writer, new CommentCreateRequest(post.getId(), "body"));
    }

    @NotNull
    private FileInputStream getFileInputStream() {
        try {
            File image = new File("src/test/resources/image/testimage.png");
            return new FileInputStream(image);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("해당 파일이 없습니다");
        }
    }

    @NotNull
    private File getImage(String imageName) {
        return new File(imagePath + imageName);
    }
}
