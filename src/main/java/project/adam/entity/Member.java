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
public class Member {

    @Id
    @Column(name = "member_id")
    private String id;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Privilege privilege;

    @OneToMany(mappedBy = "writer")
    private List<Post> posts = new ArrayList<>();

    public Member(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
        this.privilege = Privilege.USER;
    }

    public Member(String id, String nickname, Privilege privilege) {
        this(id, nickname);
        this.privilege = privilege;
    }
}
