package project.adam.entity.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.member.Member;

import javax.persistence.*;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Report {

    public static final int HIDE_COUNT = 1;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    private boolean isChecked;

    public Report(Member member, ReportType reportType) {
        this.member = member;
        this.reportType = reportType;
        this.isChecked = false;
    }

    public void check() {
        this.isChecked = true;
    }
}
