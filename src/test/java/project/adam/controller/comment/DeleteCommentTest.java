package project.adam.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;
import project.adam.controller.CommentController;
import project.adam.controller.MemberController;
import project.adam.controller.PostController;
import project.adam.entity.comment.Comment;
import project.adam.entity.member.Member;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.exception.ApiExceptionAdvice;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.PostService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.member.MemberLoginRequest;
import project.adam.service.dto.post.PostCreateRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static project.adam.exception.ExceptionEnum.INVALID_TYPE;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class DeleteCommentTest {

    @Autowired MockMvc mvc;
    @Autowired MemberController memberController;
    @Autowired PostController postController;
    @Autowired CommentController commentController;

    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired CommentService commentService;
    @Autowired ObjectMapper objectMapper;

    private static final String CURRENT_MEMBER_ID = "8351d242-366c-43d5-9afd-4fea1dd38f17";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(memberController, postController, commentController)
                .setControllerAdvice(new ApiExceptionAdvice())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("댓글 삭제 - DELETE /comments/{commentId}")
    void delete_comment() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));

        // when
        ResultActions actions = mvc.perform(delete("/comments/" + comment.getId()));

        // then
        actions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "email", password = CURRENT_MEMBER_ID)
    @DisplayName("댓글 삭제 - DELETE /comments/{commentId} - commentId가 문자인 경우")
    void delete_comment_id_is_string() throws Exception {
        // given
        Member member = memberService.join(new MemberJoinRequest(CURRENT_MEMBER_ID, "email", "name"));
        memberService.login(new MemberLoginRequest(CURRENT_MEMBER_ID, "email", "deviceToken"));
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"));
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));

        // when
        ResultActions actions = mvc.perform(delete("/comments/" + "not_number"));

        // then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(INVALID_TYPE.toString()));
    }
}
