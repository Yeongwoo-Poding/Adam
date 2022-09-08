package project.adam.entity.reply;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.BaseTimeEntity;
import project.adam.entity.member.Member;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    private String body;

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL)
    private List<ReplyReport> reports;

    public Reply(Member writer, Comment comment, String body) {
        this.writer = writer;
        this.comment = comment;
        this.body = body;
    }

    public void update(String body) {
        this.body = body;
    }
}
