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
import project.adam.service.dto.member.MemberLoginRequest;

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
public class LoginTest {

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
    @DisplayName("로그인 - POST /members/login")
    void login() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        String body = objectMapper.writeValueAsString(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        // when
        ResultActions actions = mvc.perform(post("/members/login")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("로그인 - POST /members/login - Input의 형태가 JSON이 아닌 경우")
    void login_input_not_json() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        String body = "{\"id\": \"" + CURRENT_MEMBER_ID + "\", \"email\": \"email\", \"deviceToken\": \"deviceToken\",}";

        // when
        ResultActions actions = mvc.perform(post("/members/login")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_JSON_FORMAT.toString()));
    }

    @Test
    @DisplayName("로그인 - POST /members/login - id가 존재하지 않는 경우")
    void login_no_id() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        String body = "{\"email\": \"email\", \"deviceToken\": \"deviceToken\"}";

        // when
        ResultActions actions = mvc.perform(post("/members/login")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("로그인 - POST /members/login - id가 빈 문자열인 경우")
    void login_id_empty() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        String body = objectMapper.writeValueAsString(new MemberLoginRequest("", "email", "deviceToken"));

        // when
        ResultActions actions = mvc.perform(post("/members/login")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("로그인 - POST /members/login - email이 없는 경우")
    void login_no_email() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        String body = "{\"id\": \"" + UUID.randomUUID() + "\", \"deviceToken\": \"deviceToken\"}";

        // when
        ResultActions actions = mvc.perform(post("/members/login")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("로그인 - POST /members/login - email이 빈 문자열인 경우")
    void login_email_empty() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        String body = objectMapper.writeValueAsString(new MemberLoginRequest(CURRENT_MEMBER_ID, "", "deviceToken"));

        // when
        ResultActions actions = mvc.perform(post("/members/login")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("로그인 - POST /members/login - deviceToken이 없는 경우")
    void login_no_device_token() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        String body = "{\"id\": \"" + UUID.randomUUID() + "\", \"email\": \"email\"}";

        // when
        ResultActions actions = mvc.perform(post("/members/login")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("로그인 - POST /members/login - deviceToken이 빈 문자열인 경우")
    void login_device_token_empty() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        String body = objectMapper.writeValueAsString(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", ""));

        // when
        ResultActions actions = mvc.perform(post("/members/login")
                .content(body)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("로그인 - POST /members/login - Content-Type이 JSON이 아닌 경우")
    void login_content_type_not_json() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        String body = objectMapper.writeValueAsString(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        // when
        ResultActions actions = mvc.perform(post("/members/login")
                .content(body)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_HEADER.toString()));
    }
}
