package project.adam;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Privilege;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.PostService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.post.PostCreateRequest;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitData {

    private final Init init;

    @PostConstruct
    public void post() {
        init.createDummyData();
    }

    @Component
    @RequiredArgsConstructor
    @Transactional
    static class Init {

        private final MemberService memberService;
        private final PostService postService;
        private final CommentService commentService;
        private final EntityManager em;

        public void createDummyData() {
            String member1Id = memberService.join(new MemberJoinRequest("id1", "member1", Privilege.ADMIN));
            String member2Id = memberService.join(new MemberJoinRequest("id2", "member2"));

            Long post1Id = postService.create(member1Id, new PostCreateRequest("FREE", "post1", "post body 1"));
            Long post2Id = postService.create(member1Id, new PostCreateRequest("FREE", "post2", "post body 2"));
            Long post3Id = postService.create(member2Id, new PostCreateRequest("FREE", "post3", "post body 3"));
            Long post4Id = postService.create(member2Id, new PostCreateRequest("FREE", "post3", "post body 4"));

            commentService.create(member1Id, post1Id, new CommentCreateRequest("comment body 1"));
            commentService.create(member2Id, post1Id, new CommentCreateRequest("comment body 2"));
            commentService.create(member1Id, post2Id, new CommentCreateRequest("comment body 3"));
            commentService.create(member2Id, post2Id, new CommentCreateRequest("comment body 4"));
            commentService.create(member1Id, post1Id, new CommentCreateRequest("comment body 5"));
            commentService.create(member2Id, post1Id, new CommentCreateRequest("comment body 6"));
            commentService.create(member1Id, post2Id, new CommentCreateRequest("comment body 7"));
            commentService.create(member2Id, post2Id, new CommentCreateRequest("comment body 8"));
            commentService.create(member1Id, post3Id, new CommentCreateRequest("comment body 9"));
            commentService.create(member2Id, post3Id, new CommentCreateRequest("comment body 10"));
            commentService.create(member1Id, post4Id, new CommentCreateRequest("comment body 11"));
            commentService.create(member2Id, post4Id, new CommentCreateRequest("comment body 12"));
            commentService.create(member1Id, post3Id, new CommentCreateRequest("comment body 13"));
            commentService.create(member2Id, post3Id, new CommentCreateRequest("comment body 14"));
            commentService.create(member1Id, post4Id, new CommentCreateRequest("comment body 15"));
            commentService.create(member2Id, post4Id, new CommentCreateRequest("comment body 16"));

            em.flush();
            em.clear();
        }
    }
}
