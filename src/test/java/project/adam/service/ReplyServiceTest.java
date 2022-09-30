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
    @DisplayName("대댓글 생성")
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
    @DisplayName("대댓글 생성시 댓글이 존재하지 않은 경우 오류")
    void create_no_comment() {
        // given
        Member member = createMember();

        // then
        assertThatThrownBy(() -> replyService.create(member, new ReplyCreateRequest(1L, "body")))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("대댓글 조회시 존재하지 않은 경우 오류")
    void find_no_reply() {
        // then
        assertThatThrownBy(() -> replyService.find(1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("대댓글 수정")
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
    @DisplayName("대댓글 삭제")
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
    @DisplayName("대댓글 신고")
    void report() {
        // given
        Member member = createMember();
        Comment comment = createComment(member, createPost(member));
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));
        Member reportMember = createMember("reportId", "reportEmail");

        // when
        replyService.report(reportMember, reply, ReportType.BAD);

        // then
        assertThat(reply.getReports().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("대댓글을 중복 신고하는 경우 오류")
    void report_duplicate() {
        // given
        Member member = createMember();
        Comment comment = createComment(member, createPost(member));
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));
        Member reportMember = createMember("reportId", "reportEmail");
        replyService.report(reportMember, reply, ReportType.BAD);

        // when then
        assertThatThrownBy(() -> replyService.report(reportMember, reply, ReportType.BAD))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("자신의 대댓글을 신고하는 경우 오류")
    void report_my_reply() {
        // given
        Member member = createMember();
        Comment comment = createComment(member, createPost(member));
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));

        // when then
        assertThatThrownBy(() -> replyService.report(member, reply, ReportType.BAD))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("대댓글이 5번 이상 받으면 숨김")
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
        return postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
    }

    private Comment createComment(Member member, Post post) {
        return commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
    }

    private void createFiveReports(Reply reply) {
        for (int i = 0; i < 5; i++) {
            Member reportMember = createMember("id" + i, "email" + i);
            replyService.report(reportMember, reply, ReportType.BAD);
        }
    }
}
