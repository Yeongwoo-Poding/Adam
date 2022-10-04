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
import project.adam.entity.common.ContentStatus;
import project.adam.entity.common.Report;
import project.adam.entity.common.ReportContent;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.entity.member.MemberStatus;
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
import project.adam.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static project.adam.exception.ExceptionEnum.INVALID_TYPE;
import static project.adam.exception.ExceptionEnum.NO_DATA;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ReleaseTest {

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
    }

    @Test
    @WithMockUser(username = USERNAME, password = CURRENT_MEMBER_ID, authorities = "ROLE_ADMIN")
    @DisplayName("신고 해제 - POST /reports/{reportId}/release")
    void release() throws Exception {
        // given
        String uuid = UUID.randomUUID().toString();
        Member member = memberService.join(new MemberJoinRequest(uuid, "email", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email", "deviceToken"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            postService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), post, ReportType.BAD);
        }
        List<ReportContent> contents = adminService.findReportContents();
        ReportContent content = contents.get(0);

        // when
        ResultActions actions = mvc.perform(delete("/reports/" + content.getId() + "/release"));

        // then
        actions.andExpect(status().isOk());
        assertThat(memberService.findByEmail("email").getStatus()).isEqualTo(MemberStatus.LOGIN);
        assertThat(postService.find(post.getId()).getStatus()).isEqualTo(ContentStatus.PUBLISHED);
    }

    @Test
    @WithMockUser(username = USERNAME, password = CURRENT_MEMBER_ID, authorities = "ROLE_ADMIN")
    @DisplayName("신고 해제 - POST /reports/{reportId}/release - reportId가 문자인 경우")
    void release_report_id_is_string() throws Exception {
        // given
        String uuid = UUID.randomUUID().toString();
        Member member = memberService.join(new MemberJoinRequest(uuid, "email", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email", "deviceToken"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        for (int i = 0; i < Report.HIDE_COUNT; i++) {
            postService.report(memberService.join(new MemberJoinRequest(UUID.randomUUID().toString(), "email" + i, "name")), post, ReportType.BAD);
        }

        // when
        ResultActions actions = mvc.perform(delete("/reports/not_number/release"));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_TYPE.toString()));
    }

    @Test
    @WithMockUser(username = USERNAME, password = CURRENT_MEMBER_ID, authorities = "ROLE_ADMIN")
    @DisplayName("신고 해제 - POST /reports/{reportId}/release - reportId가 존재하지 않는 경우")
    void release_report_id_not_exist() throws Exception {
        // given
        String uuid = UUID.randomUUID().toString();
        Member member = memberService.join(new MemberJoinRequest(uuid, "email", "name"));
        memberService.login(new MemberLoginRequest(uuid, "email", "deviceToken"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));

        // when
        ResultActions actions = mvc.perform(delete("/reports/1/release"));

        // then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(NO_DATA.toString()));
    }
}
