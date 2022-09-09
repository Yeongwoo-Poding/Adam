package project.adam.entity.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;
import project.adam.entity.common.BaseTimeEntity;
import project.adam.entity.post.Post;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity implements Persistable<String> {

    @Id
    @Column(name = "member_id")
    private String id;

    @Column(unique = true)
    private String email;

    private String name;

    private String image;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @OneToMany(mappedBy = "writer")
    private List<Post> posts = new ArrayList<>();

    @Column(length = 16)
    private UUID token = null;

    public Member(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.authority = Authority.ROLE_USER;
        this.token = UUID.randomUUID();
    }

    public Member(String id, String email, String name, Authority authority) {
        this(id, email, name);
        this.authority = authority;
        this.token = UUID.randomUUID();
    }

    public UUID login() {
        this.token = UUID.randomUUID();
        return token;
    }

    public void setImage(String imageName) {
        this.image = imageName;
    }

    @Override
    public boolean isNew() {
        return getCreateDate() == null;
    }
}
