package project.adam.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import project.adam.service.dto.comment.CommentReportRequest;
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
    @DisplayName("Comment 생성")
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
    @DisplayName("Post 하위 comment 조회")
    void find_comments_by_post() {
        // given
        Member member = createMember();
        Post postA = createPost(member);
        Post postB = createPost(member);
        Comment commentA = commentService.create(member, new CommentCreateRequest(postA.getId(), "body"));
        Comment commentB = commentService.create(member, new CommentCreateRequest(postA.getId(), "body"));
        Comment commentC = commentService.create(member, new CommentCreateRequest(postB.getId(), "body"));
        Comment commentD = commentService.create(member, new CommentCreateRequest(postB.getId(), "body"));
        Pageable pageable = PageRequest.of(0, 20);

        // when
        List<Comment> postAComments = commentService.findByPost(postA.getId(), pageable).getContent();
        List<Comment> postBComments = commentService.findByPost(postB.getId(), pageable).getContent();

        // then
        assertThat(postAComments).containsExactly(commentA, commentB);
        assertThat(postBComments).containsExactly(commentC, commentD);
    }

    @Test
    @DisplayName("Comment 수정")
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
    @DisplayName("Comment 삭제")
    void remove() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));

        // when
        commentService.remove(comment);

        // then
        assertThatThrownBy(() -> commentService.find(comment.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("Comment 삭제시 하위 reply 삭제")
    void remove_comment_with_reply() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));

        // when
        commentService.remove(comment);

        // then
        assertThatThrownBy(() -> replyService.find(reply.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("CommentReport 생성")
    void report() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        Member reportMember = createMember("reportId", "reportEmail");

        // when
        commentService.report(reportMember, comment, new CommentReportRequest(ReportType.BAD));

        // then
        assertThat(comment.getReports().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("한명의 Member가 하나의 Comment에 여러 번 Report 하는 경우 오류")
    void report_duplicate() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        Member reportMember = createMember("reportId", "reportEmail");
        commentService.report(reportMember, comment, new CommentReportRequest(ReportType.BAD));

        // when then
        assertThatThrownBy(() -> commentService.report(reportMember, comment, new CommentReportRequest(ReportType.BAD)))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("Comment가 Report를 5번 이상 받으면 숨김")
    void hide_comment() {
        // given
        Member member = createMember();
        Post post = createPost(member);
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        createFiveReports(comment);

        // when then
        assertThatThrownBy(() -> commentService.find(comment.getId()))
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

    private void createFiveReports(Comment comment) {
        for (int i = 0; i < 5; i++) {
            Member reportMember = createMember("id" + i, "email" + i);
            commentService.report(reportMember, comment, new CommentReportRequest(ReportType.BAD));
        }
    }
}
