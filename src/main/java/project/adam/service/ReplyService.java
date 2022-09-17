package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ReportType;
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

import java.io.IOException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final FcmService fcmService;

    @Value("${report.hiddenCount}")
    private int reportHiddenCount;

    @Transactional
    public Reply create(Member member, ReplyCreateRequest replyDto) throws IOException {
        Reply createdReply = replyRepository.save(
                new Reply(
                        member,
                        commentRepository.findById(replyDto.commentId).orElseThrow(),
                        replyDto.getBody()));

        if (isOthersPost(member, createdReply.getPost())) {
            sendPushToPostWriter(createdReply);
        } else if (isOthersComment(member, createdReply.getComment())) {
            sendPushToCommentWriter(createdReply);
        }

        return createdReply;
    }

    private void sendPushToCommentWriter(Reply createdReply) throws IOException {
        FcmRequestBuilder request = FcmRequestBuilder.builder()
                .member(createdReply.getComment().getWriter())
                .title(createdReply.getComment().getBody() + "에 대댓글이 달렸어요!")
                .body(createdReply.getBody())
                .postId(createdReply.getPost().getId())
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
        return replyRepository.findAllByCommentId(commentId, pageable);
    }

    @Transactional
    public void update(Reply reply, String body) {
        validateReplyHidden(reply.getId());
        reply.update(body);
    }

    @Transactional
    public void delete(Reply reply) {
        validateReplyHidden(reply.getId());
        replyRepository.delete(reply);
    }

    @Transactional
    public void report(Member member, Reply reply, ReportType reportType) {
        boolean isReportExist = reply.getReports().stream()
                .anyMatch(replyReport -> replyReport.getMember().equals(member));

        if (isReportExist) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }

        new ReplyReport(reply, member, reportType);
    }

    private void validateReplyHidden(Long replyId) {
        if (replyRepository.countReplyReportById(replyId) >= reportHiddenCount) {
            throw new ApiException(ExceptionEnum.HIDDEN_CONTENT);
        }
    }
}
