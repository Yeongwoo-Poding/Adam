package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.comment.Comment;
import project.adam.entity.comment.CommentReport;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.common.Report;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.post.PostRepository;
import project.adam.repository.reply.ReplyRepository;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentUpdateRequest;
import project.adam.utils.push.PushUtils;
import project.adam.utils.push.dto.PushRequest;

import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final PushUtils pushUtils;

    @Transactional
    public Comment create(Member member, CommentCreateRequest request)  {
        Comment createdComment = Comment.builder()
                        .writer(member)
                        .post(postRepository.findById(request.postId).orElseThrow())
                        .body(request.getBody())
                        .build();

        commentRepository.save(createdComment);

        if (isOthers(member, createdComment)) {
            sendPushToPostWriter(createdComment);
        }

        return createdComment;
    }

    private void sendPushToPostWriter(Comment comment)  {
        PushRequest pushRequest = new PushRequest(
                comment.getPost().getTitle() + "에 댓글이 달렸어요!",
                comment.getBody(),
                comment.getPost().getId());
        pushUtils.pushTo(comment.getPost().getWriter(), pushRequest);
    }

    private boolean isOthers(Member member, Comment comment) {
        return member != comment.getPost().getWriter();
    }

    public Comment find(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        validateComment(comment);
        return comment;
    }

    public Slice<Comment> findByPost(Long postId, Pageable pageable) {
        return commentRepository.findByPost(postRepository.findById(postId).orElseThrow(), pageable);
    }

    @Transactional
    public void update(Comment comment, CommentUpdateRequest request) {
        validateComment(comment);
        comment.update(request.getBody());
    }

    @Transactional
    public void remove(Comment comment) {
        validateComment(comment);
//        replyRepository.removeAllByComment(comment);
        commentRepository.remove(comment);
    }

    @Transactional
    public void report(Member member, Comment comment, ReportType type) {
        if (member.equals(comment.getWriter())) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }
        if (isReportExist(member, comment)) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }

        CommentReport.builder()
                .comment(comment)
                .member(member)
                .reportType(type)
                .build();

        if (commentRepository.countCommentReport(comment) >= Report.HIDE_COUNT) {
            commentRepository.hide(comment);
        }
    }

    private boolean isReportExist(Member member, Comment comment) {
        return comment.getReports().stream()
                .anyMatch(commentReport -> commentReport.getMember().equals(member));
    }

    private void validateComment(Comment comment) {
        if (comment.getStatus().equals(ContentStatus.HIDDEN)) {
            throw new ApiException(ExceptionEnum.HIDDEN_CONTENT);
        }
        if (comment.getStatus().equals(ContentStatus.REMOVED)) {
            throw new NoSuchElementException();
        }
    }
}
