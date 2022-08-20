package project.adam.aop;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.adam.aop.aspect.LogTraceAspect;
import project.adam.repository.MemberRepository;

@Configuration
@RequiredArgsConstructor
public class AopConfig {

    private final MemberRepository repository;

    @Bean
    public LogTraceAspect logTraceAspect() {
        return new LogTraceAspect();
    }
}
