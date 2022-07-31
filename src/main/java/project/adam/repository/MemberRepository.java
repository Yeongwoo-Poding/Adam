package project.adam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.adam.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
