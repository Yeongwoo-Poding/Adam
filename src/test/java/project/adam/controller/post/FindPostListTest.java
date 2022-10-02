package project.adam.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;
import project.adam.controller.MemberController;
import project.adam.controller.PostController;
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

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class FindPostListTest {

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
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .addFilters(new CharacterEncodingFilter())
                .build();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("게시글 목록 조회 - GET /posts - 조건 없음")
    void find_post_list_no_condition() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        postService.create(member, new PostCreateRequest(Board.FREE, "titleA", "body"));
        postService.create(member, new PostCreateRequest(Board.FREE, "titleB", "body"));
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleA", "body"));
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleB", "body"));

        // when
        ResultActions actions = mvc.perform(get("/posts")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(4));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("게시글 목록 조회 - GET /posts - board 조건")
    void find_post_list_board_condition() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        postService.create(member, new PostCreateRequest(Board.FREE, "titleA", "body"));
        postService.create(member, new PostCreateRequest(Board.FREE, "titleB", "body"));
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleA", "body"));
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleB", "body"));

        // when
        ResultActions actions = mvc.perform(get("/posts")
                .param("board", "FREE")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("게시글 목록 조회 - GET /posts - content 조건")
    void find_post_list_content_condition() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        postService.create(member, new PostCreateRequest(Board.FREE, "titleA", "body"));
        postService.create(member, new PostCreateRequest(Board.FREE, "titleB", "body"));
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleA", "body"));
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleB", "body"));

        // when
        ResultActions actions = mvc.perform(get("/posts")
                .param("content", "A")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("게시글 목록 조회 - GET /posts - board & content 조건")
    void find_post_list_board_content_condition() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        postService.create(member, new PostCreateRequest(Board.FREE, "titleA", "body"));
        postService.create(member, new PostCreateRequest(Board.FREE, "titleB", "body"));
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleA", "body"));
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleB", "body"));

        // when
        ResultActions actions = mvc.perform(get("/posts")
                .param("board", "FREE")
                .param("content", "A")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(1));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("게시글 목록 조회 - GET /posts - 숨겨신 게시글은 표시되지 않음")
    void find_post_list_hidden_content() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "titleA", "body"));
        postService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email1", "name")), post, ReportType.BAD);
        postService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email2", "name")), post, ReportType.BAD);
        postService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email3", "name")), post, ReportType.BAD);
        postService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email4", "name")), post, ReportType.BAD);
        postService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email5", "name")), post, ReportType.BAD);

        // when
        ResultActions actions = mvc.perform(get("/posts")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(0));
    }
}
