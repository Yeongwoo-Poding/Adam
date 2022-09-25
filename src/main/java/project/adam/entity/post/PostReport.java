package project.adam.entity.post;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.common.Report;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("post")
public class PostReport extends Report {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public PostReport(Post post, Member member, ReportType reportType) {
        super(member, reportType);
        this.post = post;
        post.getReports().add(this);
    }
}
