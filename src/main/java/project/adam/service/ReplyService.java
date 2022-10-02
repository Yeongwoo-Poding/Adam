package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.common.Report;
import project.adam.entity.common.ReportType;
import project.adam.entity.member.Member;
import project.adam.entity.reply.Reply;
import project.adam.entity.reply.ReplyReport;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.reply.ReplyRepository;
import project.adam.service.dto.reply.ReplyCreateRequest;
import project.adam.service.dto.reply.ReplyUpdateRequest;
import project.adam.utils.push.PushUtils;
import project.adam.utils.push.dto.PushRequest;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final PushUtils pushUtils;

    @Transactional
    public Reply create(Member member, ReplyCreateRequest request)  {
        Reply createdReply = Reply.builder()
                .writer(member)
                .comment(commentRepository.findById(request.commentId).orElseThrow())
                .body(request.getBody())
                .build();

        replyRepository.save(createdReply);

        PushRequest pushRequest = new PushRequest(
                "대댓글이 달렸어요!",
                createdReply.getBody(),
                createdReply.getPost().getId());

        for (Member target : getTarget(member, createdReply)) {
            pushUtils.pushTo(target, pushRequest);
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
        Reply reply = replyRepository.findById(replyId).orElseThrow();
        validateReply(reply);
        return reply;
    }

    public List<Reply> findByComment(Comment comment) {
        return comment.getReplies();
    }

    @Transactional
    public void update(Reply reply, ReplyUpdateRequest request) {
        validateReply(reply);
        reply.update(request.getBody());
    }

    @Transactional
    public void remove(Reply reply) {
        validateReply(reply);
        replyRepository.remove(reply);
    }

    @Transactional
    public void report(Member member, Reply reply, ReportType type) {
        if (member.equals(reply.getWriter())) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }
        if (isReportExist(member, reply)) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }

        ReplyReport.builder()
                .reply(reply)
                .member(member)
                .reportType(type)
                .build();

        if (replyRepository.countReplyReport(reply) >= Report.HIDE_COUNT) {
            replyRepository.hide(reply);
        }
    }

    private boolean isReportExist(Member member, Reply reply) {
        return reply.getReports().stream()
                .anyMatch(replyReport -> replyReport.getMember().equals(member));
    }

    private void validateReply(Reply reply) {
        if (reply.getStatus().equals(ContentStatus.HIDDEN)) {
            throw new ApiException(ExceptionEnum.HIDDEN_CONTENT);
        }
        if (reply.getStatus().equals(ContentStatus.REMOVED)) {
            throw new NoSuchElementException();
        }
    }
}
