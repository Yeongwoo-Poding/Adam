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
import project.adam.entity.post.Post;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.member.MemberRepository;
import project.adam.repository.post.PostRepository;
import project.adam.security.SecurityUtils;
import project.adam.service.dto.comment.CommentCreateServiceRequest;
import project.adam.service.dto.comment.CommentReportServiceRequest;
import project.adam.service.dto.comment.CommentUpdateServiceRequest;
import project.adam.utils.push.PushUtils;
import project.adam.utils.push.dto.PushRequest;

import javax.persistence.EntityManager;
import java.util.NoSuchElementException;

import static project.adam.entity.common.ReportContent.ContentType.COMMENT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final EntityManager em;
    private final PushUtils pushUtils;

    @Transactional
    public Comment create(CommentCreateServiceRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow();
        Post post = postRepository.findById(request.getPostId()).orElseThrow();
        Comment parent = request.getParentId() == null ? null : commentRepository.findById(request.getParentId()).orElseThrow();
        validateComment(parent);

        Comment comment = Comment.builder()
                .writer(member)
                .post(post)
                .parent(parent)
                .body(request.getBody())
                .build();

        commentRepository.save(comment);

        sendPush(post, parent, comment);

        return comment;
    }

    private void validateComment(Comment parent) {
        // parent가 null이면 root comment
        // parent가 root이면 대댓글
        if (parent != null && !parent.isRoot()) {
            throw new ApiException(ExceptionEnum.INVALID_COMMENT);
        }
    }

    private void sendPushToPostWriter(Post post, Comment comment)  {
        if (post.getWriter().equals(comment.getWriter()) && post.getWriter().isAllowPostNotification()) {
            PushRequest pushRequest = new PushRequest(
                    comment.getPost().getTitle() + "에 댓글이 달렸어요!",
                    comment.getBody(),
                    comment.getPost().getId());
            pushUtils.pushTo(post.getWriter(), pushRequest);
        }
    }

    private void sendPush(Post post, Comment parent, Comment comment) {
        // post writer와 comment writer가 다르면 푸시
        // parent writer와 comment writer가 다르면 푸시, parent writer와 post writer가 다르면 푸시
        if (needToPushPost(post, comment)) {
            PushRequest postPushRequest = new PushRequest(
                    "게시글 " + post.getTitle() + "에 댓글이 달렸어요!",
                    comment.getBody(),
                    post.getId());
            pushUtils.pushTo(post.getWriter(), postPushRequest);
        }

        if (needToPushParent(parent, comment)) {
            PushRequest commentPushRequest = new PushRequest(
                    "댓글 " + parent.getBody() + "에 대댓글이 달렸어요!",
                    comment.getBody(),
                    post.getId());
            pushUtils.pushTo(post.getWriter(), commentPushRequest);
        }
    }

    private boolean needToPushPost(Post post, Comment comment) {
        return post.getWriter() != comment.getWriter() && post.getWriter().isAllowPostNotification();
    }

    private boolean needToPushParent(Comment parent, Comment comment) {
        if (parent == null) {
            return false;
        }
        boolean isParentOtherComment = parent.getWriter().equals(comment.getWriter());
        boolean isParentAlreadySendPush = needToPushPost(parent.getPost(), comment) && parent.getPost().getWriter().equals(parent.getWriter());
        boolean allowCommentNotification = parent.getWriter().isAllowCommentNotification();
        return  isParentOtherComment && !isParentAlreadySendPush && allowCommentNotification;
    }

    public Comment find(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        validateCommentStatus(comment);
        return comment;
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
