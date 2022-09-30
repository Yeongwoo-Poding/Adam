package project.adam.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
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

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static project.adam.exception.ExceptionEnum.INVALID_HEADER;
import static project.adam.exception.ExceptionEnum.INVALID_INPUT;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CreateImageTest {

    @Autowired MockMvc mvc;
    @Autowired MemberController memberController;

    @Autowired MemberService memberService;
    @Autowired ObjectMapper objectMapper;

    private static final String CURRENT_MEMBER_ID = "8351d242-366c-43d5-9afd-4fea1dd38f17";
    private static final String FILE_PATH = "src/test/resources/image/testimage.png";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(memberController)
                .setControllerAdvice(new ApiExceptionAdvice())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 한글 깨짐 처리
                .build();
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("프로필 이미지 추가 - POST /members/image")
    void create_image() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        MockPart part = new MockPart("image", FILE_PATH, Files.readAllBytes(Path.of(FILE_PATH)));
        part.getHeaders().setContentType(IMAGE_PNG);

        // when
        ResultActions actions = mvc.perform(multipart("/members/image")
                .part(part)
                .contentType(MULTIPART_FORM_DATA)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("프로필 이미지 추가 - POST /members/image - image가 없는 경우")
    void create_no_image() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        MockPart part = new MockPart("not_image", FILE_PATH, Files.readAllBytes(Path.of(FILE_PATH)));
        part.getHeaders().setContentType(IMAGE_PNG);

        // when
        ResultActions actions = mvc.perform(multipart("/members/image")
                .part(part)
                .contentType(MULTIPART_FORM_DATA)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("프로필 이미지 추가 - POST /members/image - image가 null인 경우")
    void create_image_is_null() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        MockPart part = new MockPart("image", FILE_PATH, null);
        part.getHeaders().setContentType(IMAGE_PNG);

        // when
        ResultActions actions = mvc.perform(multipart("/members/image")
                .part(part)
                .contentType(MULTIPART_FORM_DATA)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("프로필 이미지 추가 - POST /members/image - image의 확장자가 jpeg나 png가 아닌 경우")
    void create_image_invalid_ext() throws Exception {
        // given
        memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        MockPart part = new MockPart("image", FILE_PATH, Files.readAllBytes(Path.of(FILE_PATH)));
        part.getHeaders().setContentType(IMAGE_GIF);

        // when
        ResultActions actions = mvc.perform(multipart("/members/image")
                .part(part)
                .contentType(MULTIPART_FORM_DATA)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_HEADER.toString()));
    }
}
