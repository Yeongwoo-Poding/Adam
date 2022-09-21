package project.adam.utils.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface ImageUtils {

    File createImageFile(MultipartFile image) throws IOException;

    File createThumbnailFile(String originImageName, MultipartFile originalImage) throws IOException;

    void removeImageFile(String imageName);

    void removeAll();
}
