package project.adam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.adam.entity.Privilege;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.PostService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.post.PostCreateRequest;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Profile({"local", "dev"})
@Component
@RequiredArgsConstructor
public class InitData {

    private final DevClass dev;

    @PostConstruct
    public void post() throws IOException {
        dev.createDummyData();
    }

    @EventListener(ContextClosedEvent.class)
    public void preDestroy() {
        dev.deleteImageFiles();
    }

    @Slf4j
    @Component
    @RequiredArgsConstructor
    @Transactional
    static class DevClass {

        private final MemberService memberService;
        private final PostService postService;
        private final CommentService commentService;
        private final EntityManager em;

        @Value("${file.dir}")
        File imageFilePath;

        public void createDummyData() throws IOException {

            log.info("Create dummy data");
            UUID member1Id = UUID.randomUUID();
            UUID member2Id = UUID.randomUUID();
            UUID member1Token = memberService.join(new MemberJoinRequest(member1Id.toString(), "member1", Privilege.ADMIN));
            UUID member2Token = memberService.join(new MemberJoinRequest(member2Id.toString(), "member2"));

            List<Long> postsId = createPosts(member1Id, member2Id);
            List<Long> commentsId = createComments(member1Id, member2Id, postsId);
            List<Long> repliesId = createReplies(member1Id, member2Id, postsId, commentsId);

            em.flush();
            em.clear();
        }

        private List<Long> createPosts(UUID member1Id, UUID member2Id) throws IOException {
            List<Long> postsId = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                postsId.add(postService.create(
                        i / 2 >= 1 ? member1Id : member2Id,
                        new PostCreateRequest("FREE", "post" + i, "post body" + i),
                        new MultipartFile[]{}).getId()
                );
            }
            return postsId;
        }

        private List<Long> createComments(UUID member1Id, UUID member2Id, List<Long> postsId) {
            List<Long> commentsId = new ArrayList<>();
            for (int i = 0; i < 16; i++) {
                commentsId.add(commentService.create(
                        i % 2 == 0 ? member1Id : member2Id,
                        postsId.get(i / 4),
                        null,
                        new CommentCreateRequest("comment body " + i)
                ));
            }

            return commentsId;
        }

        private List<Long> createReplies(UUID member1Id, UUID member2Id, List<Long> postsId, List<Long> commentsId) {
            List<Long> repliesId = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                repliesId.add(commentService.create(
                        i % 2 == 0 ? member1Id : member2Id,
                        postsId.get(i / 8),
                        commentsId.get(i / 2),
                        new CommentCreateRequest("replies " + i)));
            }

            return repliesId;
        }

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
