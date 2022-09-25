package project.adam.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.entity.reply.Reply;
import project.adam.exception.ApiException;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.reply.ReplyCreateRequest;
import project.adam.service.dto.reply.ReplyReportRequest;
import project.adam.service.dto.reply.ReplyUpdateRequest;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class ReplyServiceTest {

    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired CommentService commentService;
    @Autowired ReplyService replyService;

    @Test
    @DisplayName("Reply 생성")
    void create() {
        // given
        Member member = createMember();
        Comment comment = createComment(member, createPost(member));

        // when
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));

        // then
        assertThat(replyService.find(reply.getId())).isEqualTo(reply);
    }

    @Test
    @DisplayName("Reply 수정")
    void update() {
        // given
        Member member = createMember();
        Comment comment = createComment(member, createPost(member));
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));

        // when
        replyService.update(reply, new ReplyUpdateRequest("updatedBody"));

        // then
        assertThat(reply.getBody()).isEqualTo("updatedBody");
    }

    @Test
    @DisplayName("Reply 삭제")
    void remove() {
        // given
        Member member = createMember();
        Comment comment = createComment(member, createPost(member));
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));

        // when
        replyService.remove(reply);

        // then
        assertThatThrownBy(() -> replyService.find(reply.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("ReplyReport 생성")
    void report() {
        // given
        Member member = createMember();
        Comment comment = createComment(member, createPost(member));
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));
        Member reportMember = createMember("reportId", "reportEmail");

        // when
        replyService.report(reportMember, reply, new ReplyReportRequest(ReportType.BAD));

        // then
        assertThat(reply.getReports().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("한명의 Member가 하나의 Reply에 여러 번 Report 하는 경우 오류")
    void report_duplicate() {
        // given
        Member member = createMember();
        Comment comment = createComment(member, createPost(member));
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));
        Member reportMember = createMember("reportId", "reportEmail");
        replyService.report(reportMember, reply, new ReplyReportRequest(ReportType.BAD));

        // when then
        assertThatThrownBy(() -> replyService.report(reportMember, reply, new ReplyReportRequest(ReportType.BAD)))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("Comment가 Report를 5번 이상 받으면 숨김")
    void hide_reply() {
        // given
        Member member = createMember();
        Comment comment = createComment(member, createPost(member));
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));
        createFiveReports(reply);
        Member reportMember = createMember("reportId", "reportEmail");

        // when then
        assertThatThrownBy(() -> replyService.find(reply.getId()))
                .isInstanceOf(ApiException.class);
    }

    private Member createMember() {
        memberService.join(new MemberJoinRequest("id", "email", "name"));
        return memberService.findByEmail("email");
    }

    private Member createMember(String id, String email) {
        memberService.join(new MemberJoinRequest(id, email, "name"));
        return memberService.findByEmail(email);
    }

    private Post createPost(Member member) {
        return postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), null);
    }

    private Comment createComment(Member member, Post post) {
        return commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
    }

    private void createFiveReports(Reply reply) {
        for (int i = 0; i < 5; i++) {
            Member reportMember = createMember("id" + i, "email" + i);
            replyService.report(reportMember, reply, new ReplyReportRequest(ReportType.BAD));
        }
    }
}
