package project.adam.utils.image;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
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
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Profile({"prod", "dev"})
public class S3ImageUtils implements ImageUtils {

    private static final int THUMBNAIL_WIDTH = 200;
    private static final int THUMBNAIL_HEIGHT = 200;

    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void setS3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }

    public File createImageFile(MultipartFile image) throws IOException {
        String imageName = UUID.randomUUID() + "." + getExtension(image);

        s3Client.putObject(new PutObjectRequest(bucket, imageName, image.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return new File(s3Client.getUrl(bucket, imageName).toString());
    }

    public File createThumbnailFile(String originImageName, MultipartFile originalImage) throws IOException {
        BufferedImage originImage = ImageIO.read(originalImage.getInputStream());
        BufferedImage resizedImage = resizeImage(originImage);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, getExtension(originalImage), os);

        InputStream is = new ByteArrayInputStream(os.toByteArray());
        String imageName = UUID.randomUUID() + "." + getExtension(originalImage);

        s3Client.putObject(new PutObjectRequest(bucket, imageName, is, null));
        return new File(s3Client.getUrl(bucket, imageName).toString());
    }

    public void removeImageFile(String imageName) {
        if (imageName == null) {
            return;
        }
        s3Client.deleteObject(new DeleteObjectRequest(bucket, imageName));
    }

    public void removeAll() {
        ListObjectsRequest listObjectsRequest =
                new ListObjectsRequest()
                        .withBucketName(bucket);

        List<String> keys = new ArrayList<>();

        ObjectListing objects = s3Client.listObjects(listObjectsRequest);

        while (true) {
            List<S3ObjectSummary> objectSummaries = objects.getObjectSummaries();
            if (objectSummaries.size() < 1) {
                break;
            }

            for (S3ObjectSummary item : objectSummaries) {
                if (!item.getKey().endsWith("/"))
                    keys.add(item.getKey());
            }

            objects = s3Client.listNextBatchOfObjects(objects);
        }

        keys.forEach(key -> s3Client.deleteObject(new DeleteObjectRequest(bucket, key)));
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
