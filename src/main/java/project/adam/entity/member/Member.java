package project.adam.entity.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;
import project.adam.entity.common.BaseTimeEntity;
import project.adam.entity.post.Post;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public Member(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.authority = Authority.ROLE_USER;
    }

    public Member(String id, String email, String name, Authority authority) {
        this(id, email, name);
        this.authority = authority;
    }

    public void setImage(String imageName) {
        this.image = imageName;
    }

    @Override
    public boolean isNew() {
        return getCreateDate() == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id.equals(member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void authorization(String memberId) {
        if (!memberId.equals(this.id)) {
            if (this.authority != Authority.ROLE_ADMIN) {
                throw new ApiException(ExceptionEnum.AUTHORIZATION_FAILED);
            }
        }
    }
}
