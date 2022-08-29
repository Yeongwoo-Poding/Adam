package project.adam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.adam.entity.Board;
import project.adam.entity.Comment;
import project.adam.entity.Post;
import project.adam.entity.PostImage;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.CommentRepository;
import project.adam.repository.MemberRepository;
import project.adam.repository.PostRepository;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.service.dto.post.PostUpdateRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Value("${file.dir}")
    private String imagePath;

    @Transactional
    public Post create(UUID token, PostCreateRequest postDto, MultipartFile[] images) throws IOException {
        Post savedPost = postRepository.save(new Post(
                memberRepository.findById(token).orElseThrow(),
                Board.valueOf(postDto.getBoard()),
                postDto.getTitle(),
                postDto.getBody()
        ));

        createImages(images, savedPost);
        return savedPost;
    }

    private String getExtension(MultipartFile file) {
        String contentType = file.getContentType();
        String fileExtension;
        if (contentType == null) {
            throw new ApiException(ExceptionEnum.INVALID_HEADER);
        }

        if (contentType.equals("image/png")) {
            fileExtension = ".png";
        } else if (contentType.equals("image/jpeg")) {
            fileExtension = ".jpeg";
        } else {
            throw new ApiException(ExceptionEnum.INVALID_HEADER);
        }
        return fileExtension;
    }

    @Transactional
    public void update(Long postId, PostUpdateRequest postDto, MultipartFile[] images) throws IOException {
        Post findPost = postRepository.findById(postId).orElseThrow();
        findPost.update(postDto.getTitle(), postDto.getBody());

        removeImageFiles(findPost);
        removeImagePaths(findPost);
        createImages(images, findPost);
    }

    @Transactional
    public void remove(Long postId) {
        List<Comment> comments = commentRepository.findAllByPost(postRepository.findById(postId).orElseThrow());
        commentRepository.deleteAll(comments);

        Post findPost = postRepository.findById(postId).orElseThrow();

        removeImageFiles(findPost);
        postRepository.delete(findPost);
    }

    @Transactional
    public Post find(Long postId) {
        Post findPost = postRepository.findById(postId).orElseThrow();
        findPost.view();
        return findPost;
    }

    public Slice<Post> findAll(PostFindCondition condition, Pageable pageable) {
        return postRepository.findAll(condition, pageable);
    }

    private void createImages(MultipartFile[] images, Post findPost) throws IOException {
        if (images != null) {
            for (MultipartFile image : images) {
                createImage(findPost, image);
            }
        }
    }

    private void createImage(Post savedPost, MultipartFile image) throws IOException {
        String extension = getExtension(image);
        String imageName = UUID.randomUUID() + extension;
        File newImage = new File(imagePath + imageName);
        image.transferTo(newImage);
        PostImage postImage = new PostImage(savedPost, imageName);
        log.info("[{}.createImage()] Add image {}", getClass(), postImage.getName());
    }

    private void removeImageFiles(Post findPost) {
        for (PostImage image : findPost.getImages()) {
            removeImageFile(image.getName());
        }
    }

    private void removeImagePaths(Post findPost) {
        for (PostImage image : findPost.getImages()) {
            removeImagePath(image.getId());
        }
    }

    private void removeImageFile(String imageName) {
        log.info("imageName = {}", imageName);
        File file = new File(imagePath + imageName);
        if (!file.delete()) {
            log.warn( "[{}.removeImage] Image has not been deleted.", getClass().getName());
        }
    }

    private void removeImagePath(Long imageId) {
        log.info("imageId = {}", imageId);
        postRepository.deleteImageById(imageId);
    }
}
