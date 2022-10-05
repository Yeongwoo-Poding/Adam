package project.adam.entity.comment;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.common.BaseTimeEntity;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.member.Member;
import project.adam.entity.post.Post;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    private String body;

    @Enumerated(EnumType.STRING)
    private ContentStatus status;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentReport> reports = new ArrayList<>();

    @Builder
    public Comment(Comment parent, Member writer, Post post, String body) {
        this.parent = parent;
        this.writer = writer;
        this.post = post;
        this.body = body;
        this.status = ContentStatus.PUBLISHED;
        post.getComments().add(this);
    }

    public void update(String body) {
        this.body = body;
    }

    public boolean isRoot() {
        return this.parent == null;
    }
}
