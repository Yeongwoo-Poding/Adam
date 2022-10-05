package project.adam.repository.post;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import project.adam.entity.member.Member;
import project.adam.entity.post.Post;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Modifying
    @Query("delete from PostImage pi where pi.id = :imageId")
    void deletePostImageById(Long imageId);

    @Modifying
    @Query("delete from PostThumbnail pt where pt.id = :thumbnailId")
    void deletePostThumbnailById(Long thumbnailId);

    @Query("select count(pr) from PostReport pr where pr.post = :post and pr.isChecked = false")
    int countPostReport(Post post);

    @Query("select p from Post p where p.writer = :writer order by p.id desc")
    Slice<Post> findPostsByWriter(Member writer, Pageable pageable);

    @Query("select distinct c.post from Comment c join fetch c.post.thumbnail where c.writer = :writer order by c.post.id desc")
    Slice<Post> findPostsByCommentWriter(Member writer, Pageable pageable);

    @NotNull
    @Query("select p from Post p join fetch p.writer where p.id = :id")
    Optional<Post> findById(@NotNull Long id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Post p set p.status = project.adam.entity.common.ContentStatus.HIDDEN where p = :post")
    void hide(Post post);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Post p set p.status = project.adam.entity.common.ContentStatus.REMOVED where p = :post")
    void remove(Post post);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Post p set p.status = project.adam.entity.common.ContentStatus.PUBLISHED where p = :post")
    void release(Post post);
}
