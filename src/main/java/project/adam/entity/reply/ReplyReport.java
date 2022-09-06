package project.adam.entity.reply;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyReport {

    @Id @GeneratedValue
    @Column(name = "reply_report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Reply reply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    public ReplyReport(Reply reply, Member member, ReportType reportType) {
        this.reply = reply;
        this.member = member;
        this.reportType = reportType;
    }
}
