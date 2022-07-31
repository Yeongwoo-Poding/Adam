package project.adam.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private LocalDateTime createDate;

    private LocalDateTime lastModifiedDate;

    private String body;

    public Comment(Member writer, Post post, String body) {
        this.writer = writer;
        this.post = post;
        this.body = body;
        post.getComments().add(this);
    }

    public void update(String body) {
        this.body = body;
    }
}