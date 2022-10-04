package project.adam.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;
import project.adam.controller.MemberController;
import project.adam.controller.PostController;
import project.adam.entity.common.Report;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.exception.ApiExceptionAdvice;
import project.adam.service.MemberService;
import project.adam.service.PostService;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostReportRequest;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static project.adam.exception.ExceptionEnum.*;


@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ReportPostTest {

    @Autowired MockMvc mvc;
    @Autowired MemberController memberController;
    @Autowired PostController postController;

    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired ObjectMapper objectMapper;

    private static final String CURRENT_MEMBER_ID = "8351d242-366c-43d5-9afd-4fea1dd38f17";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(memberController, postController)
                .setControllerAdvice(new ApiExceptionAdvice())
                .addFilters(new CharacterEncodingFilter())
                .build();
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시물 신고 - POST /posts/{postId}/report")
    void report() throws Exception {
        // given
        String uuid = UUID.randomUUID().toString();
        Member member = memberService.join(new MemberJoinRequest(uuid, "email1", "name"));
        Member reportMember = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email1", "name"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));

        // when
        ResultActions actions = mvc.perform(post("/posts/" + post.getId() + "/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PostReportRequest(ReportType.BAD))));

        // then
        actions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시물 신고 - POST /posts/{postId}/report - 신고를 제한 이상 받으면 숨김")
    void report_hide() throws Exception {
        // given
        String uuid = UUID.randomUUID().toString();
        Member member = memberService.join(new MemberJoinRequest(uuid, "email1", "name"));
        Member reportMember = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email1", "name"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            postService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), post, ReportType.BAD);
        }

        // when
        ResultActions actions = mvc.perform(get("/posts/" + post.getId()));

        // then
        actions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(HIDDEN_CONTENT.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시물 신고 - POST /posts/{postId}/report - Input이 Json이 아닌 경우")
    void report_input_not_json() throws Exception {
        // given
        String uuid = UUID.randomUUID().toString();
        Member member = memberService.join(new MemberJoinRequest(uuid, "email1", "name"));
        Member reportMember = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email1", "name"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));

        // when
        ResultActions actions = mvc.perform(post("/posts/" + post.getId() + "/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reportType\": \"BAD\",}"));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_JSON_FORMAT.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시물 신고 - POST /posts/{postId}/report - Content-Type이 application/json이 아닌 경우")
    void report_content_type_not_json() throws Exception {
        // given
        String uuid = UUID.randomUUID().toString();
        Member member = memberService.join(new MemberJoinRequest(uuid, "email1", "name"));
        Member reportMember = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email1", "name"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));

        // when
        ResultActions actions = mvc.perform(post("/posts/" + post.getId() + "/report")
                .content(objectMapper.writeValueAsString(new PostReportRequest(ReportType.BAD))));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_HEADER.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시물 신고 - POST /posts/{postId}/report - reportType 필드가 존재하지 않는 경우")
    void report_type_field_not_exist() throws Exception {
        // given
        String uuid = UUID.randomUUID().toString();
        Member member = memberService.join(new MemberJoinRequest(uuid, "email1", "name"));
        Member reportMember = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email1", "name"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));

        // when
        ResultActions actions = mvc.perform(post("/posts/" + post.getId() + "/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시물 신고 - POST /posts/{postId}/report - postId가 문자인 경우")
    void report_post_id_string() throws Exception {
        // given
        String uuid = UUID.randomUUID().toString();
        Member member = memberService.join(new MemberJoinRequest(uuid, "email1", "name"));
        Member reportMember = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email1", "name"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));

        // when
        ResultActions actions = mvc.perform(post("/posts/" + "not_number" + "/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PostReportRequest(ReportType.BAD))));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_TYPE.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시물 신고 - POST /posts/{postId}/report - report type이 존재하지 않는 경우")
    void report_type_not_exist() throws Exception {
        // given
        String uuid = UUID.randomUUID().toString();
        Member member = memberService.join(new MemberJoinRequest(uuid, "email1", "name"));
        Member reportMember = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email1", "name"));

        // when
        ResultActions actions = mvc.perform(post("/posts/" + 1L + "/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PostReportRequest(ReportType.BAD))));

        // then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(NO_DATA.toString()));
    }
}
