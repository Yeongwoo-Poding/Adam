package project.adam.configure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import project.adam.exception.custom.CustomMethodException;
import project.adam.exception.custom.CustomNotFoundException;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.dir}")
    private String imagePath;
    private static final String CONNECT_PATH = "/image/**";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(0, new CustomNotFoundException(objectMapper()));
        resolvers.add(1, new CustomMethodException(objectMapper()));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(CONNECT_PATH)
                .addResourceLocations("file://" + imagePath);
    }
}
