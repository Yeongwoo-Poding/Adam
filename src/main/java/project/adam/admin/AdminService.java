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
import project.adam.entity.reply.Reply;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.member.MemberRepository;
import project.adam.repository.post.PostRepository;
import project.adam.repository.reply.ReplyRepository;

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
    private final ReplyRepository replyRepository;

    @Transactional
    public Member ban(ReportContent content, int days) {
        ContentType contentType = content.getContentType();
        Long contentId = content.getContentId();
        em.remove(content);

        switch (contentType) {
            case POST:
                return banPost(postRepository.findById(contentId).orElseThrow(), days);
            case COMMENT:
                return banComment(commentRepository.findById(contentId).orElseThrow(), days);
            case REPLY:
                return banReply(replyRepository.findById(contentId).orElseThrow(), days);
            default:
                throw new NoSuchElementException();
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

    private Member banReply(Reply reply, int days) {
        reply.getReports().forEach(Report::check);
        replyRepository.remove(reply);
        return memberRepository.ban(reply.getWriter(), days);
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

    public ReportContentDetail getReportContentDetail(Long reportId) {
        ReportContent content = em.find(ReportContent.class, reportId);
        if (content == null) {
            throw new NoSuchElementException();
        }

        switch (content.getContentType()) {
            case POST:
                Post post = postRepository.findById(content.getContentId()).orElseThrow();
                return new ReportContentDetail(content, post.getTitle(), post.getBody());
            case COMMENT:
                Comment comment = commentRepository.findById(content.getContentId()).orElseThrow();
                return new ReportContentDetail(content, null, comment.getBody());
            case REPLY:
                Reply reply = replyRepository.findById(content.getContentId()).orElseThrow();
                return new ReportContentDetail(content, null, reply.getBody());
            default:
                throw new NoSuchElementException();
        }
    }

    @Transactional
    public void release(ReportContent content) {
        ContentType contentType = content.getContentType();
        Long contentId = content.getContentId();
        em.remove(content);

        switch (contentType) {
            case POST:
                releasePost(postRepository.findById(contentId).orElseThrow());
                return;
            case COMMENT:
                releaseComment(commentRepository.findById(contentId).orElseThrow());
                return;
            case REPLY:
                releaseReply(replyRepository.findById(contentId).orElseThrow());
                return;
            default:
                throw new NoSuchElementException();
        }
    }

    private void releasePost(Post post) {
        postRepository.release(post);
    }

    private void releaseComment(Comment comment) {
        commentRepository.release(comment);
    }

    private void releaseReply(Reply reply) {
        replyRepository.release(reply);
    }
}
