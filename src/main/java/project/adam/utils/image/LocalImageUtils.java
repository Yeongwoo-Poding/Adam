package project.adam.utils.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Profile({"test"})
public class LocalImageUtils implements ImageUtils {

    private static final int THUMBNAIL_WIDTH = 200;
    private static final int THUMBNAIL_HEIGHT = 200;

    @Value("${image.path}")
    private String imagePath;

    @PostConstruct
    private File getImagePath() {
        File path = new File(imagePath);
        if (!path.exists()) {
            path.mkdir();
        }
        return path;
    }

    public String createImageName(MultipartFile image) {
        return UUID.randomUUID() + "." + getExtension(image);
    }

    public void createImageFile(String imageName, MultipartFile image) {
        try {
            File imageFile = new File(imagePath + imageName);
            image.transferTo(imageFile);
        } catch (IOException e) {
            throw new RuntimeException("이미지 생성 오류");
        }

    }

    public void createThumbnailFile(String imageName, String originImageName, MultipartFile originalImage) {
        try {
            File originalFile = new File(imagePath + originImageName);
            BufferedImage bufferedImage = resizeImage(ImageIO.read(originalFile));

            File createdImage = new File(imagePath + imageName);
            ImageIO.write(bufferedImage, getExtension(originalImage), createdImage);
        } catch (IOException e) {
            throw new RuntimeException("썸네일 생성 오류");
        }
    }

    public void removeImageFile(String imageName) {
        if (imageName == null) {
            return;
        }

        File removeFile = new File(imagePath + imageName);
        if (!removeFile.delete()) {
            log.warn("[ImageUtils] 이미지 {}가 삭제되지 않았습니다.", imageName);
        }
    }

    public void removeAll() {
        File imageDir = new File(imagePath);
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }

        File[] files = imageDir.listFiles();
        for (File file : files) {
            String deleteFileName = file.getName();
            if (file.delete()) {
                log.info("[DeInit] 파일 삭제 {}", deleteFileName);
            }
        }
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

    private BufferedImage resizeImage(BufferedImage originalImage) {
        BufferedImage croppedImage = getSquareImage(originalImage);

        BufferedImage resizedImage = new BufferedImage(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(croppedImage, 0, 0, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, null);
        graphics2D.dispose();
        return resizedImage;
    }

    private BufferedImage getSquareImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width > height) {
            int offset = (width - height) / 2;
            return image.getSubimage(offset, 0, height, height);
        } else {
            int offset = (height - width) / 2;
            return image.getSubimage(0, offset, width, width);
        }
    }
}
