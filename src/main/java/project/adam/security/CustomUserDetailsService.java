package project.adam.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.member.Member;
import project.adam.entity.member.MemberStatus;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.member.MemberRepository;

import java.util.Collections;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);
        if (member.getStatus().equals(MemberStatus.WITHDRAWN)) {
            throw new NoSuchElementException();
        } else if (member.getStatus().equals(MemberStatus.SUSPENDED)) {
            throw new ApiException(ExceptionEnum.SUSPENDED);
        }
        return createUserDetails(member);
    }

    private UserDetails createUserDetails(Member member) {
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getAuthority().toString());

        return new User(
                member.getEmail(),
                member.getId(),
                Collections.singleton(grantedAuthority)
        );
    }
}
