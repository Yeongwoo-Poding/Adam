package project.adam.entity.comment;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.common.BaseTimeEntity;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.member.Member;
import project.adam.entity.post.Post;
import project.adam.entity.reply.Reply;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String body;

    @Enumerated(EnumType.STRING)
    private ContentStatus status;

    @OneToMany(mappedBy = "comment")
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentReport> reports = new ArrayList<>();

    @Builder
    public Comment(Member writer, Post post, String body) {
        this.writer = writer;
        this.post = post;
        this.body = body;
        this.status = ContentStatus.PUBLISHED;
        post.getComments().add(this);
    }

    public void update(String body) {
        this.body = body;
    }
}
