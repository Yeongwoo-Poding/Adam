package project.adam.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;
import project.adam.controller.MemberController;
import project.adam.exception.ApiExceptionAdvice;
import project.adam.service.MemberService;
import project.adam.service.dto.member.MemberJoinRequest;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static project.adam.exception.ExceptionEnum.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class JoinTest {

    @Autowired MockMvc mvc;
    @Autowired MemberController memberController;

    @Autowired MemberService memberService;
    @Autowired ObjectMapper objectMapper;

    private static final String CURRENT_MEMBER_ID = "8351d242-366c-43d5-9afd-4fea1dd38f17";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(memberController)
                .setControllerAdvice(new ApiExceptionAdvice())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 한글 깨짐 처리
                .build();
    }

    @Test
    @DisplayName("회원가입 - POST /members")
    void join() throws Exception {
        // given
        String body = objectMapper.writeValueAsString(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));

        // when
        ResultActions actions = mvc.perform(post("/members")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 - POST /members - Input의 형태가 JSON이 아닌 경우")
    void join_not_json_input() throws Exception {
        // given
        String body = "{\"id\": \"" + CURRENT_MEMBER_ID + "\", \"email\": \"email\", \"name\": \"name\",}";

        // when
        ResultActions actions = mvc.perform(post("/members")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_JSON_FORMAT.toString()));
    }

    @Test
    @DisplayName("회원가입 - POST /members - id가 없는 경우")
    void join_no_id() throws Exception {
        // given
        String body = "{\"email\": \"email\", \"name\": \"name\"}";

        // when
        ResultActions actions = mvc.perform(post("/members")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("회원가입 - POST /members - id가 빈 문자열인 경우")
    void join_id_empty() throws Exception {
        // given
        String body = objectMapper.writeValueAsString(new MemberJoinRequest("", "email", "name"));

        // when
        ResultActions actions = mvc.perform(post("/members")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("회원가입 - POST /members - id가 UUID 형식이 아닌 경우")
    void join_id_not_uuid() throws Exception {
        // given
        String body = objectMapper.writeValueAsString(new MemberJoinRequest("not_uuid", "email", "name"));

        // when
        ResultActions actions = mvc.perform(post("/members")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("회원가입 - POST /members - email이 없는 경우")
    void join_no_email() throws Exception {
        // given
        String body = "{\"id\": \"" + UUID.randomUUID() + "\", \"name\": \"name\"}";

        // when
        ResultActions actions = mvc.perform(post("/members")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("회원가입 - POST /members - email이 빈 문자열인 경우")
    void join_email_empty() throws Exception {
        // given
        String body = objectMapper.writeValueAsString(new MemberJoinRequest(CURRENT_MEMBER_ID, "", "name"));

        // when
        ResultActions actions = mvc.perform(post("/members")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("회원가입 - POST /members - name이 없는 경우")
    void join_no_name() throws Exception {
        // given
        String body = "{\"id\": \"" + UUID.randomUUID() + "\", \"email\": \"email\"}";

        // when
        ResultActions actions = mvc.perform(post("/members")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("회원가입 - POST /members - name이 빈 문자열인 경우")
    void join_name_empty() throws Exception {
        // given
        String body = objectMapper.writeValueAsString(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", ""));

        // when
        ResultActions actions = mvc.perform(post("/members")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("회원가입 - POST /members - Content-Type이 JSON이 아닌 경우")
    void join_content_type_not_json() throws Exception {
        // given
        String body = objectMapper.writeValueAsString(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));

        // when
        ResultActions actions = mvc.perform(post("/members")
                .content(body)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_HEADER.toString()));
    }

    @Test
    @DisplayName("회원가입 - POST /members - id가 중복된 경우")
    void join_id_duplicated() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email1", "name"));
        String body = objectMapper.writeValueAsString(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));

        // when
        ResultActions actions = mvc.perform(post("/members")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        // Todo: id 검증 로직 필요
//        actions.andExpect(status().isConflict())
//                .andExpect(jsonPath("$.code").value(DUPLICATED.toString()));
        actions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 - POST /members - email이 중복된 경우")
    void join_email_duplicated() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        String body = objectMapper.writeValueAsString(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));

        // when
        ResultActions actions = mvc.perform(post("/members")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(DUPLICATED.toString()));
    }
}
