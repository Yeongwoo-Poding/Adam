package project.adam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.adam.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUuid(String uuid);
}
