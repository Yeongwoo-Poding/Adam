package project.adam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import project.adam.utils.image.ImageUtils;

import java.io.File;

@Slf4j
@Profile({"local", "dev", "test"})
@Component
@RequiredArgsConstructor
public class DeInit {

    @Value("${file.dir}")
    File imageFilePath;

    private final ImageUtils imageUtils;

    @EventListener(ContextClosedEvent.class)
    public void preDestroy() {
        imageUtils.removeAll();
    }
}
