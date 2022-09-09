package project.adam.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import project.adam.entity.post.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Modifying
    @Query("delete from PostImage pi where pi.id = :imageId")
    void deleteImageById(Long imageId);

    @Modifying
    @Query("delete from PostThumbnail pt where pt.id = :thumbnailId")
    void deleteThumbnailById(Long thumbnailId);

    @Query("select count(pr) from PostReport pr where pr.post.id = :postId")
    int countPostReportById(Long postId);
}
