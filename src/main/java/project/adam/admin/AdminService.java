package project.adam.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.admin.dto.ReportContentDetail;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.Report;
import project.adam.entity.common.ReportContent;
import project.adam.entity.common.ReportContent.ContentType;
import project.adam.entity.member.Member;
import project.adam.entity.post.Post;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.member.MemberRepository;
import project.adam.repository.post.PostRepository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final EntityManager em;

    @Transactional
    public Member ban(ReportContent content, int days) {
        ContentType contentType = content.getContentType();
        if (contentType == null) {
            throw new NoSuchElementException();
        }
        Long contentId = content.getContentId();
        em.remove(content);

        if (contentType.equals(ContentType.POST)) {
            return banPost(postRepository.findById(contentId).orElseThrow(), days);
        } else {
            return banComment(commentRepository.findById(contentId).orElseThrow(), days);
        }
    }

    private Member banPost(Post post, int days) {
        post.getReports().forEach(Report::check);
        postRepository.remove(post);
        return memberRepository.ban(post.getWriter(), days);
    }

    private Member banComment(Comment comment, int days) {
        comment.getReports().forEach(Report::check);
        commentRepository.remove(comment);
        return memberRepository.ban(comment.getWriter(), days);
    }

    @Transactional
    public void release(ReportContent content) {
        ContentType contentType = content.getContentType();
        if (contentType == null) {
            throw new NoSuchElementException();
        }
        Long contentId = content.getContentId();
        em.remove(content);

        if (contentType.equals(ContentType.POST)) {
            releasePost(postRepository.findById(contentId).orElseThrow());
        } else {
            releaseComment(commentRepository.findById(contentId).orElseThrow());
        }
    }

    private void releasePost(Post post) {
        postRepository.release(post);
    }

    private void releaseComment(Comment comment) {
        commentRepository.release(comment);
    }

    public List<ReportContent> findReportContents() {
        return em.createQuery("select rc from ReportContent rc", ReportContent.class)
                .getResultList();
    }

    public ReportContent findReportContent(Long reportId) {
        ReportContent reportContent = em.find(ReportContent.class, reportId);
        if (reportContent == null) {
            throw new NoSuchElementException();
        }
        return reportContent;
    }

    public ReportContentDetail findReportContentDetail(Long reportId) {
        ReportContent content = em.find(ReportContent.class, reportId);
        if (content == null || content.getContentType() == null) {
            throw new NoSuchElementException();
        }

        if (content.getContentType().equals(ContentType.POST)) {
            Post post = postRepository.findById(content.getContentId()).orElseThrow();
            return new ReportContentDetail(content, post.getTitle(), post.getBody());
        } else {
            Comment comment = commentRepository.findById(content.getContentId()).orElseThrow();
            return new ReportContentDetail(content, null, comment.getBody());
        }
    }
}
