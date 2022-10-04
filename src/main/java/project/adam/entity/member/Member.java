package project.adam.entity.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;
import project.adam.entity.common.BaseTimeEntity;
import project.adam.entity.post.Post;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;

import javax.persistence.*;
import java.time.LocalDate;
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

    @Enumerated(EnumType.STRING)
    private MemberStatus status = MemberStatus.LOGOUT;

    @Enumerated(EnumType.STRING)
    private MemberSession session;

    private LocalDate suspendedDate;

    private String deviceToken;

    private boolean allowPostNotification = true;

    private boolean allowCommentNotification = true;

    @OneToMany(mappedBy = "writer")
    private List<Post> posts = new ArrayList<>();

    @Builder
    public Member(String id, String email, String name, MemberSession session) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.authority = Authority.ROLE_USER;
        this.session = session;
        this.suspendedDate = LocalDate.now();
    }

    public Member(String id, String email, String name, MemberSession session, Authority authority) {
        this(id, email, name, session);
        this.authority = authority;
    }

    public String getName() {
        return this.getStatus() == MemberStatus.WITHDRAWN ? "탈퇴한 사용자" : this.name;
    }

    public void setImage(String imageName) {
        this.image = imageName;
    }

    public void login(String deviceToken) {
        this.deviceToken = deviceToken;
        this.status = MemberStatus.LOGIN;
    }

    public void logout() {
        this.status = MemberStatus.LOGOUT;
    }

    public MemberStatus getStatus() {
        if (this.status == MemberStatus.SUSPENDED && LocalDate.now().isAfter(suspendedDate)) {
            this.status = MemberStatus.LOGOUT;
        }
        return status;
    }

    @Override
    public boolean isNew() {
        return this.getCreatedDate() == null;
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

    public void authorization(Member member) {
        if (!member.equals(this)) {
            if (this.authority != Authority.ROLE_ADMIN) {
                throw new ApiException(ExceptionEnum.AUTHORIZATION_FAILED);
            }
        }
    }

    public void ban(int days) {
        this.status = MemberStatus.SUSPENDED;
        this.suspendedDate = LocalDate.now().plusDays(days);
    }

    public void update(String name, MemberSession session) {
        this.name = name;
        this.session = session;
    }

    public void togglePostNotification() {
        this.allowPostNotification = !this.allowPostNotification;
    }

    public void toggleCommentNotification() {
        this.allowCommentNotification = !this.isAllowCommentNotification();
    }
}
