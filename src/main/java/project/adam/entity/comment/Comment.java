package project.adam.entity.comment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.common.BaseTimeEntity;
import project.adam.entity.member.Member;
import project.adam.entity.post.Post;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private List<Comment> replies = new ArrayList<>();

    private String body;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentReport> reports = new ArrayList<>();

    public Comment(Member writer, Post post, Comment parent, String body) {
        this.writer = writer;
        this.post = post;
        this.body = body;
        this.parent = parent;
    }

    public void update(String body) {
        this.body = body;
    }
}
