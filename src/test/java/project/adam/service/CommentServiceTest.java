package project.adam.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.entity.reply.Reply;
import project.adam.exception.ApiException;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentUpdateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.reply.ReplyCreateRequest;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class CommentServiceTest {

    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired CommentService commentService;
    @Autowired ReplyService replyService;

    @Test
    @DisplayName("댓글 생성")
    void create() {
        // given
        Member member = createMember();
        Post post = createPost(member);

        // when
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));

        // then
        assertThat(commentService.find(comment.getId())).isEqualTo(comment);
    }

    @Test
    @DisplayName("댓글 생성시 게시글이 존재하지 않는 경우")
    void create_no_post() {
        // given
        Member member = createMember();

        // when
        assertThatThrownBy(() -> commentService.create(member, new CommentCreateRequest(1L, "body")))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("댓글 조회시 존재하지 않는 경우 오류")
    void find_comment_not_exist() {
        // when
        assertThatThrownBy(() -> commentService.find(1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("댓글 하위의 대댓글 목록 조회")
    void find_replies_by_comment() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment1 = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        Comment comment2 = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        Reply reply1 = replyService.create(member, new ReplyCreateRequest(comment1.getId(), "body"));
        Reply reply2 = replyService.create(member, new ReplyCreateRequest(comment1.getId(), "body"));
        Reply reply3 = replyService.create(member, new ReplyCreateRequest(comment2.getId(), "body"));
        Reply reply4 = replyService.create(member, new ReplyCreateRequest(comment2.getId(), "body"));

        // when
        List<Reply> replies1 = replyService.findByComment(comment1);
        List<Reply> replies2 = replyService.findByComment(comment2);

        // then
        assertThat(replies1).containsExactly(reply1, reply2);
        assertThat(replies2).containsExactly(reply3, reply4);
    }

    @Test
    @DisplayName("댓글 수정")
    void update() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));

        // when
        commentService.update(comment, new CommentUpdateRequest("updatedBody"));

        // then
        assertThat(comment.getBody()).isEqualTo("updatedBody");
    }

    @Test
    @DisplayName("댓글 삭제")
    void remove() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));

        // when
        commentService.remove(comment);

        // then
        assertThat(commentService.find(comment.getId()).getStatus()).isEqualTo(ContentStatus.REMOVED);
    }

//    @Test
//    @DisplayName("댓글 삭제시 하위 대댓글 삭제")
//    void remove_comment_with_reply() {
//        // given
//        Member member = createMember();
//        Post post = createPost(member);
//        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
//        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));
//
//        // when
//        commentService.remove(comment);
//
//        // then
//        assertThatThrownBy(() -> replyService.find(reply.getId()))
//                .isInstanceOf(NoSuchElementException.class);
//    }

    @Test
    @DisplayName("댓글 신고")
    void report() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        Member reportMember = createMember("reportId", "reportEmail");

        // when
        commentService.report(reportMember, comment, ReportType.BAD);

        // then
        assertThat(comment.getReports().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글을 중복 신고하는 경우 오류")
    void report_duplicate() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        Member reportMember = createMember("reportId", "reportEmail");
        commentService.report(reportMember, comment, ReportType.BAD);

        // when then
        assertThatThrownBy(() -> commentService.report(reportMember, comment, ReportType.BAD))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("자신의 댓글을 신고하는 경우 오류")
    void report_my_comment() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));

        // when then
        assertThatThrownBy(() -> commentService.report(member, comment, ReportType.BAD))
                .isInstanceOf(ApiException.class);
    }

//    @Test
//    @DisplayName("댓글이 제한 이상 신고를 받으면 숨김")
//    void hide_comment() {
//        // given
//        Member member = createMember();
//        Post post = createPost(member);
//        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
//        createReports(comment);
//
//        // when then
//        assertThatThrownBy(() -> commentService.find(comment.getId()))
//                .isInstanceOf(ApiException.class);
//    }

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
}
