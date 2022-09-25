package project.adam.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.entity.reply.Reply;
import project.adam.exception.ApiException;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.service.dto.post.PostReportRequest;
import project.adam.service.dto.post.PostUpdateRequest;
import project.adam.service.dto.reply.ReplyCreateRequest;
import project.adam.utils.image.ImageUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class PostServiceTest {

    @Autowired MemberService memberService;
    @Autowired PostService postService;
    @Autowired CommentService commentService;
    @Autowired ReplyService replyService;
    @Autowired ImageUtils imageUtils;
    @PersistenceContext EntityManager em;

    @Value("${image.path}")
    String imagePath;

    @BeforeEach
    void before_each() {
        imageUtils.removeAll();
    }

    @Test
    @DisplayName("Post 생성")
    void create_post() {
        // given
        Member member = createMember();

        // when
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), null);

        // then
        assertThat(postService.find(post.getId())).isEqualTo(post);
        assertThat(post.getImages().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("이미지가 포함된 Post 생성")
    void create_post_with_image() throws IOException {
        // given
        Member member = createMember();
        MultipartFile[] images = new MockMultipartFile[]{new MockMultipartFile("name", "originName", "image/png", getFileInputStream())};

        // when
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), images);

        // then
        assertThat(postService.find(post.getId())).isEqualTo(post);
        assertThat(post.getImages().size()).isEqualTo(1);
        assertThat(getImage(post.getImages().get(0).getName()).exists()).isTrue();
    }

    @Test
    @DisplayName("Post Board가 존재하지 않는 경우 오류")
    void create_post_no_board() {
        // given when then
        assertThatThrownBy(() -> postService.create(createMember(), new PostCreateRequest(Board.valueOf("NO_BOARD"), "title", "body"), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Find Post시 조회수가 증가하지 않음")
    void find_post() {
        // given
        Post post = postService.create(createMember(), new PostCreateRequest(Board.FREE, "title", "body"), null);

        // when
        Post findPost = postService.find(post.getId());

        // then
        assertThat(findPost.getViewCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Show Post시 조회수가 증가")
    void show_post() {
        // given
        Post post = postService.create(createMember(), new PostCreateRequest(Board.FREE, "title", "body"), null);
        clearPersistenceContext();

        // when
        Post findPost = postService.showPost(post.getId());

        // then
        assertThat(findPost.getViewCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Post 리스트 조회 - 조건 x")
    void find_posts_no_condition() {
        // given
        Member member = createMember();
        postService.create(member, new PostCreateRequest(Board.FREE, "titleA", "bodyB"), null);
        postService.create(member, new PostCreateRequest(Board.FREE, "titleB", "bodyA"), null);
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleA", "bodyB"), null);
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleB", "bodyA"), null);
        Pageable pageable = PageRequest.of(0, 20);

        // when
        List<Post> posts = postService.findPosts(new PostFindCondition(null, null), pageable).getContent();

        // then
        assertThat(posts.size()).isEqualTo(4);
        assertThat(posts.stream().map(Post::getTitle).collect(Collectors.toList()))
                .containsExactly("titleB", "titleA", "titleB", "titleA");
    }

    @Test
    @DisplayName("Post 리스트 조회 - Board 조건")
    void find_posts_board_condition() {
        // given
        Member member = createMember();
        postService.create(member, new PostCreateRequest(Board.FREE, "titleA", "bodyB"), null);
        postService.create(member, new PostCreateRequest(Board.FREE, "titleB", "bodyA"), null);
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleA", "bodyB"), null);
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleB", "bodyA"), null);
        Pageable pageable = PageRequest.of(0, 20);

        // when
        List<Post> posts = postService.findPosts(new PostFindCondition(Board.FREE, null), pageable).getContent();

        // then
        assertThat(posts.size()).isEqualTo(2);
        assertThat(posts.stream().map(Post::getTitle).collect(Collectors.toList()))
                .containsExactly("titleB", "titleA");
    }

    @Test
    @DisplayName("Post 리스트 조회 - content 조건 - title")
    void find_posts_content_condition_title() {
        // given
        Member member = createMember();
        postService.create(member, new PostCreateRequest(Board.FREE, "titleA", "bodyB"), null);
        postService.create(member, new PostCreateRequest(Board.FREE, "titleB", "bodyA"), null);
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleA", "bodyB"), null);
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleB", "bodyA"), null);
        Pageable pageable = PageRequest.of(0, 20);

        // when
        List<Post> posts = postService.findPosts(new PostFindCondition(null, "titleA"), pageable).getContent();

        // then
        assertThat(posts.size()).isEqualTo(2);
        assertThat(posts.stream().map(Post::getTitle).collect(Collectors.toList()))
                .containsExactly("titleA", "titleA");
    }

    @Test
    @DisplayName("Post 리스트 조회 - content 조건 - body")
    void find_posts_content_condition_body() {
        // given
        Member member = createMember();
        postService.create(member, new PostCreateRequest(Board.FREE, "titleA", "bodyB"), null);
        postService.create(member, new PostCreateRequest(Board.FREE, "titleB", "bodyA"), null);
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleA", "bodyB"), null);
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleB", "bodyA"), null);
        Pageable pageable = PageRequest.of(0, 20);

        // when
        List<Post> posts = postService.findPosts(new PostFindCondition(null, "bodyA"), pageable).getContent();

        // then
        assertThat(posts.size()).isEqualTo(2);
        assertThat(posts.stream().map(Post::getTitle).collect(Collectors.toList()))
                .containsExactly("titleB", "titleB");
    }

    @Test
    @DisplayName("Post 리스트 조회 - 모든 조건")
    void find_posts_all_condition() {
        // given
        Member member = createMember();
        postService.create(member, new PostCreateRequest(Board.FREE, "titleA", "bodyB"), null);
        postService.create(member, new PostCreateRequest(Board.FREE, "titleB", "bodyA"), null);
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleA", "bodyB"), null);
        postService.create(member, new PostCreateRequest(Board.QUESTION, "titleB", "bodyA"), null);
        Pageable pageable = PageRequest.of(0, 20);

        // when
        List<Post> posts = postService.findPosts(new PostFindCondition(Board.QUESTION, "bodyA"), pageable).getContent();

        // then
        assertThat(posts.size()).isEqualTo(1);
        assertThat(posts.stream().map(Post::getTitle).collect(Collectors.toList()))
                .containsExactly("titleB");
    }

    @Test
    @DisplayName("Post 수정")
    void update() {
        // given
        Post post = postService.create(createMember(), new PostCreateRequest(Board.FREE, "title", "body"), null);

        // when
        postService.update(post, new PostUpdateRequest("updatedTitle", "updatedBody"), null);

        // then
        assertThat(post.getTitle()).isEqualTo("updatedTitle");
        assertThat(post.getBody()).isEqualTo("updatedBody");
    }

    @Test
    @DisplayName("Post 수정시 이전 이미지 삭제 후 새 이미지 생성")
    void update_change_images() throws IOException {
        // given
        MultipartFile[] images = new MockMultipartFile[]{new MockMultipartFile("name", "originName", "image/png", getFileInputStream())};
        Post post = postService.create(createMember(), new PostCreateRequest(Board.FREE, "title", "body"), images);
        String beforeImageName = post.getImages().get(0).getName();
        File beforeImage = getImage(beforeImageName);

        // when
        MultipartFile[] updatedImages = new MockMultipartFile[]{new MockMultipartFile("updatedName", "updatedOriginName", "image/png", getFileInputStream())};
        postService.update(post, new PostUpdateRequest("updatedTitle", "updatedBody"), updatedImages);

        // then
        String imageName = post.getImages().get(0).getName();
        assertThat(imageName.equals(beforeImageName)).isFalse();
        assertThat(beforeImage.exists()).isFalse();
        assertThat(getImage(imageName).exists()).isTrue();
    }

    @Test
    @DisplayName("Post 삭제")
    void remove_post() {
        // given
        Post post = postService.create(createMember(), new PostCreateRequest(Board.FREE, "title", "body"), null);

        // when
        postService.remove(post);

        // then
        assertThatThrownBy(() -> postService.find(post.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("Post 삭제시 하위 댓글 삭제")
    void remove_post_remove_comments() {
        // given
        Member member = createMember();
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), null);
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));

        // when
        postService.remove(post);

        // then
        assertThatThrownBy(() -> commentService.find(comment.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("Post 삭제시 하위 대댓글 삭제")
    void remove_post_remove_replies() {
        // given
        Member member = createMember();
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), null);
        Comment comment = commentService.create(member, new CommentCreateRequest(post.getId(), "body"));
        Reply reply = replyService.create(member, new ReplyCreateRequest(comment.getId(), "body"));

        // when
        postService.remove(post);

        // then
        assertThatThrownBy(() -> replyService.find(reply.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("Post 삭제시 PostImage 삭제")
    void remove_post_remove_images() throws IOException {
        // given
        MultipartFile[] images = new MockMultipartFile[]{new MockMultipartFile("name", "originName", "image/png", getFileInputStream())};
        Post post = postService.create(createMember(), new PostCreateRequest(Board.FREE, "title", "body"), images);
        File image = new File(post.getImages().get(0).getName());

        // when
        postService.remove(post);

        // then
        assertThat(image.exists()).isFalse();
    }

    @Test
    @DisplayName("PostReport 생성")
    void report() {
        // given
        Member member = createMember();
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), null);
        Member reportMember = createMember("reportId", "reportEmail");

        // when
        postService.report(reportMember, post, new PostReportRequest(ReportType.BAD));

        // then
        assertThat(post.getReports().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("한명의 Member가 하나의 Post에 여러 번 Report 하는 경우 오류")
    void report_duplicate() {
        // given
        Member member = createMember();
        Member reportMember = createMember("reportId", "reportEmail");
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), null);
        postService.report(reportMember, post, new PostReportRequest(ReportType.INAPPROPRIATE));

        // when then
        assertThatThrownBy(() -> postService.report(reportMember, post, new PostReportRequest(ReportType.BAD)))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("Post가 Report를 5번 이상 받으면 숨김")
    void hide_post() {
        // given
        Member member = createMember();
        Post post = postService.create(member, new PostCreateRequest(Board.FREE, "title", "body"), null);
        createFiveReports(post);

        // when then
        assertThatThrownBy(() -> postService.find(post.getId()))
                .isInstanceOf(ApiException.class);
    }

    private void createFiveReports(Post post) {
        for (int i = 0; i < 5; i++) {
            Member reportMember = createMember("id" + i, "email" + i);
            postService.report(reportMember, post, new PostReportRequest(ReportType.BAD));
        }
    }

    private Member createMember() {
        memberService.join(new MemberJoinRequest("id", "email", "name"));
        return memberService.findByEmail("email");
    }

    private Member createMember(String id, String email) {
        memberService.join(new MemberJoinRequest(id, email, "name"));
        return memberService.findByEmail(email);
    }

    @NotNull
    private FileInputStream getFileInputStream() {
        try {
            File image = new File("src/test/resources/image/testimage.png");
            return new FileInputStream(image);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("해당 파일이 없습니다");
        }
    }

    @NotNull
    private File getImage(String imageName) {
        return new File(imagePath + imageName);
    }

    private void clearPersistenceContext() {
        em.flush();
        em.clear();
    }
}
