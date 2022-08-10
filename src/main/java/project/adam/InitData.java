package project.adam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Comment;
import project.adam.entity.Post;
import project.adam.entity.Privilege;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.PostService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.post.PostCreateRequest;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitData {

    private final Init init;

    @PostConstruct
    public void post() {
        init.createDummyData();
    }

    @Slf4j
    @Component
    @RequiredArgsConstructor
    @Transactional
    static class Init {

        private final MemberService memberService;
        private final PostService postService;
        private final CommentService commentService;
        private final EntityManager em;

        public void createDummyData() {

            log.info("Create dummy data");
            String member1Id = memberService.join(new MemberJoinRequest("id1", "member1", Privilege.ADMIN));
            String member2Id = memberService.join(new MemberJoinRequest("id2", "member2"));

            List<Long> postsId = createPosts(member1Id, member2Id);
            List<Long> commentsId = createComments(member1Id, member2Id, postsId);
            List<Long> replysId = createReplys(member1Id, member2Id, postsId, commentsId);

            em.flush();
            em.clear();

            Post post1 = postService.find(postsId.get(0));
            List<Comment> post1Comments = post1.getComments();
        }

        private List<Long> createPosts(String member1Id, String member2Id) {
            List<Long> postsId = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                postsId.add(postService.create(
                        i / 2 >= 1 ? member1Id : member2Id,
                        new PostCreateRequest("FREE", "post" + i, "post body" + i)
                ));
            }
            return postsId;
        }

        private List<Long> createComments(String member1Id, String member2Id, List<Long> postsId) {
            List<Long> commentsId = new ArrayList<>();
            for (int i = 0; i < 16; i++) {
                commentsId.add(commentService.create(
                        i % 2 == 0 ? member1Id : member2Id,
                        postsId.get(i / 4),
                        new CommentCreateRequest("comment body " + i)
                ));
            }

            return commentsId;
        }

        private List<Long> createReplys(String member1Id, String member2Id, List<Long> postsId, List<Long> commentsId) {
            List<Long> replysId = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                replysId.add(commentService.create(
                        i % 2 == 0 ? member1Id : member2Id,
                        postsId.get(i / 8),
                        new CommentCreateRequest(commentsId.get(i / 2), "reply " + i)));
            }

            return replysId;
        }
    }
}
