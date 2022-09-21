package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.comment.Comment;
import project.adam.entity.member.Member;
import project.adam.entity.post.Post;
import project.adam.entity.reply.Reply;
import project.adam.entity.reply.ReplyReport;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.fcm.FcmService;
import project.adam.fcm.dto.FcmRequestBuilder;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.reply.ReplyRepository;
import project.adam.service.dto.reply.ReplyCreateRequest;
import project.adam.service.dto.reply.ReplyReportRequest;
import project.adam.service.dto.reply.ReplyUpdateRequest;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final FcmService fcmService;

    @Value("${report.count}")
    private int reportCount;

    @Transactional
    public Reply create(Member member, ReplyCreateRequest request) throws IOException {
        Reply createdReply = Reply.builder()
                .writer(member)
                .comment(commentRepository.findById(request.commentId).orElseThrow())
                .body(request.getBody())
                .build();

        if (isOthersPost(member, createdReply.getPost())) {
            sendPushToPostWriter(createdReply);
        } else if (isOthersComment(member, createdReply.getComment())) {
            sendPushToCommentWriter(createdReply);
        }

        return createdReply;
    }

    private void sendPushToCommentWriter(Reply reply) throws IOException {
        FcmRequestBuilder request = FcmRequestBuilder.builder()
                .member(reply.getComment().getWriter())
                .title(reply.getComment().getBody() + "에 대댓글이 달렸어요!")
                .body(reply.getBody())
                .postId(reply.getPost().getId())
                .build();

        fcmService.sendMessageTo(request);
    }

    private void sendPushToPostWriter(Reply reply) throws IOException {
        FcmRequestBuilder request = FcmRequestBuilder.builder()
                .member(reply.getPost().getWriter())
                .title(reply.getPost().getTitle() + "에 대댓글이 달렸어요!")
                .body(reply.getBody())
                .postId(reply.getPost().getId())
                .build();

        fcmService.sendMessageTo(request);
    }

    private boolean isOthersComment(Member member, Comment comment) {
        return member != comment.getWriter();
    }

    private boolean isOthersPost(Member member, Post post) {
        return member != post.getWriter();
    }

    public Reply find(Long replyId) {
        validateReplyHidden(replyId);
        return replyRepository.findById(replyId).orElseThrow();
    }

    public Slice<Reply> findAllByComment(Long commentId, Pageable pageable) {
        return replyRepository.findRepliesByCommentId(commentId, pageable);
    }

    @Transactional
    public void update(Reply reply, ReplyUpdateRequest request) {
        validateReplyHidden(reply.getId());
        reply.update(request.getBody());
    }

    @Transactional
    public void delete(Reply reply) {
        validateReplyHidden(reply.getId());
        replyRepository.delete(reply);
    }

    @Transactional
    public void report(Member member, Reply reply, ReplyReportRequest request) {
        boolean isReportExist = reply.getReports().stream()
                .anyMatch(replyReport -> replyReport.getMember().equals(member));

        if (isReportExist) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }

        ReplyReport.builder()
                .reply(reply)
                .member(member)
                .reportType(request.getReport())
                .build();
    }

    private void validateReplyHidden(Long replyId) {
        if (replyRepository.countReplyReportById(replyId) >= reportCount) {
            throw new ApiException(ExceptionEnum.HIDDEN_CONTENT);
        }
    }
}
