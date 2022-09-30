package project.adam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.Report;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.entity.post.Post;
import project.adam.entity.post.PostImage;
import project.adam.entity.post.PostReport;
import project.adam.entity.post.PostThumbnail;
import project.adam.entity.reply.Reply;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.post.PostRepository;
import project.adam.repository.reply.ReplyRepository;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.service.dto.post.PostUpdateRequest;
import project.adam.utils.image.ImageUtils;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final ImageUtils imageUtils;

    @Transactional
    public Post create(Member member, PostCreateRequest request)  {
        Post createdPost = Post.builder()
                .writer(member)
                .board(request.getBoard())
                .title(request.getTitle())
                .body(request.getBody())
                .build();

        return postRepository.save(createdPost);
    }

    @Transactional
    public Post create(Member member, PostCreateRequest request, MultipartFile[] images)  {
        Post createdPost = Post.builder()
                .writer(member)
                .board(request.getBoard())
                .title(request.getTitle())
                .body(request.getBody())
                .build();

        createImages(images, createdPost);
        String imageName = createdPost.getImages().get(0).getName();
        MultipartFile image = images[0];
        createThumbnail(imageName, image, createdPost);

        return postRepository.save(createdPost);
    }

    public Post find(Long postId) {
        validatePostHidden(postId);
        return postRepository.findById(postId).orElseThrow();
    }

    @Transactional
    public Post showPost(Long postId) {
        validatePostHidden(postId);
        return postRepository.showPost(postId).orElseThrow();
    }

    public Slice<Post> findPosts(PostFindCondition condition, Pageable pageable) {
        return postRepository.findPosts(condition, pageable);
    }

    @Transactional
    public void update(Post post, PostUpdateRequest request, MultipartFile[] images)  {
        validatePostHidden(post.getId());

        post.update(request.getTitle(), request.getBody());

        removeImageFiles(post);
        removeImageDatas(post);
        removeThumbnailFile(post);
        removeThumbnailData(post);

        if (images != null) {
            createImages(images, post);
            String imageName = post.getImages().get(0).getName();
            MultipartFile image = images[0];
            createThumbnail(imageName, image, post);
        }
    }

    @Transactional
    public void remove(Post post) {
        validatePostHidden(post.getId());

        deleteAllCommentsAndReplies(post);
        removeImageFiles(post);
        removeThumbnailFile(post);
        postRepository.delete(post);
    }

    private void deleteAllCommentsAndReplies(Post post) {
        List<Comment> comments = post.getComments();
        for (Comment comment : comments) {
            List<Reply> replies = comment.getReplies();
            replyRepository.deleteAll(replies);
        }
        commentRepository.deleteAll(comments);
    }

    private void createImages(MultipartFile[] images, Post post) {
        for (MultipartFile image : images) {
            File imageFile = imageUtils.createImageFile(image);
            PostImage.builder()
                    .post(post)
                    .name(imageFile.getName())
                    .build();
        }
    }

    private void createThumbnail(String originImageName, MultipartFile image, Post post) {
        File thumbnailFile = imageUtils.createThumbnailFile(originImageName, image);

        PostThumbnail.builder()
                .post(post)
                .name(thumbnailFile.getName())
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
    public void report(Member member, Post post, ReportType type) {
        if (member.equals(post.getWriter())) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }
        if (isReportExist(member, post)) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }

        PostReport.builder()
                .post(post)
                .member(member)
                .reportType(type)
                .build();
    }

    private boolean isReportExist(Member member, Post post) {
        return post.getReports().stream()
                .anyMatch(postReport -> postReport.getMember().equals(member));
    }

    private void validatePostHidden(Long postId) {
        if (postRepository.countPostReportById(postId) >= Report.HIDE_COUNT) {
            throw new ApiException(ExceptionEnum.HIDDEN_CONTENT);
        }
    }
}
