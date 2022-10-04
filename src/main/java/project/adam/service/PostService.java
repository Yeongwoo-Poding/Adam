package project.adam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.adam.controller.dto.request.post.PostListFindCondition;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.common.Report;
import project.adam.entity.common.ReportContent;
import project.adam.entity.member.Authority;
import project.adam.entity.member.Member;
import project.adam.entity.post.*;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.member.MemberRepository;
import project.adam.repository.post.PostRepository;
import project.adam.repository.reply.ReplyRepository;
import project.adam.security.SecurityUtils;
import project.adam.service.dto.post.PostCreateServiceRequest;
import project.adam.service.dto.post.PostReportServiceRequest;
import project.adam.service.dto.post.PostUpdateServiceRequest;
import project.adam.utils.image.ImageUtils;
import project.adam.utils.push.PushUtils;
import project.adam.utils.push.dto.PushRequest;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;

import static project.adam.entity.common.ReportContent.ContentType.POST;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final EntityManager em;
    private final ImageUtils imageUtils;
    private final PushUtils pushUtils;

    @Transactional
    public Post create(PostCreateServiceRequest request)  {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow();

        Post post = Post.builder()
                .writer(member)
                .board(request.getBoard())
                .title(request.getTitle())
                .body(request.getBody())
                .build();

        if (post.getBoard().equals(Board.NOTICE)) {
            if (member.getAuthority().equals(Authority.ROLE_ADMIN)) {
                pushUtils.pushAll(new PushRequest(post.getTitle(), post.getBody(), post.getId()));
            } else {
                throw new ApiException(ExceptionEnum.AUTHORIZATION_FAILED);
            }
        }

        return postRepository.save(post);
    }

    @Transactional
    public Post create(PostCreateServiceRequest request, MultipartFile[] images)  {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow();

        Post post = Post.builder()
                .writer(member)
                .board(request.getBoard())
                .title(request.getTitle())
                .body(request.getBody())
                .build();

        if (post.getBoard().equals(Board.NOTICE)) {
            if (member.getAuthority().equals(Authority.ROLE_ADMIN)) {
                pushUtils.pushAll(new PushRequest(post.getTitle(), post.getBody(), post.getId()));
            } else {
                throw new ApiException(ExceptionEnum.AUTHORIZATION_FAILED);
            }
        }

        createImages(images, post);
        String imageName = post.getImages().get(0).getName();
        MultipartFile image = images[0];
        createThumbnail(imageName, image, post);

        return postRepository.save(post);
    }

    @Transactional
    public Post find(Long postId) {
        Post post = postRepository.showPost(postId).orElseThrow();
        validatePostStatus(post);
        return post;
    }

    public Slice<Post> findPosts(PostListFindCondition condition, Pageable pageable) {
        return postRepository.findPosts(condition, pageable);
    }

    public Slice<Comment> findComments(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId).orElseThrow();
        validatePostStatus(post);
        return commentRepository.findByPost(post, pageable);
    }

    @Transactional
    public void update(PostUpdateServiceRequest request)  {
        Post post = postRepository.findById(request.getPostId()).orElseThrow();
        validatePostStatus(post);
        authorization(post.getWriter());

        post.update(request.getTitle(), request.getBody());

        removeImageFiles(post);
        removeImageDatas(post);
        removeThumbnailFile(post);
        removeThumbnailData(post);
    }

    @Transactional
    public void update(PostUpdateServiceRequest request, MultipartFile[] images)  {
        Post post = postRepository.findById(request.getPostId()).orElseThrow();
        validatePostStatus(post);
        authorization(post.getWriter());

        post.update(request.getTitle(), request.getBody());

        removeImageFiles(post);
        removeImageDatas(post);
        removeThumbnailFile(post);
        removeThumbnailData(post);

        createImages(images, post);
        String imageName = post.getImages().get(0).getName();
        MultipartFile image = images[0];
        createThumbnail(imageName, image, post);
    }

    @Transactional
    public void remove(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        validatePostStatus(post);
        authorization(post.getWriter());

        deleteAllCommentsAndReplies(post);
        removeImageFiles(post);
        removeThumbnailFile(post);
        postRepository.remove(post);
    }

    private void deleteAllCommentsAndReplies(Post post) {
        List<Comment> comments = post.getComments();
        for (Comment comment : comments) {
            replyRepository.removeAllByComment(comment);
        }
        commentRepository.removeAllByPost(post);
    }

    private void createImages(MultipartFile[] images, Post post) {
        for (MultipartFile image : images) {
            String imageName = imageUtils.createImageName(image);
            imageUtils.createImageFile(imageName, image);
            PostImage.builder()
                    .post(post)
                    .name(imageName)
                    .build();
        }
    }

    private void createThumbnail(String originImageName, MultipartFile image, Post post) {
        String thumbnailName = imageUtils.createImageName(image);
        imageUtils.createThumbnailFile(thumbnailName, originImageName, image);

        PostThumbnail.builder()
                .post(post)
                .name(thumbnailName)
                .build();
    }

    private void removeImageFiles(Post post) {
        if (post.getImages() == null) {
            return;
        }
        for (PostImage image : post.getImages()) {
            imageUtils.removeImageFile(image.getName());
        }
    }

    private void removeImageDatas(Post post) {
        if (post.getImages() == null) {
            return;
        }
        for (PostImage image : post.getImages()) {
            postRepository.deletePostImageById(image.getId());
        }
        post.getImages().clear();
    }

    private void removeThumbnailFile(Post post) {
        if (post.getThumbnail() == null) {
            return;
        }
        imageUtils.removeImageFile(post.getThumbnail().getName());
    }

    private void removeThumbnailData(Post post) {
        if (post.getThumbnail() == null) {
            return;
        }
        postRepository.deletePostThumbnailById(post.getThumbnail().getId());
    }

    @Transactional
    public void report(PostReportServiceRequest request) {
        Post post = postRepository.findById(request.getPostId()).orElseThrow();
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow();

        if (post.getBoard().equals(Board.NOTICE)) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }
        if (request.getReportType() == null) {
            throw new ApiException(ExceptionEnum.INVALID_INPUT);
        }
        if (member.equals(post.getWriter())) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }
        if (isReportExist(member, post)) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }

        PostReport.builder()
                .post(post)
                .member(member)
                .reportType(request.getReportType())
                .build();

        if (postRepository.countPostReport(post) >= Report.HIDE_COUNT) {
            postRepository.hide(post);
            em.persist(new ReportContent(POST, post.getId()));
        }
    }

    private void validatePostStatus(Post post) {
        if (post.getStatus().equals(ContentStatus.HIDDEN)) {
            throw new ApiException(ExceptionEnum.HIDDEN_CONTENT);
        }
        if (post.getStatus().equals(ContentStatus.REMOVED)) {
            throw new NoSuchElementException();
        }
    }

    private boolean isReportExist(Member member, Post post) {
        return post.getReports().stream()
                .anyMatch(postReport -> postReport.getMember().equals(member));
    }

    public void authorization(Member member) {
        Member loginMember = memberRepository.findByEmail(SecurityUtils.getCurrentMemberEmail()).orElseThrow();
        loginMember.authorization(member);
    }
}
