package project.adam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.adam.entity.Member;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findBySessionId(UUID sessionId);
}
