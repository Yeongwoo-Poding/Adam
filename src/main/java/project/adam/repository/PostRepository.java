package project.adam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.adam.entity.Member;
import project.adam.entity.Post;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select distinct p from Post p left join fetch p.comments")
    List<Post> findAll();
    List<Post> findAllByWriter(Member writer);
}
