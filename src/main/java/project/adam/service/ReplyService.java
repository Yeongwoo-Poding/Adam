package project.adam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.common.Report;
import project.adam.entity.common.ReportContent;
import project.adam.entity.member.Member;
import project.adam.entity.reply.Reply;
import project.adam.entity.reply.ReplyReport;
import project.adam.exception.ApiException;
import project.adam.exception.ExceptionEnum;
import project.adam.repository.comment.CommentRepository;
import project.adam.repository.member.MemberRepository;
import project.adam.repository.reply.ReplyRepository;
import project.adam.security.SecurityUtils;
import project.adam.service.dto.reply.ReplyCreateServiceRequest;
import project.adam.service.dto.reply.ReplyReportServiceRequest;
import project.adam.service.dto.reply.ReplyUpdateServiceRequest;
import project.adam.utils.push.PushUtils;
import project.adam.utils.push.dto.PushRequest;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static project.adam.entity.common.ReportContent.ContentType.REPLY;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final EntityManager em;
    private final PushUtils pushUtils;

    @Transactional
    public Reply create(ReplyCreateServiceRequest request)  {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow();
        Reply createdReply = Reply.builder()
                .writer(member)
                .comment(commentRepository.findById(request.getCommentId()).orElseThrow())
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
            Member writer = reply.getPostWriter();
            if (writer.isAllowPostNotification()) {
                target.add(writer);
            }
        }

        if (needToPushCommentWriter(member, reply)) {
            Member writer = reply.getCommentWriter();
            if (writer.isAllowCommentNotification()) {
                target.add(writer);
            }
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
        validateReplyStatus(reply);
        return reply;
    }

    @Transactional
    public void update(ReplyUpdateServiceRequest request) {
        Reply reply = replyRepository.findById(request.getReplyId()).orElseThrow();
        validateReplyStatus(reply);
        authorization(reply.getWriter());

        reply.update(request.getBody());
    }

    @Transactional
    public void remove(Long replyId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow();
        validateReplyStatus(reply);
        authorization(reply.getWriter());

        replyRepository.remove(reply);
    }

    @Transactional
    public void report(ReplyReportServiceRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow();
        Reply reply = replyRepository.findById(request.getReplyId()).orElseThrow();
        if (request.getReportType() == null) {
            throw new ApiException(ExceptionEnum.INVALID_INPUT);
        }
        if (member.equals(reply.getWriter())) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }
        if (isReportExist(member, reply)) {
            throw new ApiException(ExceptionEnum.INVALID_REPORT);
        }

        ReplyReport.builder()
                .reply(reply)
                .member(member)
                .reportType(request.getReportType())
                .build();

        if (replyRepository.countReplyReport(reply) >= Report.HIDE_COUNT) {
            replyRepository.hide(reply);
            em.persist(new ReportContent(REPLY, reply.getId()));
        }
    }

    private void validateReplyStatus(Reply reply) {
        if (reply.getStatus().equals(ContentStatus.HIDDEN)) {
            throw new ApiException(ExceptionEnum.HIDDEN_CONTENT);
        }
        if (reply.getStatus().equals(ContentStatus.REMOVED)) {
            throw new NoSuchElementException();
        }
    }

    private boolean isReportExist(Member member, Reply reply) {
        return reply.getReports().stream()
                .anyMatch(replyReport -> replyReport.getMember().equals(member));
    }

    public void authorization(Member member) {
        Member loginMember = memberRepository.findByEmail(SecurityUtils.getCurrentMemberEmail()).orElseThrow();
        loginMember.authorization(member);
    }
}
