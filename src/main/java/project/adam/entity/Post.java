package project.adam.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

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

    private LocalDateTime createDate;

    private LocalDateTime lastModifiedDate;

    private String title;

    private String body;

    public Post(Member writer, Board board, String title, String body) {
        this.writer = writer;
        this.board = board;
        this.title = title;
        this.body = body;
        writer.getPosts().add(this);
    }
    
    public void update(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
