package project.adam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.adam.entity.Member;
import project.adam.entity.Post;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
}
