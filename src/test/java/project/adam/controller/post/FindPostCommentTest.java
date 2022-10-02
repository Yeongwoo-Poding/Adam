package project.adam.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;
import project.adam.controller.MemberController;
import project.adam.controller.PostController;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.exception.ApiExceptionAdvice;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.PostService;
import project.adam.service.ReplyService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.reply.ReplyCreateRequest;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static project.adam.exception.ExceptionEnum.NO_DATA;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class FindPostCommentTest {

    @Autowired MockMvc mvc;
    @Autowired MemberController memberController;
    @Autowired PostController postController;

    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired CommentService commentService;
    @Autowired ReplyService replyService;
    @Autowired ObjectMapper objectMapper;

    private static final String CURRENT_MEMBER_ID = "8351d242-366c-43d5-9afd-4fea1dd38f17";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(memberController, postController)
                .setControllerAdvice(new ApiExceptionAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .addFilters(new CharacterEncodingFilter())
                .build();
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시글 댓글 조회 - GET /posts/{postId}/comments")
    void find_comment() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        commentService.create(member, new CommentCreateRequest(post.getId(), "body"));

        // when
        ResultActions actions = mvc.perform(get("/posts/" + post.getId() + "/comments")
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(3));
    }

    //Todo
    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시글 댓글 조회 - GET /posts/{postId}/comments - 숨겨진 댓글에 대댓글이 없으면 표시되지 않음")
    void find_comment_hidden_no_reply() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        commentService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email1", "name")), comment, ReportType.BAD);
        commentService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email2", "name")), comment, ReportType.BAD);
        commentService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email3", "name")), comment, ReportType.BAD);
        commentService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email4", "name")), comment, ReportType.BAD);
        commentService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email5", "name")), comment, ReportType.BAD);

        // when
        ResultActions actions = mvc.perform(get("/posts/" + post.getId() + "/comments")
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(0));
    }

    //Todo
    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시글 댓글 조회 - GET /posts/{postId}/comments - 숨겨진 댓글에 대댓글이 있으면 body가 \"숨겨진 댓글입니다.\"로 표시")
    void find_comment_hidden_reply() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));
        commentService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email1", "name")), comment, ReportType.BAD);
        commentService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email2", "name")), comment, ReportType.BAD);
        commentService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email3", "name")), comment, ReportType.BAD);
        commentService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email4", "name")), comment, ReportType.BAD);
        commentService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email5", "name")), comment, ReportType.BAD);

        // when
        ResultActions actions = mvc.perform(get("/posts/" + post.getId() + "/comments")
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.contents[0].body").value("숨겨진 댓글입니다"));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시글 댓글 조회 - GET /posts/{postId}/comments - 게시글이 존재하지 않는 경우")
    void find_comment_no_post() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        commentService.create(member, new CommentCreateRequest(post.getId(), "body"));

        // when
        ResultActions actions = mvc.perform(get("/posts/" + 2L + "/comments")
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(NO_DATA.toString()));
    }
}
