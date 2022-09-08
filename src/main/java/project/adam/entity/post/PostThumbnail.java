package project.adam.entity.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostThumbnail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_thumbnail_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String name;

    public PostThumbnail(Post post, String name) {
        this.post = post;
        this.name = name;
        post.setThumbnail(this);
    }
}
