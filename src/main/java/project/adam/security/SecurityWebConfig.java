package project.adam.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import project.adam.exception.custom.CustomAccessDeniedHandler;
import project.adam.exception.custom.CustomAuthenticationEntryPoint;
import project.adam.repository.member.MemberRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityWebConfig {

    private final TokenProvider tokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final MemberRepository memberRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public RoleHierarchy roleHierarchy() {
//        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
//        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
//        return roleHierarchy;
//    }
//
//    @Bean
//    public DefaultWebSecurityExpressionHandler handler() {
//        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
//        handler.setRoleHierarchy(roleHierarchy());
//        return handler;
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()

                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
                .and()

                .authorizeRequests()
                .anyRequest().permitAll()
//                .expressionHandler(handler())
                .and()

                .headers()
                .frameOptions()
                .sameOrigin()
                .and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .addFilterBefore(new SecurityFilter(tokenProvider, memberRepository), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
