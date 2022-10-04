package project.adam.controller.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;
import project.adam.controller.CommonController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class GetReportTest {

    @Autowired MockMvc mvc;
    @Autowired CommonController commonController;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(commonController)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 한글 깨짐 처리
                .build();
    }

    @Test
    @DisplayName("신고 리스트 불러오기 - GET /info/reports")
    void report() throws Exception {
        // given when
        ResultActions actions = mvc.perform(get("/info/reports")
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk());
    }
}
