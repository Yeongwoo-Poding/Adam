package project.adam.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import project.adam.admin.AdminService;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.common.Report;
import project.adam.entity.common.ReportContent;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Authority;
import project.adam.entity.member.Member;
import project.adam.entity.member.MemberStatus;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.entity.reply.Reply;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.reply.ReplyCreateRequest;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static project.adam.entity.common.ReportContent.ContentType.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class AdminServiceTest {

    @Autowired AdminService adminService;
    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired CommentService commentService;
    @Autowired ReplyService replyService;

    @Value("${admin.test.username}")
    private String username;

    @Value("${admin.test.password}")
    private String password;

    @Test
    @DisplayName("ADMIN 권한으로 로그인")
    void login() {
        // given when
        memberService.login(new MemberLoginRequest(password, username, "deviceToken"));

        // then
        assertThat(memberService.findByEmail(username).getStatus()).isEqualTo(MemberStatus.LOGIN);
        assertThat(memberService.findByEmail(username).getAuthority()).isEqualTo(Authority.ROLE_ADMIN);
    }

    @Test
    @DisplayName("정지시 status 변경, suspendedDate 변경")
    void ban() {
        // given
        Member member = memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email", "name"));

        // when
        member.ban(3);

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.SUSPENDED);
        assertThat(member.getSuspendedDate().toLocalDate()).isEqualTo(LocalDate.now().plusDays(3));
    }

    @Test
    @DisplayName("정지 후 suspendedDate가 지나면 status 값 변경")
    void ban_after_date() {
        // given
        Member member = memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email", "name"));

        // when
        member.ban(0);

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.LOGOUT);
    }

    @Test
    @DisplayName("게시글 신고내용 생성")
    void report_content_post() {
        // given
        Member member = memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email", "name"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));

        // when
        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            postService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), post, ReportType.BAD);
        }

        // then
        assertThat(adminService.findReportContents().size()).isEqualTo(1);
        ReportContent content = adminService.findReportContents().get(0);
        assertThat(adminService.findReportContent(content.getId()).getContentType()).isEqualTo(POST);
        assertThat(adminService.findReportContent(content.getId()).getContentId()).isEqualTo(post.getId());
    }

    @Test
    @DisplayName("댓글 신고내용 생성")
    void report_content_comment() {
        // given
        Member member = memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email", "name"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));

        // when
        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            commentService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), comment, ReportType.BAD);
        }

        // then
        assertThat(adminService.findReportContents().size()).isEqualTo(1);
        ReportContent content = adminService.findReportContents().get(0);
        assertThat(adminService.findReportContent(content.getId()).getContentType()).isEqualTo(COMMENT);
        assertThat(adminService.findReportContent(content.getId()).getContentId()).isEqualTo(comment.getId());
    }

    @Test
    @DisplayName("대댓글 신고내용 생성")
    void report_content_reply() {
        // given
        Member member = memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email", "name"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));

        // when
        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            replyService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), reply, ReportType.BAD);
        }

        // then
        assertThat(adminService.findReportContents().size()).isEqualTo(1);
        ReportContent content = adminService.findReportContents().get(0);
        assertThat(adminService.findReportContent(content.getId()).getContentType()).isEqualTo(REPLY);
        assertThat(adminService.findReportContent(content.getId()).getContentId()).isEqualTo(reply.getId());
    }

    @Test
    @DisplayName("게시글 신고")
    void ban_post() {
        // given
        Member member = memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email", "name"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            postService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), post, ReportType.BAD);
        }
        ReportContent content = adminService.findReportContents().get(0);
        assertThat(postService.find(post.getId()).getStatus()).isEqualTo(ContentStatus.HIDDEN);

        // when
        Member bannedMember = adminService.ban(content, 3);

        // then
        assertThat(member.getId()).isEqualTo(bannedMember.getId());
        assertThat(postService.find(post.getId()).getStatus()).isEqualTo(ContentStatus.REMOVED);
        assertThat(memberService.findByEmail("email").getStatus()).isEqualTo(MemberStatus.SUSPENDED);
    }

    @Test
    @DisplayName("댓글 신고")
    void ban_comment() {
        // given
        Member member = memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email", "name"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            commentService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), comment, ReportType.BAD);
        }
        ReportContent content = adminService.findReportContents().get(0);
        assertThat(commentService.find(comment.getId()).getStatus()).isEqualTo(ContentStatus.HIDDEN);

        // when
        Member bannedMember = adminService.ban(content, 3);

        // then
        assertThat(member.getId()).isEqualTo(bannedMember.getId());
        assertThat(commentService.find(comment.getId()).getStatus()).isEqualTo(ContentStatus.REMOVED);
        assertThat(memberService.findByEmail("email").getStatus()).isEqualTo(MemberStatus.SUSPENDED);
    }

    @Test
    @DisplayName("대댓글 신고")
    void ban_reply() {
        // given
        Member member = memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email", "name"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));
        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            replyService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), reply, ReportType.BAD);
        }
        ReportContent content = adminService.findReportContents().get(0);
        assertThat(replyService.find(reply.getId()).getStatus()).isEqualTo(ContentStatus.HIDDEN);

        // when
        Member bannedMember = adminService.ban(content, 3);

        // then
        assertThat(member.getId()).isEqualTo(bannedMember.getId());
        assertThat(replyService.find(reply.getId()).getStatus()).isEqualTo(ContentStatus.REMOVED);
        assertThat(memberService.findByEmail("email").getStatus()).isEqualTo(MemberStatus.SUSPENDED);
    }

    @Test
    @DisplayName("게시글 신고 해제")
    void release_post() {
        // given
        Member member = memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email", "name"));
        member.login("deviceToken");
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            postService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), post, ReportType.BAD);
        }
        ReportContent content = adminService.findReportContents().get(0);
        assertThat(postService.find(post.getId()).getStatus()).isEqualTo(ContentStatus.HIDDEN);

        // when
        adminService.release(content);

        // then
        assertThat(adminService.findReportContents().size()).isEqualTo(0);
        assertThat(postService.find(post.getId()).getStatus()).isEqualTo(ContentStatus.PUBLISHED);
        assertThat(memberService.findByEmail("email").getStatus()).isEqualTo(MemberStatus.LOGIN);
    }

    @Test
    @DisplayName("댓글 신고 해제")
    void release_comment() {
        // given
        Member member = memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email", "name"));
        member.login("deviceToken");
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            commentService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), comment, ReportType.BAD);
        }
        ReportContent content = adminService.findReportContents().get(0);
        assertThat(commentService.find(comment.getId()).getStatus()).isEqualTo(ContentStatus.HIDDEN);

        // when
        adminService.release(content);

        // then
        assertThat(adminService.findReportContents().size()).isEqualTo(0);
        assertThat(commentService.find(comment.getId()).getStatus()).isEqualTo(ContentStatus.PUBLISHED);
        assertThat(memberService.findByEmail("email").getStatus()).isEqualTo(MemberStatus.LOGIN);
    }

    @Test
    @DisplayName("대댓글 신고 해제")
    void release_reply() {
        // given
        Member member = memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email", "name"));
        member.login("deviceToken");
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));
        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            replyService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), reply, ReportType.BAD);
        }
        ReportContent content = adminService.findReportContents().get(0);
        assertThat(replyService.find(reply.getId()).getStatus()).isEqualTo(ContentStatus.HIDDEN);

        // when
        adminService.release(content);

        // then
        assertThat(adminService.findReportContents().size()).isEqualTo(0);
        assertThat(replyService.find(reply.getId()).getStatus()).isEqualTo(ContentStatus.PUBLISHED);
        assertThat(memberService.findByEmail("email").getStatus()).isEqualTo(MemberStatus.LOGIN);
    }
}
