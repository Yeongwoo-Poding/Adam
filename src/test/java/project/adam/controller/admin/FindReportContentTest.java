package project.adam.controller.admin;

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
import project.adam.admin.AdminController;
import project.adam.admin.AdminService;
import project.adam.entity.common.Report;
import project.adam.entity.common.ReportContent;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Authority;
import project.adam.entity.member.Member;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.exception.ApiExceptionAdvice;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.member.MemberRepository;
import project.adam.service.MemberService;
import project.adam.service.PostService;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;
import project.adam.service.dto.post.PostCreateRequest;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class FindReportContentTest {

    @Autowired MockMvc mvc;
    @Autowired AdminController adminController;

    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired AdminService adminService;
    @Autowired MemberRepository memberRepository;
    @Autowired ObjectMapper objectMapper;

    private static final String USERNAME = "admin";

    private static final String CURRENT_MEMBER_ID = "8351d242-366c-43d5-9afd-4fea1dd38f17";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(adminController)
                .setControllerAdvice(new ApiExceptionAdvice())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        Member member = memberRepository.save(new Member(CURRENT_MEMBER_ID, USERNAME, "name", Authority.ROLE_ADMIN));
        member.login("deviceToken");
    }

    @Test
    @WithMockUser(username = USERNAME, password = CURRENT_MEMBER_ID, authorities = "ROLE_ADMIN")
    @DisplayName("신고 대상 조회 - GET /reports/{reportId}")
    void report_content() throws Exception {
        // given
        String uuid = UUID.randomUUID().toString();
        Member member = memberService.join(new MemberJoinRequest(uuid, "email", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email", "deviceToken"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            postService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), post, ReportType.BAD);
        }
        ReportContent content = adminService.findReportContents().get(0);

        // when
        ResultActions actions = mvc.perform(get("/reports/" + content.getId())
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.contentType").value("POST"))
                .andExpect(jsonPath("$.contentId").value(post.getId()));
    }

    @Test
    @WithMockUser(username = USERNAME, password = CURRENT_MEMBER_ID, authorities = "ROLE_ADMIN")
    @DisplayName("신고 대상 조회 - GET /reports/{reportId} - reportId가 문자인 경우")
    void report_content_id_is_string() throws Exception {
        // given
        String uuid = UUID.randomUUID().toString();
        Member member = memberService.join(new MemberJoinRequest(uuid, "email", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email", "deviceToken"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            postService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), post, ReportType.BAD);
        }

        // when
        ResultActions actions = mvc.perform(get("/reports/not_number")
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ExceptionEnum.INVALID_TYPE.toString()));
    }

    @Test
    @WithMockUser(username = USERNAME, password = CURRENT_MEMBER_ID, authorities = "ROLE_ADMIN")
    @DisplayName("신고 대상 조회 - GET /reports/{reportId} - reportId가 존재하지 않는 경우")
    void report_content_not_exist() throws Exception {
        // given
        String uuid = UUID.randomUUID().toString();
        Member member = memberService.join(new MemberJoinRequest(uuid, "email", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email", "deviceToken"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));

        // when
        ResultActions actions = mvc.perform(get("/reports/1")
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ExceptionEnum.NO_DATA.toString()));
    }
}
