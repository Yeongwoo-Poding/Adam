package project.adam.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue
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

    private String body;

    private int viewCount;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
    private PostThumbnail thumbnail = null;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostReport> reports = new ArrayList<>();

    public Post(Member writer, Board board, String title, String body) {
        this.writer = writer;
        this.board = board;
        this.title = title;
        this.body = body;
        this.viewCount = 0;
        writer.getPosts().add(this);
    }
    
    public void update(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public void setThumbnail(PostThumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailName() {
        return (thumbnail == null) ? null : thumbnail.getName();
    }
}
