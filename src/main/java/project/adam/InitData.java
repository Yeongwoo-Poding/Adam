package project.adam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.PostService;

import javax.persistence.EntityManager;
import java.io.File;

@Profile({"local", "dev"})
@Component
@RequiredArgsConstructor
public class InitData {

    private final DevClass dev;

    @EventListener(ContextClosedEvent.class)
    public void preDestroy() {
        dev.deleteImageFiles();
    }

    @Slf4j
    @Component
    @RequiredArgsConstructor
    @Transactional
    static class DevClass {

        @Value("${file.dir}")
        File imageFilePath;

        public void deleteImageFiles() {
            if (!imageFilePath.exists()) {
                log.warn("[Deinit] 이미지 파일 경로가 없습니다.");
                return;
            }

            File[] files = imageFilePath.listFiles();
            for (File file : files) {
                String deleteFileName = file.getName();
                if (file.delete()) {
                    log.info("[Deinit] 파일 삭제 {}", deleteFileName);
                }
            }
        }
    }
}
