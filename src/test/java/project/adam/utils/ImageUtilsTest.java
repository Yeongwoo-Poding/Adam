package project.adam.utils;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import project.adam.exception.ApiException;
import project.adam.utils.image.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class ImageUtilsTest {

    @Autowired ImageUtils imageUtils;

    @Value("${image.path}")
    private String imagePath;

    private static final int THUMBNAIL_WIDTH = 200;
    private static final int THUMBNAIL_HEIGHT = 200;

    @Test
    @DisplayName("png 형식 이미지 생성")
    void create_image_png() throws IOException {
        // given
        MockMultipartFile multipartImage = new MockMultipartFile("name", "originName", "image/png", getFileInputStream("testimage.png"));

        // when
        String imageName = imageUtils.createImageName(multipartImage);
        imageUtils.createImageFile(imageName, multipartImage);
        File image = new File(imagePath + imageName);

        // then
        assertThat(image.exists()).isTrue();
    }

    @Test
    @DisplayName("jpeg 형식 이미지 생성")
    void create_image_jpeg() throws IOException {
        // given
        MockMultipartFile multipartImage = new MockMultipartFile("name", "originName", "image/jpeg", getFileInputStream("testimage.jpeg"));

        // when
        String imageName = imageUtils.createImageName(multipartImage);
        imageUtils.createImageFile(imageName, multipartImage);
        File image = new File(imagePath + imageName);

        // then
        assertThat(image.exists()).isTrue();
    }

    @Test
    @DisplayName("지원하지 않는 이미지 형식 입력시 오류")
    void create_image_other() throws IOException {
        // given
        MockMultipartFile multipartImage = new MockMultipartFile("name", "originName", "image/jpg", getFileInputStream("testimage.jpg"));

        // when then
        assertThatThrownBy(() -> imageUtils.createImageName(multipartImage))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("Thumbnail 생성")
    void create_thumbnail() throws IOException {
        // given
        MockMultipartFile multipartImage = new MockMultipartFile("name", "testimage.png", "image/png", getFileInputStream("testimage.png"));
        String imageName = imageUtils.createImageName(multipartImage);
        imageUtils.createImageFile(imageName, multipartImage);
        File image = new File(imagePath + imageName);

        // when
        String thumbnailName = imageUtils.createImageName(multipartImage);
        imageUtils.createThumbnailFile(thumbnailName, imageName, multipartImage);
        File thumbnailImage = new File(imagePath + thumbnailName);

        // then
        assertThat(thumbnailImage.exists()).isTrue();

        BufferedImage bufferedImage = ImageIO.read(thumbnailImage);
        assertThat(bufferedImage.getWidth()).isEqualTo(THUMBNAIL_WIDTH);
        assertThat(bufferedImage.getHeight()).isEqualTo(THUMBNAIL_HEIGHT);
    }

    @Test
    @DisplayName("Image 삭제")
    void remove_image() throws IOException {
        // given
        MockMultipartFile multipartImage = new MockMultipartFile("name", "testimage.png", "image/png", getFileInputStream("testimage.png"));
        String imageName = imageUtils.createImageName(multipartImage);
        imageUtils.createImageFile(imageName, multipartImage);
        File image = new File(imagePath + imageName);

        // when
        imageUtils.removeImageFile(image.getName());

        // then
        assertThat(image.exists()).isFalse();
    }

    @Test
    @DisplayName("Image 모두 삭제")
    void remove_all() throws IOException {
        // given
        MockMultipartFile multipartImage = new MockMultipartFile("name", "testimage.png", "image/png", getFileInputStream("testimage.png"));
        for (int i = 0; i < 5; i++) {
            String imageName = imageUtils.createImageName(multipartImage);
            imageUtils.createImageFile(imageName, multipartImage);
        }

        // when
        imageUtils.removeAll();

        // then
        File path = new File(imagePath);
        assertThat(path.listFiles().length).isEqualTo(0);
    }

    @NotNull
    private FileInputStream getFileInputStream(String imageName) {
        try {
            File image = new File("src/test/resources/image/" + imageName);
            return new FileInputStream(image);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("해당 파일이 없습니다");
        }
    }

}
