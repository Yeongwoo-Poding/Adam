package project.adam.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;
import project.adam.controller.CommentController;
import project.adam.controller.MemberController;
import project.adam.controller.PostController;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.Report;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.exception.ApiExceptionAdvice;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.PostService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentReportRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;
import project.adam.service.dto.post.PostCreateRequest;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static project.adam.exception.ExceptionEnum.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ReportCommentTest {

    @Autowired MockMvc mvc;
    @Autowired MemberController memberController;
    @Autowired PostController postController;
    @Autowired CommentController commentController;

    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired CommentService commentService;
    @Autowired ObjectMapper objectMapper;

    private static final String CURRENT_MEMBER_ID = "8351d242-366c-43d5-9afd-4fea1dd38f17";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(memberController, postController, commentController)
                .setControllerAdvice(new ApiExceptionAdvice())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("댓글 신고 - POST /comments/{commentId}/report")
    void report() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        String uuid = UUID.randomUUID().toString();
        Member writer = memberService.join(new MemberJoinRequest(uuid, "email1", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email1", "deviceToken"));

        Post post = postService.create(writer, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(writer, new CommentCreateRequest(post.getId(), "body"));

        String content = objectMapper.writeValueAsString(new CommentReportRequest(ReportType.BAD));

        // when
        ResultActions actions = mvc.perform(post("/comments/" + comment.getId() + "/report")
                .content(content)
                .contentType(APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("댓글 신고 - POST /comments/{commentId}/report - 신고를 제한 이상 받으면 숨김")
    void report_hide_comment() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        String uuid = UUID.randomUUID().toString();
        Member writer = memberService.join(new MemberJoinRequest(uuid, "email1", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email1", "deviceToken"));

        Post post = postService.create(writer, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(writer, new CommentCreateRequest(post.getId(), "body"));

        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            commentService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), comment, ReportType.BAD);
        }

        // when
        ResultActions actions = mvc.perform(get("/comments/" + comment.getId()));

        // then
        actions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(HIDDEN_CONTENT.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("댓글 신고 - POST /comments/{commentId}/report - Input의 형식이 Json이 아닌 경우")
    void report_input_not_json() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        String uuid = UUID.randomUUID().toString();
        Member writer = memberService.join(new MemberJoinRequest(uuid, "email1", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email1", "deviceToken"));

        Post post = postService.create(writer, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(writer, new CommentCreateRequest(post.getId(), "body"));

        String content = "{\"reportType\": \"BAD\",}";

        // when
        ResultActions actions = mvc.perform(post("/comments/" + comment.getId() + "/report")
                .content(content)
                .contentType(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_JSON_FORMAT.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("댓글 신고 - POST /comments/{commentId}/report - reportType이 없는 경우")
    void report_type_not_exist() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        String uuid = UUID.randomUUID().toString();
        Member writer = memberService.join(new MemberJoinRequest(uuid, "email1", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email1", "deviceToken"));

        Post post = postService.create(writer, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(writer, new CommentCreateRequest(post.getId(), "body"));

        String content = "{}";

        // when
        ResultActions actions = mvc.perform(post("/comments/" + comment.getId() + "/report")
                .content(content)
                .contentType(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("댓글 신고 - POST /comments/{commentId}/report - commentId가 문자인 경우")
    void report_comment_id_is_string() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        String uuid = UUID.randomUUID().toString();
        Member writer = memberService.join(new MemberJoinRequest(uuid, "email1", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email1", "deviceToken"));

        Post post = postService.create(writer, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(writer, new CommentCreateRequest(post.getId(), "body"));

        String content = objectMapper.writeValueAsString(new CommentReportRequest(ReportType.BAD));

        // when
        ResultActions actions = mvc.perform(post("/comments/" + "not_number" + "/report")
                .content(content)
                .contentType(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_TYPE.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("댓글 신고 - POST /comments/{commentId}/report - Content-Type이 application/json이 아닌 경우")
    void report_content_type_not_json() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        String uuid = UUID.randomUUID().toString();
        Member writer = memberService.join(new MemberJoinRequest(uuid, "email1", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email1", "deviceToken"));

        Post post = postService.create(writer, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(writer, new CommentCreateRequest(post.getId(), "body"));

        String content = objectMapper.writeValueAsString(new CommentReportRequest(ReportType.BAD));

        // when
        ResultActions actions = mvc.perform(post("/comments/" + comment.getId() + "/report")
                .content(content));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_HEADER.toString()));
    }
}
