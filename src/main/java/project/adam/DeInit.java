package project.adam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Profile({"local", "dev", "test"})
@Component
@RequiredArgsConstructor
public class DeInit {

    @Value("${file.dir}")
    File imageFilePath;

    @EventListener(ContextClosedEvent.class)
    public void preDestroy() {
        deleteImageFiles();
    }

    public void deleteImageFiles() {
        if (!imageFilePath.exists()) {
            log.warn("[DeInit] 이미지 파일 경로가 없습니다.");
            return;
        }

        File[] files = imageFilePath.listFiles();
        for (File file : files) {
            String deleteFileName = file.getName();
            if (file.delete()) {
                log.info("[DeInit] 파일 삭제 {}", deleteFileName);
            }
        }
    }
}
