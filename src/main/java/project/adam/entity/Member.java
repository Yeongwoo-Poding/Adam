package project.adam.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;
import project.adam.exception.ApiException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static project.adam.exception.ExceptionEnum.AUTHORIZATION_FAILED;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity implements Persistable<String> {

    @Id
    @Column(name = "member_id")
    private String id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Privilege privilege;

    @OneToMany(mappedBy = "writer")
    private List<Post> posts = new ArrayList<>();

    private String sessionId = null;

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
        this.privilege = Privilege.USER;
    }

    public Member(String id, String name, Privilege privilege) {
        this(id, name);
        this.privilege = privilege;
    }

    public void authorization(Privilege privilege) {
        if (this.privilege.value < privilege.value) {
            throw new ApiException(AUTHORIZATION_FAILED);
        }
    }

    public String login() {
        this.sessionId = UUID.randomUUID().toString();
        return sessionId;
    }

    @Override
    public boolean isNew() {
        return getCreateDate() == null;
    }
}
