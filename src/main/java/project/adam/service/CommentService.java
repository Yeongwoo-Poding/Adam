package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.comment.Comment;
import project.adam.entity.comment.CommentReport;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.common.Report;
import project.adam.entity.common.ReportContent;
import project.adam.entity.member.Member;
import project.adam.entity.reply.Reply;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.member.MemberRepository;
import project.adam.repository.post.PostRepository;
import project.adam.repository.reply.ReplyRepository;
import project.adam.security.SecurityUtils;
import project.adam.service.dto.comment.CommentCreateServiceRequest;
import project.adam.service.dto.comment.CommentReportServiceRequest;
import project.adam.service.dto.comment.CommentUpdateServiceRequest;
import project.adam.utils.push.PushUtils;
import project.adam.utils.push.dto.PushRequest;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;

import static project.adam.entity.common.ReportContent.ContentType.COMMENT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final EntityManager em;
    private final PushUtils pushUtils;

    @Transactional
    public Comment create(CommentCreateServiceRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow();
        Comment comment = Comment.builder()
                        .writer(member)
                        .post(postRepository.findById(request.getPostId()).orElseThrow())
                        .body(request.getBody())
                        .build();

        commentRepository.save(comment);

        if (needToPushPostWriter(member, comment)) {
            sendPushToPostWriter(comment);
        }

        return comment;
    }

    private void sendPushToPostWriter(Comment comment)  {
        PushRequest pushRequest = new PushRequest(
                comment.getPost().getTitle() + "에 댓글이 달렸어요!",
                comment.getBody(),
                comment.getPost().getId());

        Member writer = comment.getPost().getWriter();
        if (writer.isAllowPostNotification()) {
            pushUtils.pushTo(writer, pushRequest);
        }
    }

    private boolean needToPushPostWriter(Member member, Comment comment) {
        return !member.equals(comment.getPost().getWriter());
    }

    public Comment find(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        validateCommentStatus(comment);
        return comment;
    }

    public List<Reply> findReplies(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        validateCommentStatus(comment);
        return replyRepository.findByComment(comment);
    }

    @Transactional
    public void update(CommentUpdateServiceRequest request) {
        Comment comment = commentRepository.findById(request.getCommentId()).orElseThrow();
        validateCommentStatus(comment);
        authorization(comment.getWriter());

        comment.update(request.getBody());
    }

    @Transactional
    public void remove(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        validateCommentStatus(comment);
        authorization(comment.getWriter());

        commentRepository.remove(comment);
    }

    @Transactional
    public void report(CommentReportServiceRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow();
        Comment comment = commentRepository.findById(request.getCommentId()).orElseThrow();
        if (request.getReportType() == null) {
            throw new ApiException(ExceptionEnum.INVALID_INPUT);
        }
        if (member.equals(comment.getWriter())) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }
        if (isReportExist(member, comment)) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }

        CommentReport.builder()
                .comment(comment)
                .member(member)
                .reportType(request.getReportType())
                .build();

        if (commentRepository.countCommentReport(comment) >= Report.HIDE_COUNT) {
            commentRepository.hide(comment);
            em.persist(new ReportContent(COMMENT, comment.getId()));
        }
    }

    private void validateCommentStatus(Comment comment) {
        if (comment.getStatus().equals(ContentStatus.HIDDEN)) {
            throw new ApiException(ExceptionEnum.HIDDEN_CONTENT);
        }
        if (comment.getStatus().equals(ContentStatus.REMOVED)) {
            throw new NoSuchElementException();
        }
    }

    private boolean isReportExist(Member member, Comment comment) {
        return comment.getReports().stream()
                .anyMatch(commentReport -> commentReport.getMember().equals(member));
    }

    public void authorization(Member member) {
        Member loginMember = memberRepository.findByEmail(SecurityUtils.getCurrentMemberEmail()).orElseThrow();
        loginMember.authorization(member);
    }
}
