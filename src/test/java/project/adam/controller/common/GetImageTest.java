package project.adam.controller.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import project.adam.controller.CommentController;
import project.adam.entity.member.Member;
import project.adam.service.MemberService;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;

import java.io.FileInputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureWebMvc
public class GetImageTest {

    @Autowired MockMvc mvc;
    @Autowired CommentController commentController;

    @Autowired MemberService memberService;
    @Autowired WebApplicationContext webApplicationContext;

    private static final String CURRENT_MEMBER_ID = "8351d242-366c-43d5-9afd-4fea1dd38f17";
    private static final String FILE_PATH = "src/test/resources/image/testimage.png";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("이미지 불러오기 - GET /image/{image}")
    void image() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        memberService.saveImage(member, new MockMultipartFile("name", "name", "image/png", new FileInputStream(FILE_PATH)));
        String image = memberService.findByEmail("email").getImage();
        System.out.println("image = " + image);

        // when
        ResultActions actions = mvc.perform(get("/image/" + image));

        // then
        actions.andExpect(status().isOk());
    }
}
