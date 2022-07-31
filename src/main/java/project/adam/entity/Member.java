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

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String uuid;

    private String nickname;

    @OneToMany(mappedBy = "writer")
    private List<Post> posts = new ArrayList<>();

    public Member(String uuid, String nickname) {
        this.uuid = uuid;
        this.nickname = nickname;
    }
}
