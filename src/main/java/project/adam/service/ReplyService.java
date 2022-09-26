package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.Report;
import project.adam.entity.member.Member;
import project.adam.entity.reply.Reply;
import project.adam.entity.reply.ReplyReport;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.fcm.FcmService;
import project.adam.fcm.dto.FcmPushRequest;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.reply.ReplyRepository;
import project.adam.service.dto.reply.ReplyCreateRequest;
import project.adam.service.dto.reply.ReplyReportRequest;
import project.adam.service.dto.reply.ReplyUpdateRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final FcmService fcmService;

    @Transactional
    public Reply create(Member member, ReplyCreateRequest request)  {
        Reply createdReply = Reply.builder()
                .writer(member)
                .comment(commentRepository.findById(request.commentId).orElseThrow())
                .body(request.getBody())
                .build();

        replyRepository.save(createdReply);

        FcmPushRequest fcmRequest = new FcmPushRequest(
                "대댓글이 달렸어요!",
                createdReply.getBody(),
                createdReply.getPost().getId());

        for (Member target : getTarget(member, createdReply)) {
            fcmService.pushTo(target, fcmRequest);
        }

        return createdReply;
    }

    private Set<Member> getTarget(Member member, Reply reply) {
        Set<Member> target = new HashSet<>();
        if(needToPushPostWriter(member, reply)) {
            target.add(reply.getPostWriter());
        }

        if (needToPushCommentWriter(member, reply)) {
            target.add(reply.getCommentWriter());
        }

        return target;
    }

    private boolean needToPushPostWriter(Member member, Reply reply) {
        return !member.equals(reply.getPostWriter());
    }

    private boolean needToPushCommentWriter(Member member, Reply reply) {
        return !(member.equals(reply.getCommentWriter()));
    }

    public Reply find(Long replyId) {
        validateReplyHidden(replyId);
        return replyRepository.findById(replyId).orElseThrow();
    }

    public List<Reply> findRepliesByComment(Comment comment) {
        return replyRepository.findRepliesByComment(comment);
    }

    @Transactional
    public void update(Reply reply, ReplyUpdateRequest request) {
        validateReplyHidden(reply.getId());
        reply.update(request.getBody());
    }

    @Transactional
    public void remove(Reply reply) {
        validateReplyHidden(reply.getId());
        replyRepository.delete(reply);
    }

    @Transactional
    public void report(Member member, Reply reply, ReplyReportRequest request) {
        if (isReportExist(member, reply)) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }

        ReplyReport.builder()
                .reply(reply)
                .member(member)
                .reportType(request.getReportType())
                .build();
    }

    private boolean isReportExist(Member member, Reply reply) {
        return reply.getReports().stream()
                .anyMatch(replyReport -> replyReport.getMember().equals(member));
    }

    private void validateReplyHidden(Long replyId) {
        if (replyRepository.countReplyReportById(replyId) >= Report.HIDE_COUNT) {
            throw new ApiException(ExceptionEnum.HIDDEN_CONTENT);
        }
    }
}
