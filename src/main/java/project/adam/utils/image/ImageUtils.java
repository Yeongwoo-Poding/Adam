package project.adam.utils.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface ImageUtils {

    File createImageFile(MultipartFile image);
    File createThumbnailFile(String originImageName, MultipartFile originalImage);
    void removeImageFile(String imageName);
    void removeAll();
}
