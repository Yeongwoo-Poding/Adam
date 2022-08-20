package project.adam.aop;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import project.adam.aop.aspect.LogTraceAspect;

@Configuration
@RequiredArgsConstructor
public class AopConfig {
    @Bean
    public LogTraceAspect logTraceAspect() {
        return new LogTraceAspect();
    }
}
