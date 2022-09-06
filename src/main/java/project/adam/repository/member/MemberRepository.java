package project.adam.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import project.adam.entity.member.Member;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByToken(UUID token);

    Optional<Member> findByEmail(String email);
}
