package project.adam.entity.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportContent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_content_id")
    private Long id;

    private ContentType contentType;

    private Long contentId;

    public ReportContent(ContentType contentType, Long contentId) {
        this.contentType = contentType;
        this.contentId = contentId;
    }

    public enum ContentType {
        POST, COMMENT
    }
}
