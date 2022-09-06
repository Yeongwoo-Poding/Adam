package project.adam.entity.comment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentReport {

    @Id @GeneratedValue
    @Column(name = "comment_report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    public CommentReport(Comment comment, Member member, ReportType reportType) {
        this.comment = comment;
        this.member = member;
        this.reportType = reportType;
        comment.getReports().add(this);
    }
}
