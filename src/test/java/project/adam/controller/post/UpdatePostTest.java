package project.adam.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartFile;
import project.adam.controller.MemberController;
import project.adam.controller.PostController;
import project.adam.entity.member.Member;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.exception.ApiExceptionAdvice;
import project.adam.service.MemberService;
import project.adam.service.PostService;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostUpdateRequest;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static project.adam.exception.ExceptionEnum.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UpdatePostTest {

    @Autowired MockMvc mvc;
    @Autowired MemberController memberController;
    @Autowired PostController postController;

    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired ObjectMapper objectMapper;

    private static final String CURRENT_MEMBER_ID = "8351d242-366c-43d5-9afd-4fea1dd38f17";
    private static final String PNG_FILE_PATH = "src/test/resources/image/testimage.png";
    private static final String JPEG_FILE_PATH = "src/test/resources/image/testimage.jpeg";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(memberController, postController)
                .setControllerAdvice(new ApiExceptionAdvice())
                .addFilters(new CharacterEncodingFilter())
                .build();
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시글 수정 - PUT /posts/{postId}")
    void update() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        MultipartFile[] multipartFiles = new MockMultipartFile[]{new MockMultipartFile("name", "name", "image/png", new FileInputStream(PNG_FILE_PATH))};

        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), multipartFiles);

        MockPart pngImage = new MockPart("images", PNG_FILE_PATH, Files.readAllBytes(Path.of(PNG_FILE_PATH)));
        pngImage.getHeaders().setContentType(IMAGE_PNG);

        MockPart jpegImage = new MockPart("images", JPEG_FILE_PATH, Files.readAllBytes(Path.of(JPEG_FILE_PATH)));
        jpegImage.getHeaders().setContentType(IMAGE_JPEG);

        String jsonData = objectMapper.writeValueAsString(new PostUpdateRequest("title", "body"));
        MockPart data = new MockPart("data", jsonData.getBytes(UTF_8));
        data.getHeaders().setContentType(APPLICATION_JSON);

        // when
        ResultActions actions = mvc.perform(multipart(HttpMethod.PUT, "/posts/" + post.getId())
                .part(pngImage)
                .part(jpegImage)
                .part(data)
                .contentType(MULTIPART_FORM_DATA)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시글 수정 - PUT /posts/{postId} - Data의 형태가 Json 형식이 아닌 경우")
    void update_data_not_json() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        MultipartFile[] multipartFiles = new MockMultipartFile[]{new MockMultipartFile("name", "name", "image/png", new FileInputStream(PNG_FILE_PATH))};

        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), multipartFiles);

        MockPart pngImage = new MockPart("images", PNG_FILE_PATH, Files.readAllBytes(Path.of(PNG_FILE_PATH)));
        pngImage.getHeaders().setContentType(IMAGE_PNG);

        MockPart jpegImage = new MockPart("images", JPEG_FILE_PATH, Files.readAllBytes(Path.of(JPEG_FILE_PATH)));
        jpegImage.getHeaders().setContentType(IMAGE_JPEG);

        String jsonData = "{\"title\": \"title\", \"body\": \"body\",}";
        MockPart data = new MockPart("data", jsonData.getBytes(UTF_8));
        data.getHeaders().setContentType(APPLICATION_JSON);

        // when
        ResultActions actions = mvc.perform(multipart(HttpMethod.PUT, "/posts/" + post.getId())
                .part(pngImage)
                .part(jpegImage)
                .part(data)
                .contentType(MULTIPART_FORM_DATA)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_JSON_FORMAT.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시글 수정 - PUT /posts/{postId} - title이 존재하지 않는 경우")
    void update_title_not_exist() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        MultipartFile[] multipartFiles = new MockMultipartFile[]{new MockMultipartFile("name", "name", "image/png", new FileInputStream(PNG_FILE_PATH))};

        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), multipartFiles);

        MockPart pngImage = new MockPart("images", PNG_FILE_PATH, Files.readAllBytes(Path.of(PNG_FILE_PATH)));
        pngImage.getHeaders().setContentType(IMAGE_PNG);

        MockPart jpegImage = new MockPart("images", JPEG_FILE_PATH, Files.readAllBytes(Path.of(JPEG_FILE_PATH)));
        jpegImage.getHeaders().setContentType(IMAGE_JPEG);

        String jsonData = "{\"body\": \"body\"}";
        MockPart data = new MockPart("data", jsonData.getBytes(UTF_8));
        data.getHeaders().setContentType(APPLICATION_JSON);

        // when
        ResultActions actions = mvc.perform(multipart(HttpMethod.PUT, "/posts/" + post.getId())
                .part(pngImage)
                .part(jpegImage)
                .part(data)
                .contentType(MULTIPART_FORM_DATA)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시글 수정 - PUT /posts/{postId} - title이 비어있는 경우")
    void update_title_empty() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        MultipartFile[] multipartFiles = new MockMultipartFile[]{new MockMultipartFile("name", "name", "image/png", new FileInputStream(PNG_FILE_PATH))};

        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), multipartFiles);

        MockPart pngImage = new MockPart("images", PNG_FILE_PATH, Files.readAllBytes(Path.of(PNG_FILE_PATH)));
        pngImage.getHeaders().setContentType(IMAGE_PNG);

        MockPart jpegImage = new MockPart("images", JPEG_FILE_PATH, Files.readAllBytes(Path.of(JPEG_FILE_PATH)));
        jpegImage.getHeaders().setContentType(IMAGE_JPEG);

        String jsonData = "{\"title\": \"\", \"body\": \"body\"}";
        MockPart data = new MockPart("data", jsonData.getBytes(UTF_8));
        data.getHeaders().setContentType(APPLICATION_JSON);

        // when
        ResultActions actions = mvc.perform(multipart(HttpMethod.PUT, "/posts/" + post.getId())
                .part(pngImage)
                .part(jpegImage)
                .part(data)
                .contentType(MULTIPART_FORM_DATA)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_INPUT.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시글 수정 - PUT /posts/{postId} - postId가 문자인 경우")
    void update_post_id_string() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        MultipartFile[] multipartFiles = new MockMultipartFile[]{new MockMultipartFile("name", "name", "image/png", new FileInputStream(PNG_FILE_PATH))};

        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), multipartFiles);

        MockPart pngImage = new MockPart("images", PNG_FILE_PATH, Files.readAllBytes(Path.of(PNG_FILE_PATH)));
        pngImage.getHeaders().setContentType(IMAGE_PNG);

        MockPart jpegImage = new MockPart("images", JPEG_FILE_PATH, Files.readAllBytes(Path.of(JPEG_FILE_PATH)));
        jpegImage.getHeaders().setContentType(IMAGE_JPEG);

        String jsonData = objectMapper.writeValueAsString(new PostUpdateRequest("title", "body"));
        MockPart data = new MockPart("data", jsonData.getBytes(UTF_8));
        data.getHeaders().setContentType(APPLICATION_JSON);

        // when
        ResultActions actions = mvc.perform(multipart(HttpMethod.PUT, "/posts/" + "not_number")
                .part(pngImage)
                .part(jpegImage)
                .part(data)
                .contentType(MULTIPART_FORM_DATA)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_TYPE.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시글 수정 - PUT /posts/{postId} - data의 Content-Type이 application/json이 아닌 경우")
    void update_data_not_application_json() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        MultipartFile[] multipartFiles = new MockMultipartFile[]{new MockMultipartFile("name", "name", "image/png", new FileInputStream(PNG_FILE_PATH))};

        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), multipartFiles);

        MockPart pngImage = new MockPart("images", PNG_FILE_PATH, Files.readAllBytes(Path.of(PNG_FILE_PATH)));
        pngImage.getHeaders().setContentType(IMAGE_PNG);

        MockPart jpegImage = new MockPart("images", JPEG_FILE_PATH, Files.readAllBytes(Path.of(JPEG_FILE_PATH)));
        jpegImage.getHeaders().setContentType(IMAGE_JPEG);

        String jsonData = objectMapper.writeValueAsString(new PostUpdateRequest("title", "body"));
        MockPart data = new MockPart("data", jsonData.getBytes(UTF_8));

        // when
        ResultActions actions = mvc.perform(multipart(HttpMethod.PUT, "/posts/" + post.getId())
                .part(pngImage)
                .part(jpegImage)
                .part(data)
                .contentType(MULTIPART_FORM_DATA)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_HEADER.toString()));
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("게시글 수정 - PUT /posts/{postId} - images의 확장자가 jpeg나 png가 아닌 경우")
    void update_images_not_json_png() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));

        MultipartFile[] multipartFiles = new MockMultipartFile[]{new MockMultipartFile("name", "name", "image/png", new FileInputStream(PNG_FILE_PATH))};

        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), multipartFiles);

        MockPart pngImage = new MockPart("images", PNG_FILE_PATH, Files.readAllBytes(Path.of(PNG_FILE_PATH)));

        MockPart jpegImage = new MockPart("images", JPEG_FILE_PATH, Files.readAllBytes(Path.of(JPEG_FILE_PATH)));
        jpegImage.getHeaders().setContentType(IMAGE_JPEG);

        String jsonData = objectMapper.writeValueAsString(new PostUpdateRequest("title", "body"));
        MockPart data = new MockPart("data", jsonData.getBytes(UTF_8));

        // when
        ResultActions actions = mvc.perform(multipart(HttpMethod.PUT, "/posts/" + post.getId())
                .part(pngImage)
                .part(jpegImage)
                .part(data)
                .contentType(MULTIPART_FORM_DATA)
                .accept(APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_HEADER.toString()));
    }
}
