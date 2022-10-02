package project.adam.entity.post;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.BaseTimeEntity;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.member.Member;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private Board board;

    private String title;

    @Column(length = 1000)
    private String body;

    @Enumerated(EnumType.STRING)
    private ContentStatus status;

    private int viewCount;

    @OneToOne(mappedBy = "post", cascade = CascadeType.PERSIST)
    private PostThumbnail thumbnail = null;

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostReport> reports = new ArrayList<>();

    @Builder
    public Post(Member writer, Board board, String title, String body) {
        this.writer = writer;
        this.board = board;
        this.title = title;
        this.body = body;
        this.viewCount = 0;
        this.status = ContentStatus.PUBLISHED;
        writer.getPosts().add(this);
    }
    
    public void update(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public void setThumbnail(PostThumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }
}
