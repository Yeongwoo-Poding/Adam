package project.adam.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImageConfig implements WebMvcConfigurer {

    @Value("${image.prefix}")
    private String imagePathPrefix;

    @Value("${image.path}")
    private String imagePath;

    private static final String CONNECT_PATH = "/image/**";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(CONNECT_PATH)
                .addResourceLocations(imagePathPrefix + imagePath);
    }
}
