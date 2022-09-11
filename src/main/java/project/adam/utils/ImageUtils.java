package project.adam.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class ImageUtils {

    @Value("${file.dir}")
    private String imagePath;

    @Value("${thumbnail.width}")
    private int thumbnailWidth;

    @Value("${thumbnail.height}")
    private int thumbnailHeight;

    public File createImageFile(MultipartFile image) throws IOException {
        String imageName = UUID.randomUUID() + "." + getExtension(image);
        File imageFile = new File(imagePath + imageName);
        image.transferTo(imageFile);
        return imageFile;
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

    public File createThumbnailFile(String originImageName, MultipartFile originalImage) throws IOException {
        File originalFile = new File(imagePath + originImageName);
        BufferedImage bufferedImage = resizeImage(ImageIO.read(originalFile));

        String imageName = UUID.randomUUID() + "." + getExtension(originalImage);
        File createdImage = new File(imagePath + imageName);
        ImageIO.write(bufferedImage, getExtension(originalImage), createdImage);
        return createdImage;
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

        BufferedImage resizedImage = new BufferedImage(thumbnailWidth, thumbnailHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(croppedImage, 0, 0, thumbnailWidth, thumbnailHeight, null);
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
