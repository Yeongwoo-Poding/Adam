package project.adam.utils.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface ImageUtils {

    String createImageName(MultipartFile image);
    void createImageFile(String imageName, MultipartFile image);
    void createThumbnailFile(String imageName, String originImageName, MultipartFile originalImage);
    void removeImageFile(String imageName);
    void removeAll();
}
