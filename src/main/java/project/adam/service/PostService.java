package project.adam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.entity.post.*;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.post.PostRepository;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.service.dto.post.PostReportRequest;
import project.adam.service.dto.post.PostUpdateRequest;
import project.adam.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ImageUtils imageUtils;

    @Value("${report.hiddenCount}")
    private int reportHiddenCount;

    @Transactional
    public Post create(Member member, PostCreateRequest postDto, MultipartFile[] images) throws IOException {
        Post savedPost = postRepository.save(new Post(
                member,
                Board.valueOf(postDto.getBoard()),
                postDto.getTitle(),
                postDto.getBody()
        ));

        createImages(images, savedPost);
        if (postDto.getThumbnailIndex() != null) {
            String imageName = savedPost.getImages().get(postDto.getThumbnailIndex()).getName();
            MultipartFile image = images[postDto.getThumbnailIndex()];
            createThumbnail(imageName, image, savedPost);
        }
        return savedPost;
    }

    @Transactional
    public Post find(Long postId) {
        validationPostHidden(postId);
        return postRepository.findPostIncViewCount(postId).orElseThrow();
    }

    public Slice<Post> findAll(PostFindCondition condition, Pageable pageable) {
        return postRepository.findAll(condition, pageable);
    }

    private void createImages(MultipartFile[] images, Post post) throws IOException {
        for (MultipartFile image : images) {
            File imageFile = imageUtils.createImageFile(image);
            new PostImage(post, imageFile.getName());
        }
    }

    private void createThumbnail(String originImageName, MultipartFile image, Post post) throws IOException {
        File thumbnailFile = imageUtils.createThumbnailFile(originImageName, image);
        new PostThumbnail(post, thumbnailFile.getName());
    }

    @Transactional
    public void update(Long postId, PostUpdateRequest postDto, MultipartFile[] images) throws IOException {
        validationPostHidden(postId);
        Post findPost = postRepository.findById(postId).orElseThrow();
        findPost.update(postDto.getTitle(), postDto.getBody());

        removeImageFiles(findPost);
        removeImageDatas(findPost);
        removeThumbnailFile(findPost);
        removeThumbnailData(findPost);

        createImages(images, findPost);
        if (postDto.getThumbnailIndex() != null) {
            String imageName = findPost.getImages().get(postDto.getThumbnailIndex()).getName();
            MultipartFile image = images[postDto.getThumbnailIndex()];
            createThumbnail(imageName, image, findPost);
        }
    }

    @Transactional
    public void remove(Long postId) {
        validationPostHidden(postId);

        List<Comment> comments = commentRepository.findAllByPost(postRepository.findById(postId).orElseThrow());
        commentRepository.deleteAll(comments);

        Post findPost = postRepository.findById(postId).orElseThrow();

        removeImageFiles(findPost);
        removeThumbnailFile(findPost);
        postRepository.delete(findPost);
    }

    private void removeImageFiles(Post post) {
        for (PostImage image : post.getImages()) {
            imageUtils.removeImageFile(image.getName());
        }
    }

    private void removeImageDatas(Post post) {
        for (PostImage image : post.getImages()) {
            postRepository.deleteImageById(image.getId());
        }
        post.getImages().clear();
    }

    private void removeThumbnailFile(Post post) {
        imageUtils.removeImageFile(post.getThumbnailName());
    }

    private void removeThumbnailData(Post post) {
        postRepository.deleteThumbnailById(post.getThumbnail().getId());
    }

    @Transactional
    public void createReport(Member member, Long postId, PostReportRequest request) {
        Post post = postRepository.findById(postId).orElseThrow();

        boolean isReportExist = post.getReports().stream()
                .anyMatch(postReport -> postReport.getMember().equals(member));

        if (isReportExist) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }
        new PostReport(post, member, ReportType.valueOf(request.getReportType()));
    }

    private void validationPostHidden(Long postId) {
        if (postRepository.countPostReportById(postId) >= reportHiddenCount) {
            throw new ApiException(ExceptionEnum.AUTHORIZATION_FAILED);
        }
    }
}
