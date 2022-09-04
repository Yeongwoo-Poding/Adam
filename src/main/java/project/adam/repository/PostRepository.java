package project.adam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import project.adam.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Modifying
    @Query("delete from PostImage pi where pi.id = :imageId")
    void deleteImageById(Long imageId);

    @Modifying
    @Query("delete from PostThumbnail pt where pt.id = :thumbnailId")
    void deleteThumbnailById(Long thumbnailId);
}
