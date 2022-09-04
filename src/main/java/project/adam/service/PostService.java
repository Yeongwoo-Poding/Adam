package project.adam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.adam.entity.*;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.CommentRepository;
import project.adam.repository.MemberRepository;
import project.adam.repository.PostRepository;
import project.adam.service.dto.post.PostCreateRequest;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.service.dto.post.PostUpdateRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
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

        createImagesAndThumbnail(images, postDto.getThumbnailIndex(), savedPost);
        return savedPost;
    }

    private String getExtension(MultipartFile file) {
        String contentType = file.getContentType();
        String fileExtension;
        if (contentType == null) {
            throw new ApiException(ExceptionEnum.INVALID_HEADER);
        }

        if (contentType.equals("image/png")) {
            fileExtension = "png";
        } else if (contentType.equals("image/jpeg")) {
            fileExtension = "jpeg";
        } else {
            throw new ApiException(ExceptionEnum.INVALID_HEADER);
        }
        return fileExtension;
    }

    private String getExtension(File file) {
        return file.getName().substring(file.getName().indexOf(".") + 1);
    }

    @Transactional
    public void update(Long postId, PostUpdateRequest postDto, MultipartFile[] images) throws IOException {
        Post findPost = postRepository.findById(postId).orElseThrow();
        findPost.update(postDto.getTitle(), postDto.getBody());

        removeImageFiles(findPost);
        removeImagePaths(findPost);
        createImagesAndThumbnail(images, postDto.getThumbnailIndex(), findPost);
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
        return postRepository.findPostIncViews(postId).orElseThrow();
    }

    public Slice<Post> findAll(PostFindCondition condition, Pageable pageable) {
        return postRepository.findAll(condition, pageable);
    }

    private void createImagesAndThumbnail(MultipartFile[] images, Integer index, Post post) throws IOException {
        if (images == null) {
            return;
        }

        for (int i = 0; i < images.length; i++) {
            File image = createImage(images[i], post);
            if (i == index) {
                createThumbnail(image, post);
            }
        }
    }

    private void createThumbnail(File image, Post post) throws IOException {
        int width = 192;
        int height = 192;
        BufferedImage bufferedImage = resizeImage(ImageIO.read(image), width, height);

        String imageName = UUID.randomUUID() + "." + getExtension(image);
        ImageIO.write(bufferedImage, "png", new File(imagePath + imageName));
        PostThumbnail postThumbnail = new PostThumbnail(post, imageName);
        log.info("[{}.createThumbnail()] Add image {}", getClass(), postThumbnail.getName());
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    private File createImage(MultipartFile image, Post post) throws IOException {
        String extension = getExtension(image);
        String imageName = UUID.randomUUID() + "." + extension;
        File newImage = new File(imagePath + imageName);
        image.transferTo(newImage);
        PostImage postImage = new PostImage(post, imageName);
        log.info("[{}.createImage()] Add image {}", getClass(), postImage.getName());
        return newImage;
    }

    private void removeImageFiles(Post findPost) {
        for (PostImage image : findPost.getImages()) {
            removeImageFile(image.getName());
        }
        removeImageFile(findPost.getThumbnailName());
    }

    private void removeImagePaths(Post findPost) {
        for (PostImage image : findPost.getImages()) {
            removeImagePath(image.getId());
        }

        Long thumbnailId = findPost.getThumbnail().getId();
        if (thumbnailId != null) {
            removeThumbnailPath(thumbnailId);
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

    private void removeThumbnailPath(Long thumbnailId) {
        log.info("thumbnailId = {}", thumbnailId);
        postRepository.deleteThumbnailById(thumbnailId);
    }
}
