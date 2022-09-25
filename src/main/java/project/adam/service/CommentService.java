package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.comment.Comment;
import project.adam.entity.comment.CommentReport;
import project.adam.entity.member.Member;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.fcm.FcmService;
import project.adam.fcm.dto.FcmRequestBuilder;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.post.PostRepository;
import project.adam.repository.reply.ReplyRepository;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.comment.CommentReportRequest;
import project.adam.service.dto.comment.CommentUpdateRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final FcmService fcmService;

    @Value("${report.count}")
    private int reportCount;

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
        FcmRequestBuilder request = FcmRequestBuilder.builder()
                .member(comment.getPost().getWriter())
                .title(comment.getPost().getTitle() + "에 댓글이 달렸어요!")
                .body(comment.getBody())
                .postId(comment.getPost().getId())
                .build();
        fcmService.sendMessageTo(request);
    }

    private boolean isOthers(Member member, Comment comment) {
        return member != comment.getPost().getWriter();
    }

    public Comment find(Long commentId) {
        validateCommentHidden(commentId);
        return commentRepository.findById(commentId).orElseThrow();
    }

    public Slice<Comment> findByPost(Long postId, Pageable pageable) {
        return commentRepository.findRootCommentsByPost(postRepository.findById(postId).orElseThrow(), pageable);
    }

    @Transactional
    public void update(Comment comment, CommentUpdateRequest request) {
        validateCommentHidden(comment.getId());
        comment.update(request.getBody());
    }

    @Transactional
    public void remove(Comment comment) {
        validateCommentHidden(comment.getId());
        replyRepository.deleteAll(replyRepository.findRepliesByComment(comment));
        commentRepository.delete(comment);
    }

    @Transactional
    public void report(Member member, Comment comment, CommentReportRequest request) {
        if (isReportExist(member, comment)) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }

        CommentReport.builder()
                .comment(comment)
                .member(member)
                .reportType(request.getReportType())
                .build();
    }

    private boolean isReportExist(Member member, Comment comment) {
        return comment.getReports().stream()
                .anyMatch(commentReport -> commentReport.getMember().equals(member));
    }

    private void validateCommentHidden(Long commentId) {
        if (commentRepository.countCommentReportById(commentId) >= reportCount) {
            throw new ApiException(ExceptionEnum.HIDDEN_CONTENT);
        }
    }
}
